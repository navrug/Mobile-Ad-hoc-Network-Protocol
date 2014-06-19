package lsa;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import listener.MessageThread;
import exceptions.WrongMessageType;


public class LSAMessage
{
	private InetAddress sourceAddress;
	private LinkedList<InetAddress> neighborsAdresses;
	private short sequenceNumber;
	private short numberOfNeighbors;
	
	public LSAMessage(ByteBuffer message)
	{
		byte type = message.get();
		if (type != MessageThread.lsaType)
			throw new WrongMessageType();
		message.get();
		message.get();
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
		neighborsAdresses = new LinkedList<InetAddress>();
		for (int i = 0; i < numberOfNeighbors ; i++) {
			
			for (int j = 0; j < 4; j++) {
				byteAddress[j]=message.get();
			}
			try {
				neighborsAdresses.add(InetAddress.getByAddress(byteAddress));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	public LSAMessage(InetAddress myAddress, short sequenceNumber)
	{
		sourceAddress = myAddress;
		numberOfNeighbors = 0;
		neighborsAdresses = new LinkedList<InetAddress>();
		this.sequenceNumber = sequenceNumber;
	}
	
	public void display()
	{
		System.out.println("###################################");
		System.out.print("#  ");
		System.out.print("LSA");
		System.out.print("        ");
		System.out.println("Size : "+
				(numberOfNeighbors*4 + 12)+" bytes");
		System.out.print("#  ");
		System.out.println("From : "+sourceAddress);
		System.out.print("#  ");
		System.out.print("Sequence number : "+sequenceNumber);
		System.out.print("        ");
		System.out.println(numberOfNeighbors+" neighbors");
		for (InetAddress neighbor : neighborsAdresses) {
			System.out.print("#  ");
			System.out.println(neighbor);
		}
		System.out.println("###################################");
	}
	
	public int sequenceNumber()
	{
		return sequenceNumber;
	}
	
	public ByteBuffer toBuffer()
	{
		short messageSize = (short) (numberOfNeighbors*4 + 12);
		ByteBuffer buffer = ByteBuffer.allocate(messageSize);
		buffer.put(MessageThread.lsaType);
		buffer.putShort(messageSize);
		buffer.put(sourceAddress.getAddress());
		buffer.putShort(sequenceNumber);
		buffer.putShort(numberOfNeighbors);
		for (InetAddress address : neighborsAdresses)
			buffer.put(address.getAddress());
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

	
	public void addNeighbor(InetAddress neighbor)
	{
		numberOfNeighbors++;
		neighborsAdresses.add(neighbor);
	}

	public LinkedList<InetAddress> neighbors()
	{
		return neighborsAdresses;
	}
	
	public InetAddress source()
	{
		return sourceAddress;
	}


}
