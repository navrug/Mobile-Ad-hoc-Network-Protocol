package routing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import lsa.LSATable;

public class RoutingTable
{
	Hashtable<String, InetAddress> table;
	NetworkGraph graph;

	public RoutingTable()
	{
	}

	public void writeTable()
	{
		try {
			Runtime.getRuntime().exec("ip addr flush dev " + "eth0");
			Runtime.getRuntime().exec("ip route flush dev " + "eth0");
			Runtime.getRuntime().exec("ip addr add " + InetAddress.getLocalHost().getHostAddress()  + "/16 dev " + "eth0" + " brd +");

			for( String m : table.keySet())
			{
				Runtime.getRuntime().exec("ip route add to " + m + "/32 via " + table.get(m).getHostAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateGraph(LSATable lsaTable)
	{
		graph = new NetworkGraph(lsaTable);
		table = new Hashtable<String, InetAddress>();
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
				table.put(b.getHostAddress(), a);
				queue.add(b);
			}
	}

}
