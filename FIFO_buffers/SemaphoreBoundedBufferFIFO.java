package FIFO_buffers;

import java.util.concurrent.Semaphore;

public class SemaphoreBoundedBufferFIFO<T> implements BoundedBuffer<T> {
	
	private int head = 0, tail = 0;
	private Semaphore mutexHead = new Semaphore(1), mutexTail = new Semaphore(1);
	private int size;
	private T[] buffer;
	private Semaphore[] empty;
	private Semaphore[] full;
	
	@SuppressWarnings("unchecked")
	public SemaphoreBoundedBufferFIFO (int size) {
		this.size = size;
		
		this.buffer = (T[]) new Object[this.size];
		this.empty = new Semaphore[this.size];
		this.full = new Semaphore[this.size];
		
		for (int i = 0; i < this.size; i++) {
			empty[i] = new Semaphore(1);
			full[i] = new Semaphore(0);
		}
	}

	@Override
	public void put(T data) {
		mutexTail.acquireUninterruptibly();
		
		int index = tail++;
		tail %= size;
		
		mutexTail.release();
		
		empty[index].acquireUninterruptibly();
		
		buffer[index] = data;
		
		full[index].release();
		
	}

	@Override
	public T get() {
		mutexHead.acquireUninterruptibly();
		
		int index = head++;
		head %= size;
		
		mutexHead.release();
		
		full[index].acquireUninterruptibly();
		
		T data = buffer[index];
		
		empty[index].release();
		return data;
	}

	
}
