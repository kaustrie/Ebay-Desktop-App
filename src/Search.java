import java.util.LinkedList;
import java.util.Properties;
import java.io.*;
import java.net.*;

import org.w3c.dom.*;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;


public class Search {

	

	/**
	 * This variable stores the location of the config file, containing
	 * the properties that need to be set for this code to work.
	 */
	private static final String configFileLocation = "config.properties";
	/**
	 * This variable stores the location of the XML, containing
	 * the XML template needed for this call.
	 */
	private static final String xmlFileLocation = "searchrequest.xml";
	/**
	 * This variable stores the location of the local copy of the
	 * XML Category Tree
	 */
	private static final String searchTreeLocation = "SearchTree.xml";


	//Global Veriables
	private static String appID, serverUrl, userToken, compatLevel, siteID, verb, keyword;
	private static String catID1;
	private static String catID2;
	
	/**
	 * The Document storing the local Category Tree, local to avoid
	 * having to parse more than once
	 */
	private static Document searchTree;

	/**
	 * Main Entry point for program
	 */
	Search(String keywrd, String id1, String id2)
	{
		keyword = keywrd;
		catID1 = id1;
		catID2 = id2;
		
		//Load the properties file into a Properties object
		Properties config = new Properties();
		try
		{
			config.load(new FileInputStream(configFileLocation));
		}
		catch (Exception e)
		{
			System.out.println("ERROR: Could not open properties file\n:" + e.toString());
			return;
		}

		//Get all the properties into local strings
		
		appID = config.getProperty("AppID");
		serverUrl = config.getProperty("ServerUrl");
		siteID = "EBAY-US";//= config.getProperty("siteid");
		//SET THE NAME OF THE CALL BEING MADE!
		verb = "findItemsAdvanced";
		getEntireCategoryTree();
		
		

		//Get the categoires tag
		Node categories = searchTree.getElementsByTagName("CategoryArray").item(0);
		//output the top-level categories

		/*//ask user to kkeep selecting categories until they select a leaf node
        System.out.print("\nEnter Category ID: ");
        String catIdSelected = readLine();
        while(isCategoryLeafNode(categories, catIdSelected) == false)
        {
            //show subcategories of category selected
            outputSubCategories(categories, catIdSelected);
            System.out.print("\nEnter SubCategory ID: ");
            catIdSelected = readLine();
        }


		System.out.println("You may submit to this category: " + catIdSelected);
		 */
	}

	

	/**
	 * Downloads the entire category tree and saves it locally
	 * @return True if completed successfully, false if errors
	 */
	private static boolean getEntireCategoryTree()
	{
		try{
			//Create a HttpsURLConnection with the correct server
			URL server = new URL(serverUrl);
			HttpsURLConnection connection = (HttpsURLConnection) (server.openConnection());

			//set the connection to do both input and output. Request Method = POST
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			//Add the necessary headers to the connection
			addeBayHTTPHeaders(connection, appID,verb,siteID);

			//Send the request for the entire tree by setting ViewAllNodes = true and DetailLevel=ReturnAll
			searchTree = SendRequest(connection, userToken, "ReturnAll", siteID, verb, true);

			//Save Document to a file
			DOMSource source = new DOMSource(searchTree);
			StreamResult result = new StreamResult(new FileOutputStream(searchTreeLocation));
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.transform(source, result);

		}
		catch(Exception e)
		{
			System.out.println("ERROR - GetEntireGategory: " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}



	/**
	 * Sends the request to the Server and returns the Document (XML)
	 * that is returned as the response.
	 * @param connection The HttpsURLConnection object to be used to execute the request
	 * @param requestToken The Token of the eBay user who is making the call.
	 * @param detailLevel Controls the amount or level of data returned ("0" is default)
	 * @param siteID Indicates the eBay site associated with the call (0 = US, 2 = Canada, 3 = UK, ...)
	 * @param verb The function being called (e.g. "GeteBayOfficialTime")
	 * @param viewAllNodes If true all nodes are returned, if false only leaf nodes returned
	 * @return The Document (Xml) returned from the server after the request was made.
	 */
	private static Document SendRequest(HttpsURLConnection connection, String requestToken,
			 String siteID, String verb, String entriesPerPage, boolean descriptionSearch)
	{

		try
		{
			//Get the XML file into a Document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = factory.newDocumentBuilder();
			Document xmlDoc = docBuild.parse(new File(xmlFileLocation));

			//Set the values of the nodes

			if(descriptionSearch)
				xmlDoc.getElementsByTagName("descriptionSearch").item(0).getChildNodes().item(0).setNodeValue("true");
			else
				xmlDoc.getElementsByTagName("descriptionSearch").item(0).getChildNodes().item(0).setNodeValue("false");
			
			//xmlDoc.getElementsByTagName("pagination.entriesPerPage").item(0).getChildNodes().item(0).setNodeValue(entriesPerPage);

			//  xmlDoc.getElementsByTagName("CategorySiteID").item(0).getChildNodes().item(0).setNodeValue(siteID);
			/*
			xmlDoc.createElement("categoryId").setNodeValue(catID1);
			xmlDoc.createElement("categoryId").setNodeValue(catID2);
			xmlDoc.createElement("keywords").setNodeValue(keyword);*/
			
			//Get the output stream of the connection
			OutputStream out = connection.getOutputStream();

			//Transform and write the Document to the stream
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer tr = tf.newTransformer();
			Source input = new DOMSource(xmlDoc);
			Result output = new StreamResult(out);
			tr.transform(input, output);
			out.flush();
			out.close();

			//Get the Input stream for the response
			InputStream in = connection.getInputStream();
			//Get the stream into a Document object
			Document response = docBuild.parse(in);
			//close the input stream and connection
			in.close();
			connection.disconnect();

			//return the response XML Document
			return response;



		}
		catch(IOException e)
		{
			System.out.println("SendRequest IO Error: " + e.toString());
			e.printStackTrace();
			return null;
		}
		catch(Exception e)
		{
			System.out.println("Error Sending Request: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Adds the necessary headers to the HttpsURLConnection passed in
	 * order for the call to be successful
	 * @param connection The HttpsURLConnection to add the headers to
	 * @param devID Developer ID, as registered with the Developer's Program.
	 * @param appID Application ID, as registered with the Developer's Program.
	 * @param certID Certificate ID, as registered with the Developer's Program.
	 * @param compatLevel Regulates versioning of the XML interface for the API.
	 * @param verb Name of the function being called e.g. "GetItem"
	 * @param siteID The Id of the eBay site the call should be executed on
	 */
	private static void addeBayHTTPHeaders(HttpsURLConnection connection,
			String appID, String verb, String siteID)
	{
		

		// Add the Developer Name, Application Name, and Certification Name Headers
		connection.addRequestProperty("X-EBAY-SOA-SECURITY-APPNAME", appID);
		
		connection.addRequestProperty("X-EBAY-SOA-SERVICE-NAME", "FindingService");

		// Add the API verb Header
		connection.addRequestProperty("X-EBAY-SOA-OPERATION-NAME", verb);

		// Add the Site Id Header
		connection.addRequestProperty("X-EBAY-SOA-GLOBAL-ID", siteID);
		
		connection.addRequestProperty("X-EBAY-SOA-REQUEST-DATA-FORMAT", "XML");

		// Add the Content-Type Header
		connection.addRequestProperty("Content-Type", "text/xml");
	}
	


}
