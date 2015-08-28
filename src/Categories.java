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


public class Categories {
	private  LinkedList<Category> categories = new LinkedList<Category>();;
	private  LinkedList<Category> childCategories;
	public LinkedList<Category> getCategories(){ return categories;}

	/**
	 * This variable stores the location of the config file, containing
	 * the properties that need to be set for this code to work.
	 */
	private static final String configFileLocation = "config.properties";
	/**
	 * This variable stores the location of the XML, containing
	 * the XML template needed for this call.
	 */
	private static final String xmlFileLocation = "request.xml";
	/**
	 * This variable stores the location of the local copy of the
	 * XML Category Tree
	 */
	private static final String catTreeLocation = "CatTree.xml";
	/**
	 * This variable stores the location of the file containing
     the version of the current local category tree
	 */
	private static final String catTreeVersionLocation = "CatTreeVersion.txt";


	//Global Veriables
	private static String devID, appID, certID, serverUrl, userToken, compatLevel, siteID, verb;
	/**
	 * The Document storing the local Category Tree, local to avoid
	 * having to parse more than once
	 */
	private static Document catTree;

	/**
	 * Main Entry point for program
	 */
	Categories()
	{
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
		devID = config.getProperty("DevID");
		appID = config.getProperty("AppID");
		certID = config.getProperty("CertID");
		serverUrl = config.getProperty("ServerUrl");
		userToken = config.getProperty("UserToken");
		//set other variables needed for call
		compatLevel = config.getProperty("version");
		siteID = config.getProperty("siteid");
		//SET THE NAME OF THE CALL BEING MADE!
		verb = "GetCategories";


		//Make sure CatTree is up to date, exit if problems
		if(!checkCategoryTreeVersion())
			return;

		//if cat tree was not loaded during checking, load now
		if(catTree == null)
		{
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuild = factory.newDocumentBuilder();
				catTree = docBuild.parse(new File(catTreeLocation));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		}

		//Get the categoires tag
		Node categories = catTree.getElementsByTagName("CategoryArray").item(0);
		//output the top-level categories
		getMainCategories(categories);

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
	 * Given the categoryID and  list of categories. This method finds
	 * the category and returns true if it is a leaf node and false
	 * otherwise.
	 * @param categories List of categories to search
	 * @param catID The CategoryID to look for
	 * @return True if category is a leaf node
	 */
	public static boolean isCategoryLeafNode(Node categories, String catID)
	{
		String thisCatID = "";
		String thisLeafCat = "";
		//Go thorugh each category
		for (int i = 0; i < categories.getChildNodes().getLength(); i++)
		{
			//make sure it is an element
			if(categories.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				for (int j = 0; j < categories.getChildNodes().item(i).getChildNodes().getLength(); j++)
				{
					//Go through each of the elements and emember the id and isLeaf
					Node thisChild = categories.getChildNodes().item(i).getChildNodes().item(j);
					if(thisChild.getNodeName() == "CategoryID")
						thisCatID = thisChild.getChildNodes().item(0).getNodeValue();
					else if(thisChild.getNodeName() == "LeafCategory")
						thisLeafCat = thisChild.getChildNodes().item(0).getNodeValue();
				}
				//if last cat was the one we are looking for output result
				if(catID.equals(thisCatID))
				{
					if(thisLeafCat.equals("1") || thisLeafCat.equals("true"))
						return true;
					else
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Looks through the given list of categories to find the subcategoires
	 * of the parent category specified and output them to the console.
	 * @param categories Ths list of categories to search through
	 * @param parentCategoryID The Category to get the children of
	 */
	public void getSubCategories(String parentCategoryID)
	{
		Node categoryArray = catTree.getElementsByTagName("CategoryArray").item(0);
		childCategories = new LinkedList<Category>();
		String catID = "";
		String parentID = "";
		String name = "";
		//Go thorugh each category
		for (int i = 0; i < categoryArray.getChildNodes().getLength(); i++)
		{
			//make sure it is an element
			if(categoryArray.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				for (int j = 0; j < categoryArray.getChildNodes().item(i).getChildNodes().getLength(); j++)
				{
					//Go through each of the elements and remember the catID, parentID and name
					Node thisChild = categoryArray.getChildNodes().item(i).getChildNodes().item(j);
					if(thisChild.getNodeName() == "CategoryID")
						catID = thisChild.getChildNodes().item(0).getNodeValue();
					else if(thisChild.getNodeName() == "CategoryParentID")
						parentID = thisChild.getChildNodes().item(0).getNodeValue();
					else if(thisChild.getNodeName() == "CategoryName")
						name = thisChild.getChildNodes().item(0).getNodeValue();
				}
				//if this has the parent cateory we are looking for, output it
				if(parentCategoryID.equals(parentID) && name != "")
					childCategories.add(new Category(Integer.parseInt(catID), name));	
			}
		}
		System.out.println(childCategories.size());
	}

	/**
	 * Output to the console the top level categories from the category list
	 * @param categories The list of categories to look through
	 */
	private void getMainCategories(Node categoryArray)
	{
		String catID = "";
		String parentID = "";
		String name = "";
		//Go thorugh each category
		for (int i = 0; i < categoryArray.getChildNodes().getLength(); i++)
		{
			//make sure it is an element
			if(categoryArray.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				//go through each element and remember the catID, parentID and name
				for (int j = 0; j < categoryArray.getChildNodes().item(i).getChildNodes().getLength(); j++)
				{
					//Go through each of the elements and output the ones required
					Node thisChild = categoryArray.getChildNodes().item(i).getChildNodes().item(j);
					if(thisChild.getNodeName() == "CategoryID")
						catID = thisChild.getChildNodes().item(0).getNodeValue();
					else if(thisChild.getNodeName() == "CategoryParentID")
						parentID = thisChild.getChildNodes().item(0).getNodeValue();
					else if(thisChild.getNodeName() == "CategoryName")
						name = thisChild.getChildNodes().item(0).getNodeValue();
				}
				//if catID=parentID then it is a top-level category - output it
				if(catID.equals(parentID) && name != ""){
					categories.add(new Category(Integer.parseInt(parentID), name));
					System.out.println(categories.size());
				}
			}
		}
	}



	/**
	 * Returns the version number of the Category Tree currently stored locally
	 * @return The Version number of the local Category Tree
	 */
	private static String getLocalVersionNumber()
	{
		try
		{
			//open file containing verison number and return first line
			FileReader fr = new FileReader(catTreeVersionLocation);
			BufferedReader br = new BufferedReader(fr);
			String out = br.readLine();
			br.close();
			fr.close();
			return out;
		}
		catch(IOException e)
		{
			return "";
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
			return "";
		}
	}



	/**
	 * Sees if the category tree is up to date, if not updates it
	 * @return True if successfully completed, false if errors.
	 */
	private static boolean checkCategoryTreeVersion()
	{
		File xmlFile = new File(catTreeLocation);
		//If file does not exist then we need to get it
		if(xmlFile.exists() == false)
		{
			System.out.println("Application run for first time, downloading category tree. Please wait...");
			if(getEntireCategoryTree())
				System.out.println("Category tree retrieved and saved locally.");
			else //error
				return false;
		}
		else //file exists - check it is up to date
		{
			System.out.println();
			try
			{
				String localVersion = getLocalVersionNumber();
				String onlineVersion = getOnlineVersionNumber();

				if(onlineVersion == null)
					return false;
				//versions must be equal as online version can increment or decrement version number
				if ( localVersion !=null ){
					if(!localVersion.equals(onlineVersion))
					{
						//different version numbers - Update
						System.out.println("Updating local category tree. Please wait...");
						if(getEntireCategoryTree())
							System.out.println("Category tree updated and saved locally.");
						else //error
							return false;
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
		return true;
	}



	/**
	 * Return the version number of the Category tree currently available
	 * from the eBay API
	 * @return version number of the category tree currently available online
	 */
	private static String getOnlineVersionNumber()
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
			addeBayHTTPHeaders(connection, devID, appID, certID, compatLevel,verb,siteID);

			//Send the request for just the Version number by setting ViewAllNodes = false and DetailLevel=""
			Document xmlDoc = SendRequest(connection, userToken, "ReturnAll", siteID, verb, false);

			//return the version number
			return xmlDoc.getElementsByTagName("CategoryVersion").item(0).getChildNodes().item(0).getNodeValue();
		}
		catch(Exception e)
		{
			System.out.println("ERROR - GetOnlineVersionNumber: " + e.toString());
			e.printStackTrace();
			return null;
		}

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
			addeBayHTTPHeaders(connection, devID, appID, certID, compatLevel,verb,siteID);

			//Send the request for the entire tree by setting ViewAllNodes = true and DetailLevel=ReturnAll
			catTree = SendRequest(connection, userToken, "ReturnAll", siteID, verb, true);

			//Save Document to a file
			DOMSource source = new DOMSource(catTree);
			StreamResult result = new StreamResult(new FileOutputStream(catTreeLocation));
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.transform(source, result);

			//Save Version Number For Quick Access
			FileWriter fw = new FileWriter(catTreeVersionLocation);
			fw.write(catTree.getElementsByTagName("CategoryVersion").item(0).getChildNodes().item(0).getNodeValue());
			fw.close();
			return true;
		}
		catch(Exception e)
		{
			System.out.println("ERROR - GetEntireGategory: " + e.toString());
			e.printStackTrace();
			return false;
		}

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
			String detailLevel, String siteID, String verb,
			boolean viewAllNodes)
	{

		try
		{
			//Get the XML file into a Document object
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = factory.newDocumentBuilder();
			Document xmlDoc = docBuild.parse(new File(xmlFileLocation));

			//Set the values of the nodes
			xmlDoc.getElementsByTagName("eBayAuthToken").item(0).getChildNodes().item(0).setNodeValue(requestToken);

			if(detailLevel != "")
				xmlDoc.getElementsByTagName("DetailLevel").item(0).getChildNodes().item(0).setNodeValue(detailLevel);
			else //remove if empty string
				xmlDoc.getElementsByTagName("GetCategoriesRequest").item(0).removeChild(xmlDoc.getElementsByTagName("DetailLevel").item(0));

			//  xmlDoc.getElementsByTagName("CategorySiteID").item(0).getChildNodes().item(0).setNodeValue(siteID);

			if(viewAllNodes)
				xmlDoc.getElementsByTagName("ViewAllNodes").item(0).getChildNodes().item(0).setNodeValue("true");
			else
				xmlDoc.getElementsByTagName("ViewAllNodes").item(0).getChildNodes().item(0).setNodeValue("false");


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
			String devID, String appID,
			String certID, String compatLevel,
			String verb, String siteID)
	{
		// Add the Compatibility Level Header
		connection.addRequestProperty("X-EBAY-API-COMPATIBILITY-LEVEL", compatLevel);

		// Add the Developer Name, Application Name, and Certification Name Headers
		connection.addRequestProperty("X-EBAY-API-DEV-NAME", devID);
		connection.addRequestProperty("X-EBAY-API-APP-NAME", appID);
		connection.addRequestProperty("X-EBAY-API-CERT-NAME", certID);

		// Add the API verb Header
		connection.addRequestProperty("X-EBAY-API-CALL-NAME", verb);

		// Add the Site Id Header
		connection.addRequestProperty("X-EBAY-API-SITEID", siteID);

		// Add the Content-Type Header
		connection.addRequestProperty("Content-Type", "text/xml");
	}
	public String[] toArray(){
		String[] arr;
		int length = categories.size();
		arr = new String[length];
		for(int i=0; i<length; i++){
			arr[i] = categories.get(i).toString();
		}
		return arr;
	}
	public String[] toChildArray(){
		String[] arr;
		int length = childCategories.size();
		arr = new String[length];
		for(int i=0; i<length; i++){
			arr[i] = childCategories.get(i).toString();
		}
		return arr;
	}
	
	public String[] toChildArray( int index){
		String parentCategoryID = ((Integer)(categories.get(index).getId())).toString();
		getSubCategories(parentCategoryID );
		return toChildArray();
	}

	//takes the index of a child in the linked list and returns the ID of that child
 public int getChildId(int index){
	return childCategories.get(index).getId();
 }

}
