import hello.HelloMessage;
import hello.HelloTable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import listener.MessageThread;
import lsa.LSAMessage;
import lsa.LSATable;


public class PacketManager implements Runnable 
{
	private HelloTable helloTable;
	private LSATable lsaTable;
	private DatagramSocket socket;
	private BlockingQueue<ByteBuffer> queue;
	private static int helloPeriod = 2000;
	private static int deviationRange = 100;

	PacketManager(HelloTable helloTable,
			LSATable lsaTable)
			{
		this.helloTable = helloTable;
		this.lsaTable = lsaTable;
		queue = new LinkedBlockingQueue<ByteBuffer>();
		try {
			socket = new DatagramSocket(1234, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
			}

	private static ByteBuffer adaptBuffer(ByteBuffer original)
	{
		original.position(0);
		ByteBuffer clone = ByteBuffer.allocate(original.limit());
		for (int pos = 0; pos<original.limit(); pos++) {
			clone.put(original.get());
		}
		clone.flip();
		return clone;
	}

	private void listen(byte[] listeningBuffer, int timeout) 
			throws IOException
			{
		long ti = System.currentTimeMillis();
		long t = ti;
		System.out.println("Listening for "+timeout+" ms");
		socket.setSoTimeout(timeout);
		DatagramPacket packet = 
				new DatagramPacket(listeningBuffer, listeningBuffer.length);		
		ByteBuffer buffer;
		int numberOfPackets = 0;
		try {
			while(System.currentTimeMillis()-ti<timeout) {
				socket.receive(packet);
				//if (!(InetAddress.getLocalHost().getHostAddress()).equals(
				//		packet.getAddress().getHostAddress())) {
					System.out.println(
							"[PacketManager] Packet received from "+packet.getAddress());
					buffer = ByteBuffer.allocate(packet.getData().length);
					buffer.put(packet.getData());
					buffer.flip();
					//Depends on the encoding !!
					if (buffer.array()[0]==0
							&& buffer.array()[0]==108
							&& lsaTable.isLatest(packet.getAddress(), buffer))
						socket.send(packet);
					System.out.println(queue.size());
					queue.add(buffer);
					System.out.println(queue.size());
					System.out.println("[PacketManager] Buffer added to the queue.");
					numberOfPackets++;
				/*}
				else
					System.out.println("Received from own address");*/
				t = System.currentTimeMillis();
				socket.setSoTimeout((int)(timeout-(t-ti)));
			}
		}
		catch (SocketTimeoutException e) {
			System.out.println(
					"Received "+numberOfPackets+" packets in "+timeout+" ms");
		}
			}

	public void run() {
		Random r = new Random(System.currentTimeMillis());
		HelloMessage hello;
		LSAMessage lsa;
		byte[] listenData = new byte [65535];
		queue = new LinkedBlockingQueue<ByteBuffer>();
		new Thread(
				new MessageThread(helloTable,
						lsaTable,
						queue),
				"MessageThread").start();
		System.out.println("[PacketManager] MessageThread launched.");
		try {
			/*
			 * An iteration of the loop contains two hellos and one LSA
			 * in the following pattern :
			 * H - full period - H - half period - LSA - half period - 
			 * that is one hell per period and one LSA every two periods
			 */
			while (true) {
				System.out.println("New iteration of sender");
				hello = helloTable.createHello();
				socket.send(hello.toPacket());
				listen(listenData, helloPeriod 
						+ r.nextInt(2*deviationRange)-deviationRange);
				hello = helloTable.createHello();
				socket.send(hello.toPacket());
				listen(listenData, helloPeriod/2 
						+ r.nextInt(2*deviationRange)-deviationRange);
				lsa = helloTable.createLSA();
				socket.send(lsa.toPacket());
				listen(listenData, helloPeriod/2 
						+ r.nextInt(2*deviationRange)-deviationRange);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}