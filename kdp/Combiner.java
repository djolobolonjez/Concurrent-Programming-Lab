package kdp;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class Combiner extends Thread {

	private final BoundedBuffer<Integer> shared;
	private final Barrier consumerBarrier;
	private final Barrier printerBarrier;
	private final Map<Integer, Integer> results;
	
	public Combiner(BoundedBuffer<Integer> shared, 
				Barrier consumerBarrier,
				Barrier printerBarrier,
				Map<Integer, Integer> results) {
		
		super("Combiner");
		this.shared = shared;
		this.consumerBarrier = consumerBarrier;
		this.printerBarrier = printerBarrier;
		this.results = results;
	}

	@Override
	public void run() {
		
		consumerBarrier.await();
		Integer decade;
		while ((decade = shared.pollFirst()) != null) {
			int count;
			
			if (results.containsKey(decade)) {
				count = results.get(decade) + 1;
			} else {
				count = 1;
			}
			
			results.put(decade, count);
		}
		
		printerBarrier.arrived();
	}

}
