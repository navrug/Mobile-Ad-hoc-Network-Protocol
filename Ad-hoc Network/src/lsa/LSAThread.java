package lsa;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class LSAThread {
	LSATable table;
	InetAddress neighbor;
	ByteBuffer message;
	
	public LSAThread(
			LSATable table,
			InetAddress neighbor,
			ByteBuffer message)
	{
		this.table = table;
		this.neighbor = neighbor;
		this.message = message;
	}

	public void run() {
		table.addLSA(neighbor, new LSAMessage(message));
	}
}
