package lsa;


import java.net.InetAddress;
import java.util.Hashtable;


public class LSATable{
	private Hashtable<InetAddress, LSAMessage> table;

	public LSATable()
	{
		table = new Hashtable<InetAddress, LSAMessage>();
	}
	
	
	public void addLSA(InetAddress neighbor, LSAMessage message)
	{
		table.put(neighbor, message);
	}
}
