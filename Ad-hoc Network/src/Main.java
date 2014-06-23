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

import utilities.IP;
import lsa.LSAMessage;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {

		//new NetworkManager();

		System.out.println(InetAddress.getLocalHost());
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		System.out.println(InetAddress.getLocalHost().getHostName());
		IP ip1 = new IP(InetAddress.getLocalHost());
		IP ip2 = new IP(InetAddress.getLocalHost());
		IP ip3 = new IP(InetAddress.getLocalHost());
		HashSet<IP> set = new HashSet<IP>();
		set.add(ip1);
		System.out.println(ip1);
		System.out.println(ip2);
		System.out.println(ip1.hashCode());
		System.out.println(ip2.hashCode());
		System.out.println(ip1.equals(ip2));
		System.out.println(set.contains(ip1));
		System.out.println(set.contains(ip2));

	}
}
