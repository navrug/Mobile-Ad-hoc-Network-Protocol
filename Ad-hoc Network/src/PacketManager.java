import hello.HelloMessage;
import hello.HelloTable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
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

	private final ReentrantLock netlock = new ReentrantLock();
	private final LSATable lsaTable = new LSATable(netlock);
	private final HelloTable helloTable = new HelloTable(lsaTable);
	private DatagramSocket socket;
	private BlockingQueue<ByteBuffer> queue;
	private final static int helloPeriod = 2000;
	private final static int deviationRange = 100;

	// Implements the Singleton design pattern
	private static class PacketManagerHolder {
		static private final PacketManager instance = 
				new PacketManager();
	}

	public static PacketManager getInstance()
	{
		return PacketManagerHolder.instance;
	}	

	private PacketManager()
	{
		IP.defineIP();
		queue = new LinkedBlockingQueue<ByteBuffer>();
		netlock.lock();
		try {
			System.out.println("[PacketManager] Attribution d'un adresse ip");
			SystemCommand.cmdExec("echo 1 > /proc/sys/net/ipv4/ip_forward");
			SystemCommand.cmdExec("ifconfig " + IP.myIface() + " down");
			SystemCommand.cmdExec("ifconfig " + IP.myIface() + " up");
			SystemCommand.cmdExec("ip addr flush dev " + IP.myIface());
			SystemCommand.cmdExec("ip route flush dev " + IP.myIface());
			SystemCommand.cmdExec("ifconfig " + IP.myIface() + " " + IP.myIP() + " netmask 255.255.0.0 broadcast 1.1.255.255");
			SystemCommand.cmdExec("ip addr add " + IP.myIP() + "/32 dev " + IP.myIface() + " brd +");
			SystemCommand.cmdExec("ip route add to default via " + IP.myDefaultRoute());
		} finally {
			netlock.unlock();
		}
		try {

			//			NetworkInterface nif = NetworkInterface.getByName(IP.myIface());
			//			Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();		

			socket = new DatagramSocket(1234,
					InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
		} catch (IOException e) {
			e.printStackTrace();
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

				buffer = ByteBuffer.allocate(
						packet.getData().length);
				buffer.put(packet.getData());
				buffer.flip(); // consult mode

				byte[] byteAddress = new byte[4];
				//Getting source address
				for (int j = 0; j<4; j++) {
					byteAddress[j] = buffer.array()[j+4];
				}

				IP senderIP = new IP(byteAddress);
				/*
				 * Debugging
				 */
				if (!blacklist.contains(new IP(packet.getAddress()))) {
					/*
					 * Debugging
					 * 
					 */
					if (!IP.myIP().equals(senderIP)) {
						System.out.println(
								"[PacketManager] Packet received from "
										+senderIP);
						numberOfPackets++;
						if (buffer.array()[0]==MessageThread.lsaType
								&& lsaTable.isLatest(buffer)) {
							System.out.println(
									"[PacketManager] LSA forwarded.");
							safeForward(socket, packet);
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
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			netlock.unlock();
		}
	}

	private void safeForward(DatagramSocket socket, DatagramPacket packet)
	{
		DatagramPacket newPacket =null;
		try {
			newPacket = new DatagramPacket(packet.getData(),
					packet.getLength(),
					InetAddress.getByName("255.255.255.255"),
					1234);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		netlock.lock();
		try {
			socket.send(newPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			netlock.unlock();
		}
	}

	@Override
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

