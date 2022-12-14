package kdp_glumci;

import java.util.LinkedList;
import java.util.Queue;

public class MonitorBoundedBuffer<T> implements BoundedBuffer<T> {
	
	private Queue<T> buffer;
	
	public MonitorBoundedBuffer () {
		this.buffer = new LinkedList<>();
	}

	@Override
	public synchronized void put(T data) {
		
		buffer.add(data);
		notify();
	}

	@Override
	public synchronized T get() {
		
		while (buffer.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) { }
		}
		
		return buffer.poll();
	}

	@Override
	public synchronized T pollFirst() {
		return buffer.poll();
	}

}
