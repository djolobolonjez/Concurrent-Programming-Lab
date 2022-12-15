package kdp_filmovi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Test {
	
	private static final int BUFFER_LENGTH = 10000;

	public static void main(String[] args) {

		int consumersNumber = 5;
		int N = 1000;
		
		long start = System.currentTimeMillis();
		
		AtomicBroadcastBuffer<Rating> ratings = new RegionAtomicBroadcastBuffer<>(consumersNumber, BUFFER_LENGTH);
		BoundedBuffer<String> lines = new LockBoundedBuffer<>();
		
		Barrier consumerBarrier = new RegionBarrier(consumersNumber);
		Barrier combinerBarrier = new RegionBarrier(consumersNumber);
		Barrier printerBarrier = new RegionBarrier(1);
		
		BoundedBuffer<Film> bestFilms = new LockBoundedBuffer<>();
		Map<Integer, Map<String, Film>> globalMax = new HashMap<>();
		
		AtomicLong filmsRead = new AtomicLong(), filmsProcessed = new AtomicLong();

		Producer producer = new Producer(lines, ratings, consumerBarrier);
		producer.start();

		for (int i = 0; i < consumersNumber; i++) {
			Consumer consumer = new Consumer(i, lines, bestFilms, ratings, consumerBarrier, combinerBarrier, filmsRead, filmsProcessed);
			consumer.start();
		}

		Combiner combiner = new Combiner(bestFilms, combinerBarrier, printerBarrier, globalMax);
		combiner.start();

		Printer printer = new Printer(N, filmsRead, filmsProcessed, globalMax, printerBarrier);
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
