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
	private static short sequenceNumber = 0;
	private ReentrantLock lock = new ReentrantLock();

	public HelloTable()
	{
		table = new Hashtable<IP, HelloMessage>();
	}

	public void addHello(IP neighbor, HelloMessage message)
	{
		try {
			lock.lock();
			table.put(neighbor, message);
		}
		finally {
			lock.unlock();
		}
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
		try {
			lock.lock();
			for (IP neighbor : table.keySet()) {
				if (table.get(neighbor).isSymmetric(myAddress))
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
		IP myAddress = null;
		try {
			myAddress = new IP(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		LSAMessage result = 
				new LSAMessage(myAddress, sequenceNumber++);
		try {
			lock.lock();
			for (IP neighbor : table.keySet())
			{
				if (table.get(neighbor).isSymmetric(myAddress))
					result.addNeighbor(neighbor);
			}
		}
		finally {
			lock.unlock();
		}
		return result;
	}
}
