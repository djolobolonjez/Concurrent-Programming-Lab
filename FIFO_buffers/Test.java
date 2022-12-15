package FIFO_buffers;

public class Test {

	private static final int BUFFER_SIZE = 10;
	
	public static void main(String[] args) {
		
		BoundedBuffer<Integer> buffer = new RegionBoundedBufferFIFO<>();
		
		for (int i = 0; i < 5; i++) {
			new Producer(i, buffer).start();
			new Consumer(i, buffer).start();
		}

	}

}
