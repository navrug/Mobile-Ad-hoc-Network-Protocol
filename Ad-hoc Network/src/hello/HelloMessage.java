package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import exceptions.WrongMessageType;

public class HelloMessage
{
	private LinkedList<InetAddress> heardNeighbours;
	private int numberOfHeard;
	private LinkedList<InetAddress> symmetricNeighbours;
	private int numberOfSymmetric;
	
	HelloMessage(ByteBuffer message)
	{
		byte[] byteAddress = new byte[4];
		if (message.getChar() == 'h') {
			message.get();
			message.get();
			message.get();
			//Heard neighbours listing
			numberOfHeard = message.getShort();
			message.get();
			message.get();
			message.get();
			heardNeighbours = new LinkedList<InetAddress>();
			for (int i = 0; i<numberOfHeard; i++) {
				for (int j = 0; j<4; j++) {
					byteAddress[j] = message.get();
				}
				try {
					heardNeighbours.add(
							InetAddress.getByAddress(byteAddress));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			//Symmetric neighbours listing
			numberOfSymmetric = message.getShort();
			message.get();
			message.get();
			message.get();
			heardNeighbours = new LinkedList<InetAddress>();
			for (int i = 0; i<numberOfHeard; i++) {
				for (int j = 0; j<4; j++) {
					byteAddress[j] = message.get();
				}
				try {
					symmetricNeighbours.add(
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
		for (InetAddress address : heardNeighbours) {
			byteAddress = address.getAddress();
			for (int j = 0; j<4; j++) {
				buffer.put(byteAddress[j]);
			}
		}
		buffer.put((byte) numberOfSymmetric);
		for (InetAddress address : symmetricNeighbours) {
			byteAddress = address.getAddress();
			for (int j = 0; j<4; j++) {
				buffer.put(byteAddress[j]);
			}
		}
		return buffer;
	}

}
