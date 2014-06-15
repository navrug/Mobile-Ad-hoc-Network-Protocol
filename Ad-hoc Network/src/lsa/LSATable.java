package lsa;


import java.net.InetAddress;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;


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
	
	public int numberOfNodes()
	{
		return table.size();
	}
	
	public Set<InetAddress> addresses()
	{
		return table.keySet();
	}
	
	public Collection<LSAMessage> messages()
	{
		return table.values();
	}
	
	public boolean isConnectedTo(InetAddress a, InetAddress b)
	{
		return table.get(a).neighbors().contains(b);
	}
}
