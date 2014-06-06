package listener;

import hello.HelloThread;

import java.nio.ByteBuffer;

public class MessageThread implements Runnable
{
	HelloTable table;
	ByteBuffer message;
	
	MessageThread(ByteBuffer message)
	{
		this.message = message;
	}
	
	public void run()
	{
		switch (message.getChar()) {
		case 'h':
			new HelloThread(table, message);
		}
	}

}
