package routing;

import graphics.FormDrawable;
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

public class RoutingTable implements IDrawable
{
	private Hashtable<IP, IP> table;
	/*private*/ public NetworkGraph graph;
	Printer printer;
	private final ReentrantLock lock;

	public RoutingTable(ReentrantLock lock)
	{
		this.lock=lock;
	}

	public void writeTable()
	{
		lock.lock();
		try {

			System.out.println("[RountingThread] Reconfiguration ip");
			SystemCommand.cmdExec("ip addr flush dev " + "eth0");
			SystemCommand.cmdExec("ip route flush dev " + "eth0");
			SystemCommand.cmdExec("ip addr add " + IP.myIP() + "/16 dev " + "eth0" + " brd +");
			SystemCommand.cmdExec("ip route add to default via " + IP.myIP());
			for (IP m : table.keySet()) {
				SystemCommand.cmdExec("ip route add to " + m + "/32 via " + table.get(m));
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
		inserted.add(IP.myIP());
		addNeighbors(IP.myIP(),
				inserted,
				queue);
		while (!queue.isEmpty())
			addNeighbors(queue.remove(),
					inserted,
					queue);
		if (printer == null)
			printer = new Printer(this, true);
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
					if (!IP.myIP().equals(a))
						table.put(b, a);
					queue.add(b);
				}
	}

	//GRAPHICS
	private int xCircleCoord(int rank, int n)
	{
		return FormDrawable.doubleToCoord(0.5
				+Math.cos(rank*2*3.1415/(double)n)/2);
	}
	private int yCircleCoord(int rank, int n)
	{
		return FormDrawable.headerHeight
				+ FormDrawable.doubleToCoord(0.5
						+Math.sin(rank*2*3.1415/(double)n)/2);
	}

	@Override
	public void draw(Graphics g)
	{
		Color c = g.getColor();
		g.setColor(Color.RED);
		Hashtable<IP,Integer> ranks = new Hashtable<IP,Integer>();
		int rank = -1;
		ranks.put(IP.myIP(), rank++);
		for (IP a : graph.neighbors(IP.myIP())) {
			ranks.put(a, rank++);
		}
		for (IP a : table.keySet()) {
			System.out.println((rank+1)+" "+a+"  "+table.get(a));
			ranks.put(a, rank++);
		}
		System.out.println("----------------");
		int n = rank+1;
		for (IP a : graph.neighbors(IP.myIP())) {
			System.out.println(table.get(a));
			System.out.println(ranks.get(IP.myIP()));
			g.drawLine(xCircleCoord(ranks.get(a), n),
					yCircleCoord(ranks.get(a), n),
					xCircleCoord(ranks.get(IP.myIP()), n),
					yCircleCoord(ranks.get(IP.myIP()), n));
		}
		for (IP a : table.keySet()) {
			System.out.println(table.get(a));
			System.out.println(ranks.get(table.get(a)));
			g.drawLine(xCircleCoord(ranks.get(a), n),
					yCircleCoord(ranks.get(a), n),
					xCircleCoord(ranks.get(table.get(a)), n),
					yCircleCoord(ranks.get(table.get(a)), n));
		}
		g.setColor(Color.WHITE);
		g.drawString(
				"Genetic algorithm over Eulierian cycle search"
				,20,20);
		g.drawString("Fitness : ",20,40);
		g.drawString("Iteration #",20,60);
		g.fillOval(xCircleCoord(ranks.get(IP.myIP()), n)-4,
				yCircleCoord(ranks.get(IP.myIP()), n)-4,9,9);
		g.drawString(IP.myIP().toString(),
				xCircleCoord(ranks.get(IP.myIP()), n)-4,
				yCircleCoord(ranks.get(IP.myIP()), n)-4);
		for (IP a : graph.neighbors(IP.myIP())) {
			g.fillOval(xCircleCoord(ranks.get(a), n)-4,
					yCircleCoord(ranks.get(a), n)-4,9,9);
			g.drawString(a.toString(),
					xCircleCoord(ranks.get(a), n)-4,
					yCircleCoord(ranks.get(a), n)-4);
		}
		for (IP a : table.keySet()) {
			g.fillOval(xCircleCoord(ranks.get(a), n)-4,
					yCircleCoord(ranks.get(a), n)-4,9,9);
			g.drawString(a.toString(),
					xCircleCoord(ranks.get(a), n)-4,
					yCircleCoord(ranks.get(a), n)-4);
		}
		g.setColor(c);
	}

}
