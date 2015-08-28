


import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindItemsAdvancedRequest;
import com.ebay.services.finding.FindItemsAdvancedResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;

/**
 * A sample to show eBay Finding servcie call using the simplied interface 
 * provided by the findingKit.
 * 
 * @author boyang
 * 
 */
public class Searchb {
	/**
	 * This variable stores the location of the config file, containing
	 * the properties that need to be set for this code to work.
	 */
	private static final String configFileLocation = "config.properties";
	
	/**
	 * This variable stores the location of the XML, containing
	 * the XML template needed for this call.
	 */
	private static final String xmlFileLocation = "searchRequest.xml";

	// Basic service call flow:
	// 1. Setup client configuration
	// 2. Create service client
	// 3. Create outbound request and setup request parameters
	// 4. Call the operation on the service client and receive inbound response
	// 5. Handle response accordingly
	// Handle exception accordingly if any of the above steps goes wrong.
	public static  void main(String[] args) {
		Properties configur = new Properties();
		
		try {
			configur.load(new FileInputStream(configFileLocation));
			
			// initialize service end-point configuration
			ClientConfig config = new ClientConfig();
			// endpoint address can be overwritten here, by default, production address is used,
			// to enable sandbox endpoint, just uncomment the following line
			//config.setEndPointAddress("http://svcs.sandbox.ebay.com/services/search/FindingService/v1");
			config.setApplicationId(configur.getProperty("AppID"));

			//create a service client
			FindingServicePortType serviceClient = FindingServiceClientFactory.getServiceClient(config);

			//create request object
			FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();
			FindItemsAdvancedRequest req = new FindItemsAdvancedRequest();
			//set request parameters
			req.setKeywords("harry potter");
			PaginationInput pi = new PaginationInput();
			pi.setEntriesPerPage(4);
			req.setPaginationInput(pi);
			req.setDescriptionSearch(true);
			  List<String> catID = req.getCategoryId();
		        //catID.add("1");
		        catID.add("279");
		       

			//call service
			FindItemsAdvancedResponse resp = serviceClient.findItemsAdvanced(req);

			//output result
			System.out.println("Ack = "+resp.getAck()); 
			System.out.println("Find " + resp.getSearchResult().getCount() + " items." );
			List<SearchItem> items = resp.getSearchResult().getItem();
			for(SearchItem item : items) { 
				System.out.println(item.getTitle()); 
				System.out.println(item.getGalleryURL()); 
			}

		} catch (Exception ex) {
			// handle exception if any
			ex.printStackTrace();
		}
	}
}


