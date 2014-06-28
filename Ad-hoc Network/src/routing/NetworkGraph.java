package routing;

import java.awt.Color;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import graphics.FormDrawable;
import graphics.IDrawable;
import utilities.IP;
import lsa.LSAMessage;
import lsa.LSATable;

public class NetworkGraph implements IDrawable
{
	/*private*/ public final Hashtable<IP, HashSet<IP>> graph;
	
	public NetworkGraph()
	{
		graph = new Hashtable<IP, HashSet<IP>>();
	}
	
	NetworkGraph(LSATable table)
	{
		graph = new Hashtable<IP, HashSet<IP>>();
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
	
	//GRAPHICS 

	private int xCircleCoord(int rank, int n, int k)
	{
		return FormDrawable.doubleToCoord(0.5
				+Math.cos(rank*2*3.1415/(double)n)/2/4*k);
	}
	private int yCircleCoord(int rank, int n, int k)
	{
		return FormDrawable.headerHeight
				+ FormDrawable.doubleToCoord(0.5
						+Math.sin(rank*2*3.1415/(double)n)/2/4*k);
	}

	private void drawRound(Graphics g,
			HashSet<IP> round,
			int k)
	{
		int i = 0;
		int n = round.size();
		int x;
		int y;
		for (IP ip : round) {
			
			x = xCircleCoord(i, n, k)-4;
			y = yCircleCoord(i, n, k)-4;
			g.fillOval(x, y, 9, 9);
			g.drawString(ip.toString(), x, y);
			i++;
		}
	}

	public void draw(Graphics g)
	{
		Color c = g.getColor();
		g.setColor(Color.WHITE);

		IP ownIP = null;
		try {
			ownIP = new IP(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//Center
		g.fillOval(FormDrawable.doubleToCoord(0.5)-4,
				FormDrawable.headerHeight +
				FormDrawable.doubleToCoord(0.5)-4,9,9);
		g.drawString(ownIP.toString(),
				FormDrawable.doubleToCoord(0.5)-4,
				FormDrawable.headerHeight +
				FormDrawable.doubleToCoord(0.5)-4);

		Integer k = 1;
		HashSet<IP> lastRound = new HashSet<IP>();
		HashSet<IP> nextRound = neighbors(ownIP);
		System.out.println("next round length "+nextRound.size());
		HashSet<IP> drawn =  new HashSet<IP>();
		for (IP ip : nextRound)
			drawn.add(ip);
		drawn.add(ownIP);

		while(nextRound.size() != 0) {
			drawRound(g, nextRound, k);
			k++;
			lastRound = nextRound;
			nextRound = new HashSet<IP>();
			for (IP ip : lastRound) {
				for (IP neigh : neighbors(ip)) {
					if (!drawn.contains(neigh)) {
						System.out.print(ip+"   ");
						System.out.println(neigh);
						nextRound.add(neigh);
						drawn.add(neigh);
					}
				}
			}
		}
		g.setColor(c);
	}
}