package FIFO_buffers;

import java.util.LinkedList;
import java.util.Queue;

public class MonitorBoundedBufferFIFO<T> implements BoundedBuffer<T> {
	
	private static long ticket = 0, next = 0;
	private Queue<T> buffer = new LinkedList<>();

	@Override
	public synchronized void put(T data) {
		
		buffer.add(data);
		notifyAll();
	}

	@Override
	public synchronized T get() {
		long myTicket = ticket++;
		while (myTicket != next || buffer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) { }
		}
		
		next++;
		T data = buffer.poll();
		
		if (ticket != next && !buffer.isEmpty()) {
			notifyAll();
		} else if (ticket == next) {
			ticket = next = 0;
		}
		
		return data;
	
	}

	
}
