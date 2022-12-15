package kdp_filmovi;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAtomicBroadcastBuffer<T> implements AtomicBroadcastBuffer<T> {
	
	private Lock lock = new ReentrantLock();
	private Condition empty = lock.newCondition();
	private Condition[] slots;
	
	private int[] num;
	private int[] full;
	
	private int wi = 0;
	private int size;
	private int n;
	
	private T[] buffer;
	
	private ThreadLocal<Integer> ri = ThreadLocal.withInitial(() -> 0);
	
	public LockAtomicBroadcastBuffer(int n, int size) {
		this.n = n;
		this.size = size;
		
		this.buffer = (T[]) new Object[this.size];
		this.slots = new Condition[this.n];
		for (int i = 0; i < this.n; i++) {
			slots[i] = lock.newCondition();
		}
		
		this.full = new int[this.n];
		this.num = new int[this.size];
	}

	@Override
	public T get(int id) {
		int readIndex = ri.get();
		lock.lock();
		
		try {
			if (full[id] == 0) {
				slots[id].awaitUninterruptibly();
			}
			full[id]--;
			
		} finally {
			lock.unlock();
		}
		
		T data = buffer[readIndex];
		
		lock.lock();
		try {
			if (++num[readIndex] == n) {
				empty.signal();
			}
		} finally {
			lock.unlock();
		}
		ri.set((readIndex + 1) % size);
		
		return data;
	}

	@Override
	public void put(T data) {
		lock.lock();
		try {
			if (num[wi] < n) {
				empty.awaitUninterruptibly();
			}
			num[wi] = 0;
			
		} finally {
			lock.unlock();
		}
		
		buffer[wi++] = data;
		
		lock.lock();
		try {
			for (int i = 0; i < n; i++) {
				full[i]++;
				slots[i].signal();
			}
		} finally {
			lock.unlock();
		}
		
		wi %= size;
	}

}
