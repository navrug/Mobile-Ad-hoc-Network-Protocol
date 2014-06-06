package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import exceptions.WrongMessageType;

public class HelloMessage
{
	private LinkedList<InetAddress> heardNeighbors;
	private int numberOfHeard;
	private LinkedList<InetAddress> symmetricNeighbors;
	private int numberOfSymmetric;

	HelloMessage()
	{
		numberOfHeard = 0;
		heardNeighbors = new LinkedList<InetAddress>();
		numberOfSymmetric = 0;
		symmetricNeighbors = new LinkedList<InetAddress>();
	}


	HelloMessage(ByteBuffer message)
	{
		byte[] byteAddress = new byte[4];
		if (message.getChar() == 'h') {
			message.get();
			message.get();
			message.get();
			//Heard neighbors listing
			numberOfHeard = message.getShort();
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
			numberOfSymmetric = message.getShort();
			message.get();
			message.get();
			message.get();
			heardNeighbors = new LinkedList<InetAddress>();
			for (int i = 0; i<numberOfHeard; i++) {
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

	public ByteBuffer toBuffer() 
	{
		short bufferSize = (short)
				(4*(3 + numberOfHeard + numberOfSymmetric));
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.putChar('h');
		buffer.put((byte) 0);
		buffer.putShort(bufferSize);
		byte[] byteAddress = new byte[4];
		buffer.put((byte) numberOfHeard);
		for (InetAddress address : heardNeighbors) {
			byteAddress = address.getAddress();
			for (int j = 0; j<4; j++) {
				buffer.put(byteAddress[j]);
			}
		}
		buffer.put((byte) numberOfSymmetric);
		for (InetAddress address : symmetricNeighbors) {
			byteAddress = address.getAddress();
			for (int j = 0; j<4; j++) {
				buffer.put(byteAddress[j]);
			}
		}
		return buffer;
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
