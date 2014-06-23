package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import utilities.IP;
import lsa.LSAMessage;

public class HelloTable
{
	private Hashtable<IP, HelloMessage> table;
	private static short sequenceNumber = 0;

	public HelloTable()
	{
		table = new Hashtable<IP, HelloMessage>();
	}
	
	
	public void addHello(IP neighbor, HelloMessage message)
	{
		/*if (!message.equals(*/table.put(neighbor, message)/*))
			sequenceNumber++*/;
	}
	
	public HelloMessage createHello()
	{
		IP myAddress = null;
		try {
			myAddress = new IP(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		HelloMessage result = new HelloMessage(myAddress);
		for (IP neighbor : table.keySet())
		{
			if (table.get(neighbor).isSymmetric(myAddress))
				result.addSymmetric(neighbor);
			else
				result.addHeard(neighbor);

		}
		return result;
	}
	
	public LSAMessage createLSA()
	{
		IP myAddress = null;
		try {
			myAddress = new IP(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		LSAMessage result = 
				new LSAMessage(myAddress, sequenceNumber++);
		for (IP neighbor : table.keySet())
		{
			if (table.get(neighbor).isSymmetric(myAddress))
				result.addNeighbor(neighbor);
		}
		return result;
	}
}
