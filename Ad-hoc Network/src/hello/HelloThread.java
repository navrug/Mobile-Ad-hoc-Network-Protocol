package hello;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class HelloThread implements Runnable
{
	HelloTable table;
	InetAddress neighbor;
	ByteBuffer message;
	
	HelloThread(
			HelloTable table,
			InetAddress neighbor,
			ByteBuffer message)
	{
		this.table = table;
		this.neighbor = neighbor;
		this.message = message;
	}

	public void run() {
		table.addHello(neighbor, new HelloMessage(message));
	}

}
