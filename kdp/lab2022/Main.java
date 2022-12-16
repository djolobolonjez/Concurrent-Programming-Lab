package kdp.lab2022;

import java.util.HashMap;
import java.util.Map;

public class Main {
	
	private static final int MAX_BUFFER_SIZE = 10000;

	public static void main(String[] args) {

		int consumersNumber = 5;
		
		String titleBasics = "title.basics/data.tsv";
		String titleEpisodes = "title.episode/data.tsv";
		
		long start = System.currentTimeMillis();
		
		FIFOBuffer<String> lines = new SemaphoreBoundedBufferFIFO<>(MAX_BUFFER_SIZE);
		AtomicBroadcastBuffer<TVShow> shows = new MonitorAtomicBroadcastBuffer<>(consumersNumber, MAX_BUFFER_SIZE);
		BoundedBuffer<TVShow> shared = new LockBoundedBuffer<>();
		
		Barrier consumerBarrier = new LockBarrier(consumersNumber); /* producer waits for consumers to read data */
		Barrier combinerBarrier = new LockBarrier(consumersNumber); /* combiner waits for consumers to finish */
		Barrier printerBarrier = new LockBarrier(1); /* printer waits for combiner */
		
		Map<String, Double> result = new HashMap<>();

		Producer producer = new Producer(lines, shows, consumerBarrier, titleBasics, titleEpisodes);
		producer.start();

		for (int i = 0; i < consumersNumber; i++) {
			Consumer consumer = new Consumer(i, lines, shows, shared, consumerBarrier, combinerBarrier);
			consumer.start();
		}

		Combiner combiner = new Combiner(combinerBarrier, printerBarrier, shared, result);
		combiner.start();

		Printer printer = new Printer(printerBarrier, result);
		printer.start();

		try {
			printer.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println();
		System.out.println("Total execution time: " + (end - start) + "ms");

	}
}
