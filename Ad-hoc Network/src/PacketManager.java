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
import java.util.concurrent.locks.ReentrantLock;

import utilities.IP;
import utilities.SystemCommand;
import listener.MessageThread;
import lsa.LSAMessage;
import lsa.LSATable;


public class PacketManager implements Runnable 
{
	private final HelloTable helloTable = new HelloTable();
	private final ReentrantLock netlock = new ReentrantLock();
	private final LSATable lsaTable = new LSATable(netlock);
	private DatagramSocket socket;
	private BlockingQueue<ByteBuffer> queue;
	private final static int helloPeriod = 2000;
	private final static int deviationRange = 100;

	PacketManager()
	{
		IP.defineIP();
		queue = new LinkedBlockingQueue<ByteBuffer>();
		try {
			socket = new DatagramSocket(1234,
					InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		netlock.lock();
		try {
			System.out.println("[PacketManaer] Attribution d'un adresse ip");
			
			
			
			
			
			SystemCommand.cmdExec("ip addr flush dev " + "eth0");
			SystemCommand.cmdExec("ip route flush dev " + "eth0");
			
			
			
			SystemCommand.cmdExec("ip addr add " + IP.myIP() + "/16 dev " + "eth0" + " brd +");
			SystemCommand.cmdExec("ip route add to default via " + IP.myIP());
		} finally {
			netlock.unlock();
		}
	}


	private void listen(byte[] listeningBuffer, int timeout) 
			throws IOException
	{
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
		netlock.lock();
		try {
			socket.setSoTimeout(timeout);
		}
		finally {
			netlock.unlock();
		}
		DatagramPacket packet = 
				new DatagramPacket(listeningBuffer,
						listeningBuffer.length);		
		ByteBuffer buffer;
		int numberOfPackets = 0;
		try {
			while(System.currentTimeMillis()-ti<timeout) {
				netlock.lock();
				try {
					socket.receive(packet);
				}
				finally {
					netlock.unlock();
				}
				/*
				 * Debugging
				 */
				if (!blacklist.contains(new IP(packet.getAddress()))) {
					/*
					 * Debugging
					 */

					if (!IP.myIP().equals(new IP(packet.getAddress()))) {
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
							System.out.println(
									"[PacketManager] LSA forwarded.");
							safeSend(socket, packet);
						}
						queue.add(buffer);
					}
					else
						System.out.println(
								"[PacketManager] Received from own address : "
										+ IP.myIP());
					/*
					 * Debugging
					 */
				}
				/*
				 * Debugging
				 */

				t = System.currentTimeMillis();
				if (timeout-(t-ti)>10) {
					netlock.lock();
					try {
						socket.setSoTimeout((int)(timeout-(t-ti)));
					}
					finally {
						netlock.unlock();
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

	private void safeSend(DatagramSocket socket, DatagramPacket packet)
	{
		netlock.lock();
		try {
			try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finally {
			netlock.unlock();
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
				hello = helloTable.createHello();
				safeSend(socket, hello.toPacket());
				System.out.println("[PacketManager] Hello sent.");
				listen(listenData, helloPeriod 
						+ r.nextInt(2*deviationRange)-deviationRange);
				hello = helloTable.createHello();
				safeSend(socket, hello.toPacket());
				System.out.println("[PacketManager] Hello sent.");
				listen(listenData, helloPeriod/2 
						+ r.nextInt(2*deviationRange)-deviationRange);
				lsa = helloTable.createLSA();		
				safeSend(socket, lsa.toPacket());
				System.out.println("[PacketManager] LSA #"+lsa.sequenceNumber()+" sent.");
				listen(listenData, helloPeriod/2 
						+ r.nextInt(2*deviationRange)-deviationRange);
				helloTable.checkDeadNodes();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
