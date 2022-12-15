package kdp_filmovi;

public class RegionAtomicBroadcastBuffer<T> implements AtomicBroadcastBuffer<T> {

	private T[] buffer;
	private Object lock = new Object();
	
	private int wi = 0;
	private ThreadLocal<Integer> ri = ThreadLocal.withInitial(() -> 0);
	
	private int[] num;
	private int[] full;
	
	private int n;
	private int size;
	
	@SuppressWarnings("unchecked")
	public RegionAtomicBroadcastBuffer (int n, int size) {
		this.n = n;
		this.size = size;
		
		this.num = new int[this.size];
		for (int i = 0; i < this.size; i++) {
			num[i] = n;
		}
		
		this.full = new int[this.n];
		this.buffer = (T[]) new Object[this.size];
	}
	
	@Override
	public T get(int id) {
		T data;
		synchronized (lock) {
			while (full[id] == 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) { }
			}
			full[id]--;
			int readIndex = ri.get();
			data = buffer[readIndex];
			
			if (++num[readIndex] == n) {
				lock.notifyAll();
			}
			
			ri.set((readIndex + 1) % size);
		}
		
		return data;
	}

	@Override
	public void put(T data) {
		synchronized (lock) {
			while (num[wi] < n) {
				try {
					lock.wait();
				} catch (InterruptedException e) { }
			}
			num[wi] = 0;
			
			buffer[wi++] = data;
			wi %= size;
			
			for (int i = 0; i < n; i++) {
				full[i]++;
			}
			
			lock.notifyAll();
		}
		
	}
	
}
