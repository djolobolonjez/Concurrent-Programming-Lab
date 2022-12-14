package kdp_glumci;

import java.util.LinkedList;
import java.util.Queue;

public class RegionBoundedBuffer<T> implements BoundedBuffer<T> {
	
	private Queue<T> buffer = new LinkedList<>();

	@Override
	public void put(T data) {
		
		synchronized (buffer) {
			buffer.add(data);
			buffer.notify();
		}
		
	}

	@Override
	public T get() {
		T data;
		
		synchronized (buffer) {
			while (buffer.size() == 0) {
				try {
					buffer.wait();
				} catch (InterruptedException e) { }
			}
			
			data = buffer.poll();
		}
		
		return data;
	}

	@Override
	public T pollFirst() {
		T data;
		
		synchronized (buffer) {
			data = buffer.poll();
		}
		
		return data;
	}
	
}
