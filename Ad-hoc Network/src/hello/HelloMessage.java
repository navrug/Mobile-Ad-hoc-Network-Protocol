package hello;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import utilities.IP;
import listener.MessageThread;
import exceptions.WrongMessageType;

public class HelloMessage
{
	private final IP sourceAddress;
	private final LinkedList<IP> heardNeighbors;
	private int numberOfHeard;
	private final LinkedList<IP> symmetricNeighbors;
	private int numberOfSymmetric;


	public HelloMessage(IP myAddress)
	{
		sourceAddress = myAddress;
		numberOfHeard = 0;
		heardNeighbors = new LinkedList<IP>();
		numberOfSymmetric = 0;
		symmetricNeighbors = new LinkedList<IP>();
	}





	public HelloMessage(ByteBuffer message)
	{
		byte[] byteAddress = new byte[4];
		if (message.get() == MessageThread.helloType) {
			message.get();
			message.get();
			message.get();
			//Getting source address
			for (int j = 0; j<4; j++) {
				byteAddress[j] = message.get();
			}
			sourceAddress =	new IP(byteAddress);
			//Heard neighbors listing
			numberOfHeard = message.get();
			message.get();
			message.get();
			message.get();
			heardNeighbors = new LinkedList<IP>();
			for (int i = 0; i<numberOfHeard; i++) {
				for (int j = 0; j<4; j++) {
					byteAddress[j] = message.get();
				}
				heardNeighbors.add(new IP(byteAddress));
			}
			//Symmetric neighbors listing
			numberOfSymmetric = message.get();
			message.get();
			message.get();
			message.get();
			symmetricNeighbors = new LinkedList<IP>();
			for (int i = 0; i<numberOfSymmetric; i++) {
				for (int j = 0; j<4; j++) {
					byteAddress[j] = message.get();
				}
				symmetricNeighbors.add(new IP(byteAddress));
			}
		}
		else throw new WrongMessageType();
	}

	public void display()
	{
		System.out.println("###################################");
		System.out.print("#  ");
		System.out.print("Hello");
		System.out.print("        ");
		System.out.println("Size : "+
				(4*(4 + numberOfHeard + numberOfSymmetric))+" bytes");
		System.out.print("#  ");
		System.out.println("From : "+sourceAddress);
		System.out.print("#  ");
		System.out.println(numberOfHeard+" heard neighbors");
		for (IP neighbor : heardNeighbors) {
			System.out.print("#  ");
			System.out.println(neighbor);
		}
		System.out.print("#  ");
		System.out.println(numberOfSymmetric+" symmetric neighbors");
		for (IP neighbor : symmetricNeighbors) {
			System.out.print("#  ");
			System.out.println(neighbor);
		}
		System.out.println("###################################");
	}

	public boolean equals(HelloMessage message)
	{
		boolean result = message != null
				&& sourceAddress == message.sourceAddress
				&& numberOfHeard == message.numberOfHeard
				&& numberOfSymmetric == message.numberOfSymmetric
				&& heardNeighbors.equals(message.heardNeighbors)
				&& symmetricNeighbors.equals(message.symmetricNeighbors);
		return result;
	}

	public ByteBuffer toBuffer() 
	{
		short bufferSize = (short)
				(4*(4 + numberOfHeard + numberOfSymmetric));
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.put(MessageThread.helloType);
		buffer.put((byte) 0);
		buffer.putShort(bufferSize);
		buffer.put(sourceAddress.toBytes());
		buffer.put((byte) numberOfHeard);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		for (IP address : heardNeighbors) {
			buffer.put(address.toBytes());
		}
		buffer.put((byte) numberOfSymmetric);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		for (IP address : symmetricNeighbors) {
			buffer.put(address.toBytes());
		}
		return buffer;
	}

	public DatagramPacket toPacket() 
	{
		ByteBuffer buffer = toBuffer();
		DatagramPacket packet = null;
		try {
			packet =  new DatagramPacket(buffer.array(),
					buffer.capacity(),
					InetAddress.getByName("255.255.255.255"),
					1234);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return packet;
	}

	public void addHeard(IP address)
	{
		numberOfHeard++;
		heardNeighbors.add(address);
	}

	public void addSymmetric(IP address)
	{
		numberOfSymmetric++;
		symmetricNeighbors.add(address);
	}

	public boolean isSymmetric(IP myAddress)
	{
		for (IP neighbor : heardNeighbors)
			if (myAddress.equals(neighbor))
				return true;
		for (IP neighbor : symmetricNeighbors)
			if (myAddress.equals(neighbor))
				return true;
		return false;
	}
}
