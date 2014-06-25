package routing;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import lsa.LSATable;

public class RoutingThread implements Runnable
{
	LSATable lsaTable;
	RoutingTable table;
	ReentrantLock lock;
	Condition notUpdated;

	public RoutingThread(LSATable lsaTable,
			ReentrantLock lock,
			Condition notUpdated)
	{

		table=new RoutingTable();
		this.lsaTable = lsaTable;
		this.lock = lock;
		this.notUpdated = notUpdated;
		
		try {
		Runtime.getRuntime().exec("echo 1 > /proc/sys/net/ipv4/ip_forward");
	} catch (IOException e1) {
		e1.printStackTrace();
	}
		
	}

	public void run()
	{	
		
		lock.lock();
		try {
			while (true) {
				notUpdated.await();
				table.updateGraph(lsaTable); 
				table.writeTable();
			} 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
