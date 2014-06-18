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
	

		new NetworkManager();


}
}
