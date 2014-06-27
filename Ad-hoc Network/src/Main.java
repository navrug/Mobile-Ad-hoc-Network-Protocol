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

import routing.NetworkGraph;
import utilities.IP;
import lsa.LSAMessage;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {

		/*NetworkGraph g = new NetworkGraph();
		IP own = new IP(InetAddress.getLocalHost());
		IP a = new IP(1,1,1,1);
		IP b = new IP(1,1,1,2);
		IP c = new IP(1,1,1,3);
		IP d = new IP(1,1,1,4);
		g.addEdge(a,own);
		g.addEdge(own,a);
		//System.out.println(g.neighbors(own).toArray()[0]);
		g.addEdge(own,b);
		g.addEdge(b,own);
		g.addEdge(own,c);
		g.addEdge(c,own);
		g.addEdge(c,d);
		g.addEdge(d,c);
		Printer p = new Printer(g, true);
//p.refresh();*/
		Integer a =0;
		Integer b = 0;
		LinkedList<Integer> l1 = new LinkedList<Integer>();
		l1.add(a); l1.add(b);
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		l2.add(b); l2.add(a);
		System.out.println(l1.equals(l2));
	}
}
