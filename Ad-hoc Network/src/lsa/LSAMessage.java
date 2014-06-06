package lsa;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;


public class LSAMessage
{
	private LinkedList<InetAddress> neighboursAdresses;
	short sequenceNumber;
	short numberofNeighbours;
	
	LSAMessage(ByteBuffer message)
	{
		char type = message.getChar();
		
		if (type != 'l') 
		
		message.getChar();
		short messageSize = message.getShort();
		sequenceNumber = message.getShort();
		numberofNeighbours = message.getShort();
		
		
		byte[] addresse = new byte[4];
		for (int i = 0; i < numberofNeighbours ; i++) {
			
			for (int j = 0; j < 4; j++) {
				addresse[j]=message.get();
			}
			try {
				neighboursAdresses.add(InetAddress.getByAddress(addresse));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	ByteBuffer toBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(1000);
	}
	
}
