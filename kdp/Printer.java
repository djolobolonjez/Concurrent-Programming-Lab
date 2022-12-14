package kdp;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class Printer extends Thread {

	private final Barrier printerBarrier;
	private final Map<Integer, Integer> results;
	private final ConcurrentMap<Integer, Integer> progress;
	private final long timeout;
	
	public Printer(Barrier printerBarrier, 
				Map<Integer, Integer> results, 
				ConcurrentMap<Integer, Integer> progress,
				long timeout) {
		
		super("Printer");
		this.printerBarrier = printerBarrier;
		this.results = results;
		this.progress = progress;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		while (true) {
			if (printerBarrier.await(timeout)) {
				break;
			}
			for (Integer i : progress.keySet()) {
				int value = progress.get(i);
				System.out.println(i + ": " + value);
			}
		}
		for (Integer i : results.keySet()) {
			int decade = i * 10;
			int count = results.get(i);
			System.out.println(decade + "-" + (decade + 9) + ": "+  count);
		}
	}

}
