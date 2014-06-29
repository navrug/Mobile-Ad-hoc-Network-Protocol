package lsa;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import utilities.IP;
import listener.MessageThread;
import exceptions.WrongMessageType;


public class LSAMessage
{
	private final IP sourceAddress;
	private final LinkedList<IP> neighborsAddresses;
	private short sequenceNumber;
	private short numberOfNeighbors;
	
	public LSAMessage(ByteBuffer message)
	{
		byte type = message.get();
		if (type != MessageThread.lsaType)
			throw new WrongMessageType();
		boolean isInternetProvider=(message.get()==1);
		message.get();
		message.get();
		byte[] byteAddress = new byte[4];
		//Getting source address
		for (int j = 0; j<4; j++) {
			byteAddress[j] = message.get();
		}
		sourceAddress = new IP(byteAddress);
		sourceAddress.setInternetProvider(isInternetProvider);
		sequenceNumber = message.getShort();
		numberOfNeighbors = message.getShort();
		neighborsAddresses = new LinkedList<IP>();
		for (int i = 0; i < numberOfNeighbors ; i++) {
			
			for (int j = 0; j < 4; j++) {
				byteAddress[j]=message.get();
			}
			neighborsAddresses.add(new IP(byteAddress));
		}
	}
	
	public LSAMessage(IP myAddress, short sequenceNumber)
	{
		sourceAddress = myAddress;
		numberOfNeighbors = 0;
		neighborsAddresses = new LinkedList<IP>();
		this.sequenceNumber = sequenceNumber;
	}
	
	//Allows comparison of two LSAs of different sequence number
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof LSAMessage))
			return false;
		LSAMessage m = (LSAMessage) o;
		return  m.sourceAddress.equals(sourceAddress)
				&& m.numberOfNeighbors == numberOfNeighbors
				&& m.neighborsAddresses.equals(neighborsAddresses);
	}
	
	//Discards comparison of LSA from different sources
	@Override 
	public int hashCode()
	{
		return 2*sourceAddress.hashCode();
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
		System.out.print("From : "+sourceAddress);
		if(sourceAddress.isInternetProvider()) {
			System.out.println(" is internet pro.");
		}
		
		
		System.out.print("#  ");
		System.out.print("Sequence number : "+sequenceNumber);
		System.out.print("        ");
		System.out.println(numberOfNeighbors+" neighbors");
		for (IP neighbor : neighborsAddresses) {
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
		if(sourceAddress.isInternetProvider()) buffer.put((byte)1);
		else buffer.put((byte)0);
		buffer.putShort(messageSize);
		buffer.put(sourceAddress.toBytes());
		buffer.putShort(sequenceNumber);
		buffer.putShort(numberOfNeighbors);
		for (IP address : neighborsAddresses)
			buffer.put(address.toBytes());
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

	
	public void addNeighbor(IP neighbor)
	{
		numberOfNeighbors++;
		neighborsAddresses.add(neighbor);
	}

	public LinkedList<IP> neighbors()
	{
		return neighborsAddresses;
	}
	
	public IP source()
	{
		return sourceAddress;
	}
}
