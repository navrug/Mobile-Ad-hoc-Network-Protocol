package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

import utilities.IP;
import lsa.LSAMessage;

/*
 * This class has to be concurrency safe because MessageThread
 * adds messages to the table while PacketManager uses the
 * table to create and sends new messages.
 */
public class HelloTable
{
	private final Hashtable<IP, HelloMessage> table;
	private final long creationTime = System.currentTimeMillis();
	private final Hashtable<IP, Long> arrivalTime = new Hashtable<IP, Long>();
	private static short sequenceNumber = 0;
	private ReentrantLock lock = new ReentrantLock();

	public HelloTable()
	{
		table = new Hashtable<IP, HelloMessage>();
	}

	public void addHello(IP neighbor, HelloMessage message)
	{
		lock.lock();
		try {
			table.put(neighbor, message);
			arrivalTime.put(neighbor, 
					System.currentTimeMillis()-creationTime);
		}
		finally {
			lock.unlock();
		}
	}

	public HelloMessage createHello()
	{
		HelloMessage result = new HelloMessage(IP.myIP());
		lock.lock();
		try {
			for (IP neighbor : table.keySet()) {
				if (table.get(neighbor).isSymmetric(IP.myIP()))
					result.addSymmetric(neighbor);
				else
					result.addHeard(neighbor);
			}
		}
		finally {
			lock.unlock();
		}
		return result;
	}

	public LSAMessage createLSA()
	{
		LSAMessage result = 
				new LSAMessage(IP.myIP(), sequenceNumber++);
		lock.lock();
		try {
			for (IP neighbor : table.keySet())
			{
				if (table.get(neighbor).isSymmetric(IP.myIP()))
					result.addNeighbor(neighbor);
			}
		}
		finally {
			lock.unlock();
		}
		return result;
	}

	public boolean checkDeadNodes()
	{
		boolean result = false;
		long currentTime = System.currentTimeMillis()-creationTime;
		lock.lock();
		try {

			for (IP neighbor : arrivalTime.keySet())
				if (currentTime - arrivalTime.get(neighbor) > 5000) {
					table.remove(neighbor);
					arrivalTime.remove(neighbor);
					result = true;
				}
		}
		finally {
			lock.unlock();
		}
		return result;	
	}
}
