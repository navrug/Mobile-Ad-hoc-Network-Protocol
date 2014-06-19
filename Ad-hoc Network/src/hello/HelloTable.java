package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import lsa.LSAMessage;

public class HelloTable
{
	private Hashtable<InetAddress, HelloMessage> table;
	private static short sequenceNumber = 0;

	public HelloTable()
	{
		table = new Hashtable<InetAddress, HelloMessage>();
	}
	
	
	public void addHello(InetAddress neighbor, HelloMessage message)
	{
		if (!message.equals(table.put(neighbor, message)))
			sequenceNumber++;
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
				new LSAMessage(myAddress, sequenceNumber);
		for (InetAddress neighbor : table.keySet())
		{
			if (table.get(neighbor).isSymmetric(myAddress))
				result.addNeighbor(neighbor);
		}
		return result;
	}
}
