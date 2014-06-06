package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class HelloTable
{
	private Hashtable<InetAddress, HelloMessage> table;

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
		HelloMessage result = new HelloMessage();
		InetAddress myAddress = null;
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		for (InetAddress neighbor : table.keySet())
		{
			if (table.get(neighbor).isSymmetric(myAddress))
				result.addSymmetric(neighbor);
			else
				result.addHeard(neighbor);

		}
		return result;
	}
}
