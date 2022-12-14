package kdp_glumci;

public class MonitorBarrier implements Barrier {

	private final int count;
	private int current = 0;
	
	public MonitorBarrier (int count) {
		this.count = count;
	}
	
	@Override
	public synchronized void arrived() {
		
		if (++current == count) {
			notify();
		} 
	}

	@Override
	public synchronized boolean await(long timeout) {
	
		if (current != count) {
			try {
				if (timeout == 0) {
					wait();
				} else {
					wait(timeout);
				}
			} catch (InterruptedException e) { }
		}
		
		return current == count;
	}

	@Override
	public synchronized void await() {
		await(0);
	}

	
}
