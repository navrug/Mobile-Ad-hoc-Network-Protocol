package routing;

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
		this.lsaTable = lsaTable;
		this.lock = lock;
		this.notUpdated = notUpdated;
	}

	public void run()
	{
		lock.lock();
		try {
			while (true) {
				notUpdated.await();
				table.updateGraph(lsaTable); 
			} 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
