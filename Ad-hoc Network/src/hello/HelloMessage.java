package hello;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import listener.MessageThread;
import exceptions.WrongMessageType;

public class HelloMessage
{
	private InetAddress sourceAddress;
	private LinkedList<InetAddress> heardNeighbors;
	private int numberOfHeard;
	private LinkedList<InetAddress> symmetricNeighbors;
	private int numberOfSymmetric;


	public HelloMessage(InetAddress myAddress)
	{
		sourceAddress = myAddress;
		numberOfHeard = 0;
		heardNeighbors = new LinkedList<InetAddress>();
		numberOfSymmetric = 0;
		symmetricNeighbors = new LinkedList<InetAddress>();
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
			try {
				sourceAddress =
						InetAddress.getByAddress(byteAddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			//Heard neighbors listing
			numberOfHeard = message.get();
			message.get();
			message.get();
			message.get();
			heardNeighbors = new LinkedList<InetAddress>();
			for (int i = 0; i<numberOfHeard; i++) {
				for (int j = 0; j<4; j++) {
					byteAddress[j] = message.get();
				}
				try {
					heardNeighbors.add(
							InetAddress.getByAddress(byteAddress));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			//Symmetric neighbors listing
			numberOfSymmetric = message.get();
			message.get();
			message.get();
			message.get();
			symmetricNeighbors = new LinkedList<InetAddress>();
			for (int i = 0; i<numberOfSymmetric; i++) {
				for (int j = 0; j<4; j++) {
					byteAddress[j] = message.get();
				}
				try {
					symmetricNeighbors.add(
							InetAddress.getByAddress(byteAddress));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
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
		for (InetAddress neighbor : heardNeighbors) {
			System.out.print("#  ");
			System.out.println(neighbor);
		}
		System.out.print("#  ");
		System.out.println(numberOfSymmetric+" symmetric neighbors");
		for (InetAddress neighbor : symmetricNeighbors) {
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
		buffer.put(sourceAddress.getAddress());
		buffer.put((byte) numberOfHeard);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		for (InetAddress address : heardNeighbors) {
			buffer.put(address.getAddress());
		}
		buffer.put((byte) numberOfSymmetric);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		for (InetAddress address : symmetricNeighbors) {
			buffer.put(address.getAddress());
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

	public void addHeard(InetAddress address)
	{
		numberOfHeard++;
		heardNeighbors.add(address);
	}

	public void addSymmetric(InetAddress address)
	{
		numberOfSymmetric++;
		symmetricNeighbors.add(address);
	}

	public boolean isSymmetric(InetAddress myAddress)
	{
		for (InetAddress neighbor : heardNeighbors)
			if (myAddress.equals(neighbor))
				return true;
		for (InetAddress neighbor : symmetricNeighbors)
			if (myAddress.equals(neighbor))
				return true;
		return false;
	}
}
