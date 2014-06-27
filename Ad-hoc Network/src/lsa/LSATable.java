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

	public LSATable(ReentrantLock lock)
	{
		routingTable = new RoutingTable(lock);
	}
	
	/*
	 *  Function called when a LSA is received.
	 *  If the message has a newer sequence number, we put it in the table,
	 *  but we update the graph only if it brings different information.
	 */
	public void addLSA(IP neighbor, LSAMessage message)
	{
		LSAMessage oldMessage = table.get(neighbor);
		if (oldMessage == null || 
				oldMessage.sequenceNumber() < message.sequenceNumber()) {
			table.put(neighbor, message);
			if (!oldMessage.equals(message)) {
				routingTable.updateGraph(this);
				routingTable.writeTable();
			}
		}
	}
	
	//Takes in  consulting mode, returns the same mode
	public boolean isLatest(IP address, ByteBuffer buffer)
	{
		buffer.getDouble(); //Move of 8 bytes
		LSAMessage latestInTable = table.get(address);
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
		return table.size();
	}

	public Set<IP> addresses()
	{
		return table.keySet();
	}

	public Collection<LSAMessage> messages()
	{
		return table.values();
	}

	public boolean isConnectedTo(IP a, IP b)
	{
		return (table.get(a)!=null)&&table.get(a).neighbors().contains(b);
	}
}
