package hello;

import java.net.InetAddress;
import java.util.Hashtable;

public class HelloTable
{
	private Hashtable<InetAddress, HelloMessage> table;
	
	HelloTable()
	{
		table = new Hashtable<InetAddress, HelloMessage>();
	}
	
	HelloMessage createHello()
	{
		
	}
}
