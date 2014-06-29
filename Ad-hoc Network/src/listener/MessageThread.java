package listener;

import hello.HelloMessage;
import hello.HelloTable;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import utilities.IP;
import lsa.LSAMessage;
import lsa.LSATable;

public class MessageThread implements Runnable
{
	private final HelloTable helloTable;
	private final LSATable lsaTable;
	private final BlockingQueue<ByteBuffer> queue;
	public static final byte helloType = 0;
	public static final byte lsaType = 1;

	public MessageThread(HelloTable helloTable,
			LSATable lsaTable,
			BlockingQueue<ByteBuffer> queue)
	{
		this.helloTable = helloTable;
		this.lsaTable = lsaTable;
		this.queue = queue;
	}

	@Override
	public void run()
	{
		while (true) {
			ByteBuffer message = null;
			try {
				System.out.println("[MessageThread] Waiting for a message...");
				message = queue.take();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("[MessageThread] Operating a new message, "
					+queue.size()
					+" messages left to operate.");
			byte type = message.get();
			message.get();
			message.get();
			message.get();
			byte[] byteAddress = new byte[4];
			//Getting source address
			for (int j = 0; j<4; j++) {
				byteAddress[j] = message.get();
			}
			IP sourceAddress = new IP(byteAddress);
			message.position(0);
			switch (type) {
			case helloType:
				HelloMessage hello = new HelloMessage(message);
				hello.display();
				helloTable.addHello(sourceAddress, hello);
				break;
			case lsaType:
				LSAMessage lsa = new LSAMessage(message);
				lsa.display();
				lsaTable.addLSA(sourceAddress, lsa);
				break;
			}
		}
	}

}