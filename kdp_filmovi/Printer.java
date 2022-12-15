package kdp_filmovi;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Printer extends Thread {
	
	private AtomicLong filmsRead;
	private AtomicLong filmsProcessed;
	
	private Map<Integer, Map<String, Film>> globalMax;
	private Barrier printerBarrier;
	
	private int N;

	public Printer(int N,
					AtomicLong filmsRead,
					AtomicLong filmsProcessed,
					Map<Integer, Map<String, Film>> globalMax,
					Barrier printerBarrier) {
		
		super("Printer");
		this.N = N;
		
		this.filmsRead = filmsRead;
		this.filmsProcessed = filmsProcessed;
		this.globalMax = globalMax;
		this.printerBarrier = printerBarrier;
	}

	@Override
	public void run() {
		while (printerBarrier.await(N) == false) {
			System.out.println("Read films: " + filmsRead.get());
			System.out.println("Processed films: " + filmsProcessed.get());
		}
		
		System.out.println();
		
		for (Integer i : globalMax.keySet()) {
			int decade = i * 10;
			System.out.println(decade + "-" + (decade + 9) + ": ");
			Map<String, Film> m = globalMax.get(i);
			for (String s : m.keySet()) {
				System.out.println(s + "-" + m.get(s));
			}
			System.out.println();
		}
	}

}
