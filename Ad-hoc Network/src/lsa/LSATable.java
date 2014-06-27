package lsa;


import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import routing.RoutingTable;
import utilities.IP;


public class LSATable{
	private final Hashtable<IP, LSAMessage> table = new Hashtable<IP, LSAMessage>();
	private final RoutingTable routingTable;
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
		try {
			lock.lock();
			LSAMessage oldMessage = table.get(neighbor);
			if (oldMessage == null || 
					oldMessage.sequenceNumber() < message.sequenceNumber()) {
				table.put(neighbor, message);
				if (!message.equals(oldMessage)) {
					
					System.out.println("##########WAZZZAAAA##############");
					message.display();
					if (oldMessage!=null) {
						oldMessage.display();
					}
					System.out.println("##########WAZZZAAAA##############");
					
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
	public boolean isLatest(IP address, ByteBuffer buffer)
	{
		buffer.getDouble(); //Move of 8 bytes
		LSAMessage latestInTable = null;
		try {
			lock.lock();
			latestInTable = table.get(address);
		}
		finally {
			lock.unlock();
		}
		if (latestInTable!=null)
			System.out.println("isLatest : "+latestInTable.sequenceNumber());
		System.out.println(latestInTable == null);
		short sequence = buffer.getShort();
		buffer.position(0);
		return latestInTable == null || latestInTable.sequenceNumber() 
				< sequence;

	}

	public int numberOfNodes()
	{
		try {
			lock.lock();
			return table.size();
		}
		finally {
			lock.unlock();
		}
	}

	public Set<IP> addresses()
	{
		try {
			lock.lock();
			return table.keySet();
		}
		finally {
			lock.unlock();
		}
	}

	public Collection<LSAMessage> messages()
	{
		try {
			lock.lock();
			return table.values();
		}
		finally {
			lock.unlock();
		}
	}

	public boolean isConnectedTo(IP a, IP b)
	{
		try {
			lock.lock();
			return (table.get(a)!=null)&&table.get(a).neighbors().contains(b);
		}
		finally {
			lock.unlock();
		}
	}
}
