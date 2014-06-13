package sender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;

import lsa.LSAMessage;
import hello.HelloMessage;
import hello.HelloTable;

/*
 * This class is used to periodically send Hello and LSA messages
 */
public class Sender implements Runnable
{
	HelloTable table;
	private DatagramChannel channel;
	private static int helloPeriod = 2000;
	private static int deviationRange = 100;
	
	

	public Sender(HelloTable table,
			DatagramChannel channel)
	{
		this.table = table;
		this.channel = channel;
	}


	public void run() {
		Random r = new Random(System.currentTimeMillis());
		HelloMessage hello;
		LSAMessage lsa;
		ByteBuffer buffer;
		try /*(DatagramChannel client = DatagramChannel.open())*/ {
			/*InetSocketAddress local = new InetSocketAddress(1234); 
			client.bind(local); */
			InetSocketAddress broadcast =
					new InetSocketAddress("255.255.255.255", 1234); 
			/*
			 * An iteration of the loop contains two hellos and one LSA
			 * in the following pattern :
			 * H - full period - H - half period - LSA - half period - 
			 * that is one hell per period and one LSA every two periods
			 */
			while (channel.isOpen()) {
				System.out.println("New iteration of sender");
				hello = table.createHello();
				buffer = hello.toBuffer();
				buffer.flip(); // now in consult mode
				channel.send(buffer,broadcast);
				buffer.flip(); // now in fill mode
				Thread.sleep(helloPeriod + r.nextInt(2*deviationRange)-deviationRange);
				hello = table.createHello();
				buffer = hello.toBuffer();
				buffer.flip(); // consult mode
				channel.send(buffer,broadcast);
				buffer.flip(); // fill mode
				Thread.sleep(helloPeriod/2 + r.nextInt(2*deviationRange)-deviationRange);
				lsa = table.createLSA();
				buffer = lsa.toBuffer();
				buffer.flip(); // consult mode
				channel.send(buffer,broadcast);
				buffer.flip(); // fill mode
				Thread.sleep(helloPeriod/2 + r.nextInt(2*deviationRange)-deviationRange);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
