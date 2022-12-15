package kdp_filmovi;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBoundedBuffer<T> implements BoundedBuffer<T> {
	
	private final Lock lock = new ReentrantLock();
	private final Condition cond = lock.newCondition();
	private Queue<T> buffer = new LinkedList<>();

	@Override
	public void put(T data) {
		lock.lock();
		try {
			buffer.add(data);
			cond.signal();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T get() {
		T data;
		
		lock.lock();
		try {
			while (buffer.size() == 0) {
				try {
					cond.await();
				} catch (InterruptedException e) { }
			}
			data = buffer.poll();
			
		} finally {
			lock.unlock();
		}
		
		return data;
	}

	@Override
	public T pollFirst() {
		T data;
		
		lock.lock();
		try {
			data = buffer.poll();
		} finally {
			lock.unlock();
		}
		
		return data;
	}
	
	
}
