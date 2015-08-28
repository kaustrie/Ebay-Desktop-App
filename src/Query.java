//This class will be used to create objects for query result items 
public class Query {
	public int itemNo;
	public String name;
	public String price;
	public int bids;
	public String image;
	public String place;
	
	Query(int num, String nme, String p, int b, String img, String pl){
		itemNo = num;
		name = nme;
		price = p;
		bids = b;
		image = img;
		place = pl;
	}
}

