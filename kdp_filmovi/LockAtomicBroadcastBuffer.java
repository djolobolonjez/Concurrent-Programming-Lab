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
	
	private int[] ri;
	
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
		for (int i = 0; i < this.size; i++) {
			num[i] = n;
		}
		
		this.ri = new int[this.n];
	}

	@Override
	public T get(int id) {
		lock.lock();
		
		try {
			if (full[id] == 0) {
				slots[id].awaitUninterruptibly();
			}
			full[id]--;
			
		} finally {
			lock.unlock();
		}
		
		lock.lock();
		
		T data = buffer[ri[id]];
		try {
			if (++num[ri[id]] == n) {
				empty.signal();
			}
			
			ri[id] = (ri[id] + 1) % size;
		} finally {
			lock.unlock();
		}
		
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
		
		lock.lock();
		buffer[wi++] = data;
		try {
			for (int i = 0; i < n; i++) {
				full[i]++;
				slots[i].signal();
			}
			
			wi %= size;
		} finally {
			lock.unlock();
		}
		
	}

}
