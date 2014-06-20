import java.util.ArrayList;


public class Blacklist {

	private ArrayList<String> list;
	
	public Blacklist() {
		list=new ArrayList<String>();
	}

	public void add (String address) {
		list.add(address);
	}

	public boolean contains (String address) {
		for (String addresslist : list) {
			if (addresslist.equals(address)) return true;
		}
		return false;
	}
}
