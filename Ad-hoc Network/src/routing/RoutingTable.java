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
import java.util.concurrent.locks.ReentrantLock;

import utilities.IP;
import utilities.SystemCommand;
import lsa.LSATable;

public class RoutingTable
{
	Printer printer;
	private Hashtable<IP, IP> table;
	private NetworkGraph graph;
	private IP ownAddress;
	private final ReentrantLock lock;

	public RoutingTable(ReentrantLock lock)
	{
		this.lock=lock;
		try {
			ownAddress = new IP(InetAddress.getLocalHost());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void writeTable()
	{
		lock.lock();
		try {

			System.out.println("[RountingThread] Reconfiguration ip");
			SystemCommand.cmdExecPrint("ip addr flush dev " + "eth0");
			SystemCommand.cmdExecPrint("ip route flush dev " + "eth0");
			try {
				SystemCommand.cmdExecPrint("ip addr add " + InetAddress.getLocalHost().getHostAddress()  + "/16 dev " + "eth0" + " brd +");
				SystemCommand.cmdExecPrint("ip route add to default via " + InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			for (IP m : table.keySet()) {
				SystemCommand.cmdExecPrint("ip route add to " + m + "/32 via " + table.get(m));
				System.out.println("[RountingThread] ip route add to " + m + "/32 via " + table.get(m));
			}
		}
		finally {
			lock.unlock();
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
