package FIFO_buffers;

import java.util.LinkedList;
import java.util.Queue;

public class RegionBoundedBufferFIFO<T> implements BoundedBuffer<T> {

	private Queue<T> buffer = new LinkedList<>();
	private Object lock = new Object();
	private static long ticket = 0, next = 0;
	
	@Override
	public void put(T data) {
		synchronized (lock) {
			buffer.add(data);
			lock.notifyAll();
		}
		
	}

	@Override
	public T get() {
		T data;
		synchronized (lock) {
			long myTicket = ticket++;
			while (myTicket != next || buffer.isEmpty()) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					
				}
			}
			next++;
			data = buffer.poll();
			
			if (ticket != next && !buffer.isEmpty()) {
				lock.notifyAll();
			} else if (ticket == next) {
				ticket = next = 0;
			}
		}
		
		return data;
	}
	
}
