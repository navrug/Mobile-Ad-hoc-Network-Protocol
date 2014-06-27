package routing;

import graphics.IDrawable;
import graphics.Printer;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

import utilities.IP;
import lsa.LSATable;

public class RoutingTable
{
	Hashtable<IP, IP> table;
	NetworkGraph graph;
	IP ownAddress;
	Printer printer;

	public RoutingTable()
	{
		try {
			ownAddress = new IP(InetAddress.getLocalHost());
			Runtime.getRuntime().exec("echo 1 > /proc/sys/net/ipv4/ip_forward");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void writeTable()
	{
		try {
			Runtime.getRuntime().exec("ip addr flush dev " + "eth0");
			Runtime.getRuntime().exec("ip route flush dev " + "eth0");
			Runtime.getRuntime().exec("ip addr add " + InetAddress.getLocalHost().getHostAddress()  + "/16 dev " + "eth0" + " brd +");
			Runtime.getRuntime().exec("ip route add to default via 192.168.181.131");
			
			System.out.println("[RountingThread] azzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
			
			for( IP m : table.keySet())
			{
				Runtime.getRuntime().exec("ip route add to " + m + "/32 via " + table.get(m));
				System.out.println("[RountingThread] ip route add to " + m + "/32 via " + table.get(m));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateGraph(LSATable lsaTable)
	{
		graph = new NetworkGraph(lsaTable);
		table = new Hashtable<IP, IP>();
		HashSet<IP> inserted = new HashSet<IP>();
		LinkedList<IP> queue = new LinkedList<IP>();
		try {
			addNeighbors(new IP(InetAddress.getLocalHost()),
					inserted,
					queue);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		while (!queue.isEmpty())
			addNeighbors(queue.remove(),
					inserted,
					queue);
		printer.refresh();
	}

	private void addNeighbors(IP a,
			HashSet<IP> inserted,
			LinkedList<IP> queue)
	{
		if (graph.neighbors(a)!=null)
			for (IP b : graph.neighbors(a))
				if (!inserted.contains(b)) {
					inserted.add(b);
					if (!ownAddress.equals(a))
						table.put(b, a);
					queue.add(b);
				}
	}
	
	
}
