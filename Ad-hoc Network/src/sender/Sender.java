package sender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import hello.HelloMessage;
import hello.HelloTable;

/*
 * This class is used to periodically send Hello and LSA messages
 */
public class Sender implements Runnable
{
	HelloTable table;

	Sender(HelloTable table)
	{
		this.table = table;
	}


	public void run() {
		HelloMessage hello;
		ByteBuffer buffer;
		try (DatagramChannel client = DatagramChannel.open()) {
			InetSocketAddress local = new InetSocketAddress(1234); 
			client.bind(local); 
			InetSocketAddress broadcast =
					new InetSocketAddress("255.255.255.255", 1234); 
			while (true) {
				hello = table.createHello();
				buffer = hello.toBuffer();
				buffer.flip();
				client.send(buffer,broadcast);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
