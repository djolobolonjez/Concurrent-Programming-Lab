package kdp_filmovi;

import java.util.concurrent.Semaphore;

public class SemaphoreAtomicBroadcastBuffer<T> implements AtomicBroadcastBuffer<T> {
	
	private int n;
	private int size;
	
	private int wi; /* next index for writing inside a buffer */
	
	private int[] ri; /* index for reading for every consumer */
	private int[] num; /* how many consumer have read i-th element*/
	
	private T[] buffer;
	private Semaphore[] mutex; /* mutual exclusion for every element */
	private Semaphore[] full; /* consumer is allowed to read his element */
	
	private final Semaphore empty; /* semaphore for synchronization with producer */
	
	public SemaphoreAtomicBroadcastBuffer (int n, int size) {
		this.n = n; // number of consumers
		this.size = size; // buffer size
		this.wi = 0; // index for putting next element
		
		this.empty = new Semaphore(size);
		this.buffer = (T[]) new Object[this.size];
		
		this.mutex = new Semaphore[this.size];
		for (int i = 0; i < this.size; i++) {
			mutex[i] = new Semaphore(1);
		}
		
		this.ri = new int[this.n];
		this.num = new int[this.size];
		
		this.full = new Semaphore[this.n];
		for (int i = 0; i < this.n; i++) {
			full[i] = new Semaphore(0);
		}
		
	}

	@Override
	public T get(int id) {
		
		full[id].acquireUninterruptibly();
		mutex[ri[id]].acquireUninterruptibly();
		
		T data = buffer[ri[id]];
		num[ri[id]]++;
		
		if (num[ri[id]] == n) {
			num[ri[id]] = 0;
			empty.release();
		}
		
		mutex[ri[id]++].release();
		
		ri[id] %= size;
		return data;
	}

	@Override
	public void put(T data) {
		
		empty.acquireUninterruptibly();
		
		buffer[wi++] = data;
		wi %= size;
		
		for (int i = 0; i < n; i++) {
			full[i].release();
		}
	}

}
