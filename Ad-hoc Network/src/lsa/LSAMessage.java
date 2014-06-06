package lsa;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import exceptions.WrongMessageType;


public class LSAMessage
{
	private InetAddress sourceAddress;
	private LinkedList<InetAddress> neighboursAdresses;
	private short sequenceNumber;
	private short numberOfNeighbors;
	
	LSAMessage(ByteBuffer message)
	{
		char type = message.getChar();
		
		if (type != 'l')
			throw new WrongMessageType();
		
		message.getChar();
		message.get();
		short messageSize = message.getShort();
		byte[] byteAddress = new byte[4];
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
		sequenceNumber = message.getShort();
		numberOfNeighbors = message.getShort();
		for (int i = 0; i < numberOfNeighbors ; i++) {
			
			for (int j = 0; j < 4; j++) {
				byteAddress[j]=message.get();
			}
			try {
				neighboursAdresses.add(InetAddress.getByAddress(byteAddress));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	public LSAMessage(InetAddress myAddress, short sequenceNumber)
	{
		sourceAddress = myAddress;
		numberOfNeighbors = 0;
		neighboursAdresses = new LinkedList<InetAddress>();
		this.sequenceNumber = sequenceNumber;
	}
	
	ByteBuffer toBuffer()
	{
		short messageSize = (short) (numberOfNeighbors*4 + 12);
		ByteBuffer buffer = ByteBuffer.allocate(messageSize);
		buffer.putChar('l');
		buffer.putShort(messageSize);
		buffer.put(sourceAddress.getAddress());
		buffer.putShort(sequenceNumber);
		buffer.putShort(numberOfNeighbors);
		for (InetAddress address : neighboursAdresses)
			buffer.put(address.getAddress());
		return buffer;
		
	}
	
	public void addNeighbor(InetAddress neighbor)
	{
		numberOfNeighbors++;
		neighboursAdresses.add(neighbor);
	}
}
