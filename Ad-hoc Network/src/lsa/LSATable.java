package lsa;


import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import routing.RoutingTable;
import utilities.IP;


public class LSATable{
	private Hashtable<IP, LSAMessage> table;
	private RoutingTable routingTable = new RoutingTable();

	public LSATable()
	{
		table = new Hashtable<IP, LSAMessage>();
	}
	
	public void addLSA(IP neighbor, LSAMessage message)
	{
		LSAMessage oldMessage = table.get(neighbor);
		if (oldMessage == null || 
				oldMessage.sequenceNumber() < message.sequenceNumber()) {
			table.put(neighbor, message);
			routingTable.updateGraph(this);
			routingTable.writeTable();
		}
	}
	
	//Takes in  consulting mode, returns the same mode
	public boolean isLatest(IP address, ByteBuffer buffer)
	{
		buffer.getDouble();//Avancer de 8 octets
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
