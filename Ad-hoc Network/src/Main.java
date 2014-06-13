import hello.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import sender.Sender;


public class Main {

	public static void main(String[] args) {

		HelloTable table = new HelloTable();
		HelloMessage message = table.createHello();
		ByteBuffer buffer = message.toBuffer();
		buffer.flip();
		InetAddress myAddress = null;
		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(buffer.toString());
		System.out.println(buffer.getChar());
		//Thread hello = new Thread(new HelloThread(table, myAddress, buffer), "hello");
		//hello.start();
		Thread sender = new Thread(new Sender(table), "sender");
		sender.start();
	}

}
