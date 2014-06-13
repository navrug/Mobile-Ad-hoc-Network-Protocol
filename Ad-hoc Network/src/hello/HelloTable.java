package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import lsa.LSAMessage;

public class HelloTable
{
	private Hashtable<InetAddress, HelloMessage> table;
	private static short sequenceNumberCounter = 0;

	HelloTable()
	{
		table = new Hashtable<InetAddress, HelloMessage>();
	}
	
	
	public void addHello(InetAddress neighbor, HelloMessage message)
	{
		table.put(neighbor, message);
	}
	
	public HelloMessage createHello()
	{
		InetAddress myAddress = null;
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		HelloMessage result = new HelloMessage(myAddress);
		for (InetAddress neighbor : table.keySet())
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
		InetAddress myAddress = null;
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		LSAMessage result = 
				new LSAMessage(myAddress, sequenceNumberCounter++);
		for (InetAddress neighbor : table.keySet())
		{
			if (table.get(neighbor).isSymmetric(myAddress))
				result.addNeighbor(neighbor);
		}
		return result;
	}
}
