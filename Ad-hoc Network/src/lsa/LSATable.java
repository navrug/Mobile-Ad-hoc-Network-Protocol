package lsa;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import routing.RoutingTable;
import utilities.IP;


public class LSATable{
	private final Hashtable<IP, LSAMessage> table = new Hashtable<IP, LSAMessage>();
	private final RoutingTable routingTable;
	private final long creationTime = System.currentTimeMillis();
	private final Hashtable<IP, Long> arrivalTime = new Hashtable<IP, Long>();
	private final HashSet<IP> internetProviders = new HashSet<IP>(); 
	private ReentrantLock lock = new ReentrantLock(); 

	public LSATable(ReentrantLock netLock)
	{
		routingTable = new RoutingTable(netLock);
	}

	/*
	 *  Function called when a LSA is received.
	 *  If the message has a newer sequence number, we put it in the table,
	 *  but we update the graph only if it brings different information.
	 */
	public void addLSA(IP neighbor, LSAMessage message)
	{
		lock.lock();
		try {
			LSAMessage oldMessage = table.get(neighbor);
			if (oldMessage == null || 
					oldMessage.sequenceNumber() < message.sequenceNumber()) {
				table.put(neighbor, message);
				arrivalTime.put(neighbor, 
						System.currentTimeMillis()-creationTime);
				if (message.source().isInternetProvider())
					internetProviders.add(message.source());
				if (!message.equals(oldMessage)) {
					routingTable.updateGraph(this);
					routingTable.writeTable();
				}
			}
		}
		finally {
			lock.unlock();
		}
	}

	//Takes in  consulting mode, returns the same mode
	public boolean isLatest(ByteBuffer buffer)
	{
		buffer.getFloat(); //Move of 4 bytes
		byte[] source = new byte[4];
		for (int i =0; i<4; i++)
			source[i] = buffer.get();
		IP sourceAddress = new IP(source);
		LSAMessage latestInTable = null;
		lock.lock();
		try {
			latestInTable = table.get(sourceAddress);
		}
		finally {
			lock.unlock();
		}
		short sequence = buffer.getShort();
		buffer.position(0);
		return latestInTable == null || latestInTable.sequenceNumber() 
				< sequence;

	}

	public int numberOfNodes()
	{
		lock.lock();
		try {
			return table.size();
		}
		finally {
			lock.unlock();
		}
	}

	public Set<IP> addresses()
	{
		lock.lock();
		try {
			return table.keySet();
		}
		finally {
			lock.unlock();
		}
	}

	public Collection<LSAMessage> messages()
	{
		lock.lock();
		try {
			return table.values();
		}
		finally {
			lock.unlock();
		}
	}

	public boolean isConnectedTo(IP a, IP b)
	{
		lock.lock();
		try {
			return (table.get(a)!=null)&&table.get(a).neighbors().contains(b);
		}
		finally {
			lock.unlock();
		}
	}
	
	public boolean checkDeadNodes()
	{
		boolean result = false;
		long currentTime = System.currentTimeMillis()-creationTime;
		LinkedList<IP> toRemove = new LinkedList<IP>();
		lock.lock();
		try {
			for (IP neighbor : arrivalTime.keySet())
				if (currentTime - arrivalTime.get(neighbor) > 20000) {
					toRemove.add(neighbor);
					result = true;
				}
			for (IP neighbor : toRemove) {
				table.remove(neighbor);
				arrivalTime.remove(neighbor);
			}
		}
		finally {
			lock.unlock();
		}
		return result;	
	}

	public HashSet<IP> getInternetProviders()
	{
		return internetProviders;
	}
}
