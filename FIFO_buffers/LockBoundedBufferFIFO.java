package FIFO_buffers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBoundedBufferFIFO<T> implements BoundedBuffer<T> {
	
	private Queue<T> buffer = new LinkedList<>();
	private Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();
	private static long ticket = 0, next = 0;
	
	@Override
	public void put(T data) {
		lock.lock();
		try {
			buffer.add(data);
			cond.signalAll();
		} finally {
			lock.unlock();
		}
		
	}
	@Override
	public T get() {
		T data;
		lock.lock();
		try {
			long myTicket = ticket++;
			while (myTicket != next || buffer.isEmpty()) {
				cond.awaitUninterruptibly();
			}
			next++;
			data = buffer.poll();
			
			if (ticket != next && !buffer.isEmpty()) {
				cond.signalAll();
			} else if (ticket == next) {
				ticket = next = 0;
			}
			
		} finally {
			lock.unlock();
		}
		
		return data;
	}
}
