package routing;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import lsa.LSAMessage;
import lsa.LSATable;

public class NetworkGraph
{
	boolean[][] neighbor;
	InetAddress[] addresses;
	int numberOfNodes;
	Hashtable<InetAddress, HashSet<InetAddress>> graph;
	
	NetworkGraph(LSATable table)
	{
		graph = new Hashtable<InetAddress, HashSet<InetAddress>>();
		numberOfNodes = table.numberOfNodes();
		neighbor = new boolean[numberOfNodes][];
		for (int i = 0; i<numberOfNodes; i++)
			neighbor[i] = new boolean[numberOfNodes];
		addresses = new InetAddress[numberOfNodes];
		int i = 0;
		for (InetAddress address : table.addresses()) {
			addresses[i] = address;
			i++;
		}
		for (LSAMessage message : table.messages()) {
			for (InetAddress neighbor : message.neighbors())
				if (table.isConnectedTo(neighbor, message.source()))
					addEdge(message.source(), neighbor);
		}
	}
	
	private void addEdge(InetAddress a, InetAddress b) {
		if (graph.containsKey(a))
			graph.get(a).add(b);
		else {
			graph.put(a, new HashSet<InetAddress>());
			graph.get(a).add(b);
		}
	}
	public HashSet<InetAddress> neighbors(InetAddress a)
	{
		return graph.get(a);
	}
	
	public Set<InetAddress> nodes()
	{
		return graph.keySet();
	}
}
