package kdp;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreBarrier implements Barrier {
	
	private final Semaphore mutex = new Semaphore(1);
	private final Semaphore barrier = new Semaphore(0);
	private int current = 0;
	private int count;
	
	public SemaphoreBarrier (int count) {
		this.count = count;
	}

	@Override
	public void arrived() {
		mutex.acquireUninterruptibly();
		if (++current == count) {
			barrier.release();
		}
		
		mutex.release();
	}

	@Override
	public void await() {
		await(0);
	}

	@Override
	public boolean await(long timeout) {
		
		try {
			if (timeout == 0) {
				barrier.acquireUninterruptibly();
			} else {
				barrier.tryAcquire(timeout, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) { }
			
		return current == count;
	}
	
}
