
public class Category {

	private int id;
	private String name;
	public int getId(){ return id;}
	public String getName(){ return name;}
	
	Category(int i, String n){
		id = i;
		name = n;
	}
	
	public String toString(){
		return name; // +" "+ "( "+id+" )";
	}
}
