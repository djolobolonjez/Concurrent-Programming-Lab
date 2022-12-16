package kdp.lab2022;

public class MonitorAtomicBroadcastBuffer<T> implements AtomicBroadcastBuffer<T> {

	private int n; // number of consumers
	private int size; // buffer size
	private T[] buffer; 
	private int[] full; // available item on given index
	private int[] num; // everyone has read from given index
	
	private int wi = 0;
	private int[] ri;
	
	public MonitorAtomicBroadcastBuffer(int n, int size) {
		this.n = n;
		this.size = size;
		
		this.buffer = (T[]) new Object[this.size];
		this.full = new int[this.n];
		
		this.ri = new int[this.n];
		
		this.num = new int[this.size];
		for (int i = 0; i < this.size; i++) {
			num[i] = n;
		}
	}
	
	@Override
	public synchronized T get(int id) {
		T data;
		while (full[id] == 0) {
			try {
				wait();
			} catch (InterruptedException e) { }
		}
		
		full[id]--;
		data = buffer[ri[id]];
		num[ri[id]]++;
		
		if (num[ri[id]++] == n) {
			notifyAll();
		}
		
		ri[id] %= size;
		return data;
	}
	
	@Override
	public synchronized void put(T data) {
		while (num[wi] < n) {
			try {
				wait();
			} catch (InterruptedException e) { }
			
		}
		num[wi] = 0;
		
		buffer[wi++] = data;
		wi %= size;
		
		for (int i = 0; i < this.n; i++) {
			full[i]++;
		}
		
		notifyAll();
	}

}
