package listener;

import hello.HelloMessage;
import hello.HelloTable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import lsa.LSAMessage;
import lsa.LSATable;

public class MessageThread implements Runnable
{
	HelloTable helloTable;
	LSATable lsaTable;
	BlockingQueue<ByteBuffer> queue;

	MessageThread(HelloTable helloTable,
			LSATable lsaTable,
			BlockingQueue<ByteBuffer> queue)
	{
		this.helloTable = helloTable;
		this.lsaTable = lsaTable;
		this.queue = queue;
	}
	
	/*
	 * Takes a ByteBuffer in consult mode, return a buffer in
	 * consult mode
	 */
	private static ByteBuffer adaptBuffer(ByteBuffer original)
	{
		ByteBuffer clone = ByteBuffer.allocate(original.limit());
		for (int pos = 0; pos < original.limit(); pos++)
			clone.put(original.get());
		clone.flip();
		return clone;
	}

	public void run()
	{
		ByteBuffer message = null;
		try {
			message = queue.take();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		char type = message.getChar();
		message.get();
		message.get();
		message.get();
		InetAddress sourceAddress = null;
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
		switch (type) {
		case 'h':
			helloTable.addHello(sourceAddress,
					new HelloMessage(adaptBuffer(message)));
			break;
		case 'l':
			lsaTable.addLSA(sourceAddress, 
					new LSAMessage(adaptBuffer(message)));
			break;
		}
	}

}