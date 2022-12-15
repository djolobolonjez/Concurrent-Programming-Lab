package kdp_filmovi;

public class RegionBarrier implements Barrier {
	
	private Object lock = new Object();
	private int current = 0;
	private int count;
	
	public RegionBarrier (int count) {
		this.count = count;
	}

	@Override
	public void arrived() {
		synchronized (lock) {
			
			if (++current == count) {
				lock.notifyAll();
			}
		}
		
	}

	@Override
	public void await() {
		
		synchronized (lock) {
			while (current != count) {
				try {
					lock.wait();
				} catch (InterruptedException e) { }
			}
		}
	}

	@Override
	public boolean await(long timeout) {
		
		synchronized (lock) {
			try {
				if (current != count) {
					lock.wait(timeout);
				}
				
			} catch (InterruptedException e) { }
		}
		
		return current == count;
	}

}
