import graphics.Printer;
import hello.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import routing.NetworkGraph;
import routing.RoutingTable;
import utilities.IP;
import lsa.LSAMessage;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {

		/*IP.defineIP();
		NetworkGraph g = new NetworkGraph();
		IP own = IP.myIP();
		IP a = new IP(1,1,1,1);
		IP b = new IP(1,1,1,2);
		IP c = new IP(1,1,1,3);
		IP d = new IP(1,1,1,4);
		g.addEdge(a,own);
		g.addEdge(own,a);
		g.addEdge(own,b);
		g.addEdge(b,own);
		g.addEdge(own,c);
		g.addEdge(c,own);
		g.addEdge(c,d);
		g.addEdge(d,c);
		
		RoutingTable t = new RoutingTable(new ReentrantLock());
		
		t.graph = g;
		t.updateGraph();
		
		/*Printer p = new Printer(t, true);
		p.refresh();*/
		
		PacketManager packet = PacketManager.getInstance();
		packet.run();
	}
}
