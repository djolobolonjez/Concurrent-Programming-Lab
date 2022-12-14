package kdp_glumci;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Test {
	
	private static final long WAIT_TIME = 1000;

	public static void main(String[] args) {

		int consumersNumber = 5;
		int N = 1000;
		String fileName = "test_fajlovi/data.tsv";
		
		long start = System.currentTimeMillis();
		
		// Shared buffer for reading from TSV file
		BoundedBuffer<String> lines = new LockBoundedBuffer<>();
		
		// Shared buffer for communication between Consumers and Combiner
		BoundedBuffer<Integer> shared = new LockBoundedBuffer<>();
		
		Barrier consumerBarrier = new MonitorBarrier(consumersNumber);
		Barrier printerBarrier = new MonitorBarrier(1);
	
		ConcurrentMap<Integer, Integer> progress = new ConcurrentHashMap<>();
		Map<Integer, Integer> results = new HashMap<>();
		
		Producer producer = new Producer(fileName, lines);
		producer.start();

		for (int i = 0; i < consumersNumber; i++) {
			Consumer consumer = new Consumer(i, N, lines, shared, consumerBarrier, progress);
			consumer.start();
		}

		Combiner combiner = new Combiner(shared, consumerBarrier, printerBarrier, results);
		combiner.start();

		Printer printer = new Printer(printerBarrier, results, progress, WAIT_TIME);
		printer.start();

		try {
			printer.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Total execution time: " + (end - start) + "ms");

	}
}
