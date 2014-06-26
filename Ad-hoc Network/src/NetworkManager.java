import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import hello.HelloTable;
import lsa.LSATable;


/*
 * This class is used to launch a Listener and a Sender thread
 */
public class NetworkManager
{
	NetworkManager()
	{
		Thread channel = new Thread(
				new PacketManager(),
				"PacketManager");
		channel.start();
		System.out.println("[NetworkManager] PacketManager launched.");
	}
}
