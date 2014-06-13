import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import routing.RoutingThread;
import sender.Sender;
import hello.HelloTable;
import listener.Listener;
import lsa.LSATable;


/*
 * This class is used to launch a Listener and a Sender thread
 */
public class NetworkManager
{
	private HelloTable helloTable;
	private LSATable lsaTable;
	
	NetworkManager()
	{
		helloTable = new HelloTable();
		lsaTable = new LSATable();

		Thread channel = new Thread(
				new ChannelManager(helloTable, lsaTable),
				"ChannelManager");
		channel.start();
		/*Thread listener = new Thread(
				new Listener(helloTable, lsaTable, channel),
				"Listener");
		Thread sender = new Thread(
				new Sender(helloTable, channel),
				"Sender");*/
		Thread routing = new Thread(
				new RoutingThread(),
				"Routing");
		
		//listener.start();
		//sender.start();
		routing.start();
	}
}
