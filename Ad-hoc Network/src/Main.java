import hello.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import sender.Sender;


public class Main {

	public static void main(String[] args) throws SocketException {

		//System.out.println(buffer.toString());
		//System.out.println(buffer.getChar());

		//new NetworkManager();

		ByteBuffer listeningBuffer = ByteBuffer.allocate(65536);
		long timeout = 100;
		DatagramSocket socket = new DatagramSocket();
		DatagramChannel channel = null;
		try {
			channel = DatagramChannel.open();
			InetSocketAddress local = new InetSocketAddress(1234); 
			channel.bind(local); 
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			channel.socket().setSoTimeout((int) timeout);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		long t0 = System.currentTimeMillis();
		try {
			/*while((timeout >0)
					&&!(InetAddress.getLocalHost()).equals(
					channel.receive(listeningBuffer))){
				System.out.println("Own address");
				timeout = System.currentTimeMillis() - t0;
				if (timeout > 0)
					channel.socket().setSoTimeout((int) timeout);
				t0 = System.currentTimeMillis();
			}*/
			channel.receive(listeningBuffer);
		}
		catch (IOException e) {
			System.out.println("Nothing received");
			return;
		}
	}

}
