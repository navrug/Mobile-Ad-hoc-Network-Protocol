package listener;

import hello.HelloTable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lsa.LSATable;

/*
 * This class listens and receives ByteBuffers, identifies which type it
 * is and launches the corresponding thread to operate them.
 */
public class Listener implements Runnable
{
	private HelloTable helloTable;
	private LSATable lsaTable;
	
	Listener(HelloTable helloTable, LSATable lsaTable)
	{
		this.helloTable = helloTable;
		this.lsaTable = lsaTable;
	}
	
	/*
	 * Takes a buffer in consult mode and returns a buffer
	 * in consult mode
	 */
	private static ByteBuffer adaptBuffer(ByteBuffer original)
	{
		ByteBuffer clone = ByteBuffer.allocate(original.limit());
			for (int pos = 0; pos<original.limit(); pos++) {
				clone.put(original.get());
			}
			clone.flip();
	       return clone;
	}

	public void run() {
		ByteBuffer buffer = ByteBuffer.allocate(65535);
		BlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<ByteBuffer>();
		new Thread(
				new MessageThread(helloTable,
						lsaTable,
						queue)).start();
		try (DatagramChannel serverSocket = DatagramChannel.open()) 
		{ 
			InetSocketAddress local = new InetSocketAddress(1234);
			serverSocket.bind(local);
			while (true) { 
				serverSocket.read(buffer);
				buffer.flip(); // consult mode
				queue.put(adaptBuffer(buffer));
				buffer.flip(); // fill mode
			} 

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} 


	}

}
