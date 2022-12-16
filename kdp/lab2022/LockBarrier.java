package kdp.lab2022;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBarrier implements Barrier {
	
	private final Lock lock = new ReentrantLock();
	private final Condition cond = lock.newCondition();
	
	private int current = 0;
	private int count;
	
	public LockBarrier (int count) {
		this.count = count;
	}
	
	@Override
	public void arrived() {
		lock.lock();
		
		try {
			if (++current == count) {
				cond.signal();
			}
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void await() {
		lock.lock();
		try {
			while (current != count) {
				try {
					cond.await();
				} catch (InterruptedException e) { }
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	
	
}
