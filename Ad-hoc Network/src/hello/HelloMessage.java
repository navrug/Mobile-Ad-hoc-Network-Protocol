package hello;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class HelloMessage
{
	private LinkedList<InetAddress> heardNeighbours;
	private LinkedList<InetAddress> symmetricNeighbours;
	
	HelloMessage(ByteBuffer message)
	{
		byte[] byteAddress = new byte[4];
		if (message.getChar() == 'h') {
			message.get();
			message.get();
			message.get();
			//Heard neighbours listing
			int numberOfHeard = message.getShort();
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
			int numberOfSymmetric = message.getShort();
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
	}

	public ByteBuffer toBuffer() 
	{
		int bufferSize = 32*(3 
				+ heardNeighbours.size()
				+ symmetricNeighbours.size());
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.putChar('h');
		buffer.put((byte) 0);
		buffer.putShort(value)

		
	}

}
