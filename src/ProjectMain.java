import java.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
public class ProjectMain {
	
	public static LinkedList<Query> queryResults; //linked list to store each query result item
	public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; "
			+ "en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6";

	public static String itemToUrl( String itm){

		return "http://cgi.ebay.com/ws/eBayISAPI.dll?"
				+ "MfcISAPICommand=ViewItem&item=" + itm;
	}

	//Obtains html from ebay query page
	public static String getQueryHTML(String url) throws IOException{
		URL webpage = new URL(url); //creates new url object from string
		URLConnection connection = webpage.openConnection(); //open url connection
		connection.setRequestProperty("User-Agent", USER_AGENT); //apply user agent info
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));

		String line = reader.readLine();
		String queryContent = "";//extract entire page here

		// while loop: to store the url into a string
		while (line != null) 
		{
			queryContent += line;
			line = reader.readLine();

		} // end while

		return queryContent;
	}

	public static void getQueryInfo () throws IOException{
		String html = getQueryHTML(" ");
		String temp;
		//Match each item on page using appropriate tags

		Matcher itemMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern itemPattern = Pattern.compile("<table listingId=(.*?)</table>", Pattern.DOTALL);
		itemMatch = itemPattern.matcher(html);

		while (itemMatch.find()){
			getQueryFields(itemMatch.group());
		}
	}

	public static void getQueryFields(String item){
		Matcher itemNoMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern itemNoPattern = Pattern.compile("\\d+", Pattern.DOTALL);
		itemNoMatch = itemNoPattern.matcher(item);
		int num = Integer.parseInt(itemNoMatch.group());

		Matcher nameMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern namePattern = Pattern.compile("title='(.*?)' itemprop", Pattern.DOTALL);
		nameMatch = namePattern.matcher(item);
		nameMatch.matches();
		String name = nameMatch.group().replaceAll("(title=')|(' itemprop)", "");

		Matcher priceMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern pricePattern = Pattern.compile("itemprop=\"price\">(.*?)</div>", Pattern.DOTALL);
		priceMatch = pricePattern.matcher(item);
		priceMatch.matches();
		String price = nameMatch.group().replaceAll("(itemprop=\"price\">)|\\s|(</div>)", "");

		Matcher bidMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern bidPattern = Pattern.compile("div class=\"bids\">(.*?)</div>", Pattern.DOTALL);
		bidMatch = bidPattern.matcher(item);
		bidMatch.matches();
		int bid = Integer.parseInt(nameMatch.group().replaceAll("(div class=\"bids\">)|(<div  >)|( bids</div>)", ""));
		
		Matcher placeMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern placePattern = Pattern.compile("div class=\"mWSpc\"></div>(.*?)</div>", Pattern.DOTALL);
		placeMatch = placePattern.matcher(item);
		placeMatch.matches();
		String place = nameMatch.group().replaceAll("(div class=\"mWSpc\">)|(<div  >)|(</div>(From )?)", "");

		Matcher imageMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern imagePattern = Pattern.compile("(http(s?):/)(/[^/]+)+" + "\\.(?:jpe?g)", Pattern.DOTALL);
		imageMatch = imagePattern.matcher(item);
		String image = imageMatch.group();
		
		queryResults.addLast(new Query(num, name, price, bid, image, place));
	}

	public String getNextPage() throws IOException{
		String html = getQueryHTML(" url ");
		Matcher nxtMatch; //matcher created
		//strip the image section based on the pattern below
		Pattern nxtPattern = Pattern.compile("div class=\"bids\">(.*?) </div>", Pattern.DOTALL);
		nxtMatch = nxtPattern.matcher(" url ");
		String nxt = nxtMatch.group();

		//we only want link to next page
		Pattern imgPattern = Pattern.compile("http://(.*?)=nc", Pattern.DOTALL);
		Matcher imgMatcher = imgPattern.matcher(" url ");
		return imgMatcher.group();
	}
	
	public String search(String keyword, String Category){
		
	}
	
}
