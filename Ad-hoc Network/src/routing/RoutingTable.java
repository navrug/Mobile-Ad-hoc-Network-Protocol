package routing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

public class RoutingTable
{
	Hashtable<InetAddress, InetAddress> table;
	NetworkGraph graph;
	
	RoutingTable(NetworkGraph graph)
	{
		this.graph = graph;
		table = new Hashtable<InetAddress, InetAddress>();
		HashSet<InetAddress> inserted = new HashSet<InetAddress>();
		LinkedList<InetAddress> queue = new LinkedList<InetAddress>();
		try {
			addNeighbors(InetAddress.getLocalHost(),
					inserted,
					queue);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		while (!queue.isEmpty())
			addNeighbors(queue.remove(),
					inserted,
					queue);
	}
	
	private void addNeighbors(InetAddress a,
			HashSet<InetAddress> inserted,
			LinkedList<InetAddress> queue)
	{
		for (InetAddress b : graph.neighbors(a))
			if (!inserted.contains(b)) {
				inserted.add(b);
				table.put(b, a);
				queue.add(b);
			}
		
			
	}
}
