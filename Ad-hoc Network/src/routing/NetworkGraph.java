package routing;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import utilities.IP;
import lsa.LSAMessage;
import lsa.LSATable;

public class NetworkGraph
{
	boolean[][] neighbor;
	IP[] addresses;
	int numberOfNodes;
	Hashtable<IP, HashSet<IP>> graph;
	
	NetworkGraph(LSATable table)
	{
		graph = new Hashtable<IP, HashSet<IP>>();
		numberOfNodes = table.numberOfNodes();
		/*neighbor = new boolean[numberOfNodes][];
		for (int i = 0; i<numberOfNodes; i++)
			neighbor[i] = new boolean[numberOfNodes];
		addresses = new IP[numberOfNodes];
		int i = 0;
		for (IP address : table.addresses()) {
			addresses[i] = address;
			i++;
		}*/
		for (LSAMessage message : table.messages()) {
			for (IP neighbor : message.neighbors())
				if (table.isConnectedTo(neighbor, message.source()))
					addEdge(message.source(), neighbor);
		}
	}
	
	private void addEdge(IP a, IP b) {
		if (graph.containsKey(a))
			graph.get(a).add(b);
		else {
			graph.put(a, new HashSet<IP>());
			graph.get(a).add(b);
		}
	}
	public HashSet<IP> neighbors(IP a)
	{
		return graph.get(a);
	}
	
	public Set<IP> nodes()
	{
		return graph.keySet();
	}
}
