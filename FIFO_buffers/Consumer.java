package FIFO_buffers;

public class Consumer extends Thread {
	
	private int id;
	private BoundedBuffer<Integer> buffer;
	
	public Consumer (int id, BoundedBuffer<Integer> buffer) {
		this.id = id;
		this.buffer = buffer;
	}
	
	@Override
	public void run() {
		while (true) {
			int item = buffer.get();
			System.out.println("Consumer " + id + " consumed item: " + item);
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
	}

}
