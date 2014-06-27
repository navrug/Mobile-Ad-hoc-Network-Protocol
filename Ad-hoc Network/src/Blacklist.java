import java.util.ArrayList;

import utilities.IP;


public class Blacklist {

	private final ArrayList<IP> list;
	
	public Blacklist() {
		list=new ArrayList<IP>();
	}

	public void add (IP address) {
		list.add(address);
	}

	public boolean contains (IP address) {
		for (IP addresslist : list) {
			if (addresslist.equals(address)) return true;
		}
		return false;
	}
}
