import hello.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import lsa.LSAMessage;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {

		new NetworkManager();

		/*HelloMessage hello = new HelloMessage(InetAddress.getLocalHost());
		hello.display();
		HelloTable htable = new HelloTable();
		htable.addHello(InetAddress.getLocalHost(), hello);
		LSAMessage lsa = htable.createLSA();
		System.out.println(lsa.sequenceNumber());
		lsa.display();
		for (int i = 0; i<10; i++)
			System.out.println((byte)lsa.toBuffer().array()[i]);
		ByteBuffer buffer =lsa.toBuffer();
		buffer.flip();
		
		System.out.println((byte) buffer.get());
		System.out.println((byte) buffer.get());
		System.out.println(buffer.getShort());
		System.out.println(127+(byte) buffer.get());
		System.out.println(127+(byte) buffer.get());
		System.out.println(127+(byte) buffer.get());
		System.out.println(127+(byte) buffer.get());
		
		System.out.println(buffer.getDouble());
		System.out.println(buffer.getShort());

		System.out.println("----------");
		System.out.println(256*(127+lsa.toBuffer().array()[8])+(127+lsa.toBuffer().array()[9]));
		System.out.println((127+lsa.toBuffer().array()[8])+256*(127+lsa.toBuffer().array()[9]));
		System.out.println(256*lsa.toBuffer().array()[8]+lsa.toBuffer().array()[9]);
		System.out.println(lsa.toBuffer().array()[8]+256*lsa.toBuffer().array()[9]);
	*/
	}
}
