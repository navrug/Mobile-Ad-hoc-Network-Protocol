import hello.HelloMessage;
import hello.HelloTable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import listener.MessageThread;
import lsa.LSAMessage;
import lsa.LSATable;


public class ChannelManager implements Runnable
{
	private HelloTable helloTable;
	private LSATable lsaTable;
	private DatagramChannel channel;
	BlockingQueue<ByteBuffer> queue;
	private static int helloPeriod = 2000;
	private static int deviationRange = 100;
	
	ChannelManager(HelloTable helloTable,
			LSATable lsaTable)
	{
		this.helloTable = helloTable;
		this.lsaTable = lsaTable;
		queue = new LinkedBlockingQueue<ByteBuffer>();
		try {
			channel = DatagramChannel.open();
			InetSocketAddress local = new InetSocketAddress(1234); 
			channel.bind(local); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static ByteBuffer adaptBuffer(ByteBuffer original)
	{
		ByteBuffer clone = ByteBuffer.allocate(original.limit());
		for (int pos = 0; pos<original.limit(); pos++) {
			clone.put(original.get());
		}
		clone.flip();
		return clone;
	}
	
	private void listen(ByteBuffer listeningBuffer, int timeout) 
			throws IOException
	{
		channel.socket().setSoTimeout(timeout);
		try {
			while(!(InetAddress.getLocalHost()).equals(channel.receive(listeningBuffer)))
				System.out.println("Own address");
			}
		catch (SocketException e) {
			System.out.println("Nothing received");
			return;
		}
			listeningBuffer.flip(); // consult mode
		try {
			queue.put(adaptBuffer(listeningBuffer));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		listeningBuffer.flip(); // fill mode
	}
	
	public void run() {
		Random r = new Random(System.currentTimeMillis());
		HelloMessage hello;
		LSAMessage lsa;
		ByteBuffer sendingBuffer;
		ByteBuffer listeningBuffer = ByteBuffer.allocate(65535);
		BlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<ByteBuffer>();
		new Thread(
				new MessageThread(helloTable,
						lsaTable,
						queue),
				"MessageThread").start();
		try {
			InetSocketAddress broadcast =
					new InetSocketAddress("255.255.255.255", 1234); 
			/*
			 * An iteration of the loop contains two hellos and one LSA
			 * in the following pattern :
			 * H - full period - H - half period - LSA - half period - 
			 * that is one hell per period and one LSA every two periods
			 */
			while (true) {
				System.out.println("New iteration of sender");
				hello = helloTable.createHello();
				sendingBufferhello.toBuffer();
				sendingBuffer.flip(); // now in consult mode
				channel.send(
						sendingBuffer,
						broadcast);
				sendingBuffer.flip(); // now in fill mode
				listen(listeningBuffer, helloPeriod + r.nextInt(2*deviationRange)-deviationRange);
				hello = helloTable.createHello();
				sendingBuffer = hello.toBuffer();
				sendingBuffer.flip(); // consult mode
				channel.send(sendingBuffer,broadcast);
				sendingBuffer.flip(); // fill mode
				listen(listeningBuffer, helloPeriod/2 + r.nextInt(2*deviationRange)-deviationRange);
				lsa = helloTable.createLSA();
				sendingBuffer = lsa.toBuffer();
				sendingBuffer.flip(); // consult mode
				channel.send(sendingBuffer,broadcast);
				sendingBuffer.flip(); // fill mode
				listen(listeningBuffer, helloPeriod/2 + r.nextInt(2*deviationRange)-deviationRange);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
