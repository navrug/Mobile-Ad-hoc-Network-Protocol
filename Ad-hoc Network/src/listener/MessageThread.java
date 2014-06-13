package listener;

import hello.HelloTable;
import hello.HelloThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import lsa.LSATable;
import lsa.LSAThread;

public class MessageThread implements Runnable
{
	HelloTable helloTable;
	LSATable lsaTable;
	ByteBuffer message;

	MessageThread(ByteBuffer message)
	{
		this.message = message;
	}

	public void run()
	{
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
			new HelloThread(helloTable, sourceAddress, message);
		case 'l':
			new LSAThread(lsaTable, sourceAddress, message);
		}
	}

}
