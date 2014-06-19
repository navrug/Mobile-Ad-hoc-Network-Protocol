package lsa;


import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class LSATable{
	private Hashtable<InetAddress, LSAMessage> table;
	private final ReentrantLock lock;
	private final Condition notUpdated;

	public LSATable(ReentrantLock lock, Condition notUpdated)
	{
		table = new Hashtable<InetAddress, LSAMessage>();
		this.lock = lock;
		this.notUpdated = notUpdated;
	}
	
	
	public void addLSA(InetAddress neighbor, LSAMessage message)
	{
		LSAMessage oldMessage = table.get(neighbor);
		if (oldMessage != null && 
				oldMessage.sequenceNumber() < message.sequenceNumber()) {
			table.put(neighbor, message);
			notUpdated.signal();
			
		}
	}
	
	public boolean isLatest(InetAddress address, ByteBuffer buffer)
	{
		LSAMessage latest = table.get(address);
		return latest == null || latest.sequenceNumber() 
				< 256*buffer.array()[8]+buffer.array()[9];
	}
	
	public int numberOfNodes()
	{
		return table.size();
	}
	
	public Set<InetAddress> addresses()
	{
		return table.keySet();
	}
	
	public Collection<LSAMessage> messages()
	{
		return table.values();
	}
	
	public boolean isConnectedTo(InetAddress a, InetAddress b)
	{
		return table.get(a).neighbors().contains(b);
	}
}
