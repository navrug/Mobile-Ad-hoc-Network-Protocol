package routing;

import graphics.FormDrawable;
import graphics.IDrawable;
import graphics.Printer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
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
			SystemCommand.cmdExec("ip route flush dev " + IP.myIface());
			SystemCommand.cmdExec("ip route add to default via " + IP.myDefaultRoute());
			
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
		
		if(!graph.contains(IP.myDefaultRoute())) 
			IP.defineDefaultRoute(IP.myIP());
		
		inserted.add(IP.myIP());
		addNeighbors(IP.myIP(),
				inserted,
				queue);
		while (!queue.isEmpty()) {
			IP ip = queue.remove();
			if(IP.myDefaultRoute().equals(IP.myIP())&&ip.isInternetProvider()) 
				IP.defineDefaultRoute(ip);
			addNeighbors(ip,
					inserted,
					queue);
			}
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
				+0.4*Math.cos(rank*2*3.1415/(double)n));
	}
	private int yCircleCoord(int rank, int n)
	{
		return FormDrawable.headerHeight
				+ FormDrawable.doubleToCoord(0.5
						+0.4*Math.sin(rank*2*3.1415/(double)n));
	}
	private void drawLine(Graphics g, int a, int b, int n) {
		g.drawLine(xCircleCoord(a, n),
				yCircleCoord(a, n),
				xCircleCoord(b, n),
				yCircleCoord(b, n));
	}
	private void drawNode(Graphics g, IP ip, int a, int n)
	{
		g.fillOval(xCircleCoord(a, n)-4,
				yCircleCoord(a, n)-4,9,9);
		g.drawString(ip.toString(),
				xCircleCoord(a, n)-4,
				yCircleCoord(a, n)-4);
	}

	@Override
	public void draw(Graphics g)
	{
		Color c = g.getColor();
		//Counting the nodes
		Hashtable<IP,Integer> ranks = new Hashtable<IP,Integer>();
		int rank = 0;
		ranks.put(IP.myIP(), rank++);
		for (IP a : graph.neighbors(IP.myIP()))
			ranks.put(a, rank++);
		for (IP a : table.keySet())
			ranks.put(a, rank++);
		int n = rank;
		//Drawing all the edges of the graph
		g.setColor(Color.GRAY);
		for (IP a : graph.neighbors(IP.myIP()))
			for (IP b : graph.neighbors(a))
				drawLine(g, ranks.get(a), ranks.get(b), n);
		for (IP a : table.keySet())
			for (IP b : graph.neighbors(a))
				drawLine(g, ranks.get(a), ranks.get(b), n);
		//Drawing the routing edges
		g.setColor(Color.RED);
		for (IP a : graph.neighbors(IP.myIP()))
			drawLine(g, ranks.get(a), ranks.get(IP.myIP()), n);
		for (IP a : table.keySet())
			drawLine(g, ranks.get(a), ranks.get(table.get(a)), n);
		
		g.setColor(Color.WHITE);
		//Drawing the nodes
		drawNode(g, IP.myIP(), ranks.get(IP.myIP()), n);
		for (IP a : graph.neighbors(IP.myIP()))
			drawNode(g, a, ranks.get(a), n);
		for (IP a : table.keySet())
			drawNode(g, a, ranks.get(a), n);
		//Drawing the header
		g.drawString(
				"Graphic representation of the routing table of "
				+IP.myIP()
				,20,20);
		g.drawString("Red edges are routes,"
				+" white are unused connections",20,40);
		g.setColor(c);
	}

}
