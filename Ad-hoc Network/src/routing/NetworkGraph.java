package routing;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import utilities.IP;
import lsa.LSAMessage;
import lsa.LSATable;

public class NetworkGraph 
{
	private final Hashtable<IP, HashSet<IP>> graph;
	private final HashSet<IP> internetProviders; 
	
	public NetworkGraph()
	{
		graph = new Hashtable<IP, HashSet<IP>>();
		internetProviders = new HashSet<IP>();
	}
	
	NetworkGraph(LSATable table)
	{
		graph = new Hashtable<IP, HashSet<IP>>();
		internetProviders = table.getInternetProviders();
		for (LSAMessage message : table.messages()) {
			for (IP neighbor : message.neighbors())
				if (table.isConnectedTo(neighbor, message.source()))
					addEdge(message.source(), neighbor);
		}
	}

	public void addEdge(IP a, IP b) {
		if (graph.containsKey(a))
			graph.get(a).add(b);
		else {
			graph.put(a, new HashSet<IP>());
			graph.get(a).add(b);
		}
	}
	public HashSet<IP> neighbors(IP a)
	{
		HashSet<IP> result = graph.get(a);
		if (result == null)
			return new HashSet<IP>();
		return result;
	}

	public Set<IP> nodes()
	{
		return graph.keySet();
	}
	
	public boolean contains(IP ip)
	{
		return graph.containsKey(ip) ;
	}
	
	public boolean isInternetProvider(IP ip) {
		return internetProviders.contains(ip);
	}
	
}