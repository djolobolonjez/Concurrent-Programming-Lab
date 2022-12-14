package kdp;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SemaphoreBoundedBuffer<T> implements BoundedBuffer<T> {
	
	private Queue<T> buffer = new LinkedList<>();
	
	private final Semaphore mutex = new Semaphore(1);
	private final Semaphore itemAvailable = new Semaphore(0);

	@Override
	public void put(T data) {
		
		mutex.acquireUninterruptibly();
		buffer.add(data);
		
		mutex.release();
		itemAvailable.release();
	}

	@Override
	public T get() {
		itemAvailable.acquireUninterruptibly();
		mutex.acquireUninterruptibly();
		
		T data = buffer.poll();
		mutex.release();
		
		return data;
	}

	@Override
	public T pollFirst() {
		if (itemAvailable.tryAcquire() == false) {
			return null;
		}
		
		mutex.acquireUninterruptibly();
		T data = buffer.poll();
		mutex.release();
		
		return data;
	}

}
