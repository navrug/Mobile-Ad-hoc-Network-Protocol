import hello.HelloMessage;
import hello.HelloTable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import utilities.IP;
import listener.MessageThread;
import lsa.LSAMessage;
import lsa.LSATable;


public class PacketManager implements Runnable 
{
	private HelloTable helloTable = new HelloTable();
	private static Lock lock = new ReentrantLock();
	private LSATable lsaTable = new LSATable(lock);
	private DatagramSocket socket;
	private BlockingQueue<ByteBuffer> queue;
	private static int helloPeriod = 2000;
	private static int deviationRange = 100;

	PacketManager()
	{
		queue = new LinkedBlockingQueue<ByteBuffer>();
		try {
			socket = new DatagramSocket(1234,
					InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		lock.lock();
		try {
			Runtime.getRuntime().exec("echo 1 > /proc/sys/net/ipv4/ip_forward");
			Runtime.getRuntime().exec("ip addr flush dev " + "eth0");
			Runtime.getRuntime().exec("ip route flush dev " + "eth0");
			Runtime.getRuntime().exec("ip addr add " + InetAddress.getLocalHost().getHostAddress()  + "/16 dev " + "eth0" + " brd +");
			Runtime.getRuntime().exec("ip route add to default via " + InetAddress.getLocalHost().getHostAddress());
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			lock.unlock();
		}
	}


	private void listen(byte[] listeningBuffer, int timeout) 
			throws IOException
	{
		IP myIP = new IP(InetAddress.getLocalHost());
		long ti = System.currentTimeMillis();
		long t = ti;
		/*
		 * Debugging
		 */
		Blacklist blacklist = new Blacklist();
		//blacklist.add(new IP(192,168,181,130));
		/*
		 * Debugging
		 */
		System.out.println(
				"[PacketManager] Listening for "
						+timeout+" ms...");
		lock.lock();
		try {
			socket.setSoTimeout(timeout);
		}
		finally {
			lock.unlock();
		}

		DatagramPacket packet = 
				new DatagramPacket(listeningBuffer,
						listeningBuffer.length);		
		ByteBuffer buffer;
		int numberOfPackets = 0;
		try {
			while(System.currentTimeMillis()-ti<timeout) {
				lock.lock();
				try {
					socket.receive(packet);
				}
				finally {
					lock.unlock();
				}
				/*
				 * Debugging
				 */
				if (!blacklist.contains(new IP(packet.getAddress()))) {
					/*
					 * Debugging
					 */

					if (!myIP.equals(new IP(packet.getAddress()))) {
						System.out.println(
								"[PacketManager] Packet received from "
										+packet.getAddress().getHostAddress());
						numberOfPackets++;
						buffer = ByteBuffer.allocate(
								packet.getData().length);
						buffer.put(packet.getData());
						buffer.flip(); // consult mode
						//Depends on the encoding !!
						if (buffer.array()[0]==MessageThread.lsaType
								&& lsaTable.isLatest(
										new IP(packet.getAddress()), 
										buffer)) {
							lock.lock();
							try {
								System.out.println(
										"[PacketManager] LSA forwarded.");
								socket.send(packet);
							}
							finally {
								lock.unlock();
							}
						}
						queue.add(buffer);
						//System.out.println(
						//"[PacketManager] Buffer added to the queue.");

					}
					else
						System.out.println(
								"[PacketManager] Received from own address : "
										+ myIP);
					/*
					 * Debugging
					 */
				}
				/*
				 * Debugging
				 */

				t = System.currentTimeMillis();
				if (timeout-(t-ti)>10) {
					lock.lock();
					try {
						socket.setSoTimeout((int)(timeout-(t-ti)));
					}
					finally {
						lock.unlock();
					}	
				}
				else
					timeout = -1;

			}
		}
		catch (SocketTimeoutException e) {

		}
		System.out.println(
				"[PacketManager] Received "
						+numberOfPackets
						+" packets in "+timeout+" ms.");
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
				hello = helloTable.createHello();
				lock.lock();
				try {
					socket.send(hello.toPacket());
				}
				finally {
					lock.unlock();
				}

				System.out.println("[PacketManager] Hello sent.");
				listen(listenData, helloPeriod 
						+ r.nextInt(2*deviationRange)-deviationRange);
				hello = helloTable.createHello();

				lock.lock();
				try {
					socket.send(hello.toPacket());
				}
				finally {
					lock.unlock();
				}

				System.out.println("[PacketManager] Hello sent.");
				listen(listenData, helloPeriod/2 
						+ r.nextInt(2*deviationRange)-deviationRange);
				lsa = helloTable.createLSA();		
				lock.lock();
				try {
					socket.send(lsa.toPacket());
				}
				finally {
					lock.unlock();
				}

				System.out.println("[PacketManager] LSA #"+lsa.sequenceNumber()+" sent.");
				listen(listenData, helloPeriod/2 
						+ r.nextInt(2*deviationRange)-deviationRange);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
