package FIFO_buffers;

import java.util.Random;

public class Producer extends Thread {
	
	private BoundedBuffer<Integer> buffer;
	private int id;
	
	public Producer (int id, BoundedBuffer<Integer> buffer) {
		this.buffer = buffer;
		this.id = id;
	}

	@Override
	public void run() {
		while (true) {
			int item = new Random().nextInt(100);
			System.out.println("Producer " + id + " produced item: " + item);
			buffer.put(item);
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
	}
}
