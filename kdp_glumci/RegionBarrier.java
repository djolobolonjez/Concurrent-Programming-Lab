package kdp_glumci;

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
				lock.notify();
			}
		}
		
	}

	@Override
	public void await() {
		await(0);
	}

	@Override
	public boolean await(long timeout) {
		
		synchronized (lock) {
			try {
				if (current != count) {
					if (timeout == 0) {
						lock.wait();
					} else {
						lock.wait(timeout);
					}
				}
				
			} catch (InterruptedException e) { }
		}
		
		return current == count;
	}

}
