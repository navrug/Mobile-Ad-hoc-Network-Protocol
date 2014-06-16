import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
		final ReentrantLock lock = new ReentrantLock();
		final Condition notUpdated  = lock.newCondition();
		
		helloTable = new HelloTable();
		lsaTable = new LSATable(lock, notUpdated);
		
		Thread channel = new Thread(
				new PacketManager(helloTable, lsaTable),
				"PacketManager");
		channel.start();
		/*Thread listener = new Thread(
				new Listener(helloTable, lsaTable, channel),
				"Listener");
		Thread sender = new Thread(
				new Sender(helloTable, channel),
				"Sender");*/
		Thread routing = new Thread(
				new RoutingThread(lsaTable, lock, notUpdated),
				"Routing");
		
		//listener.start();
		//sender.start();
		routing.start();
	}
}
