package kdp.lab2022;

import java.util.Map;

public class Printer extends Thread {
	
	private Barrier printerBarrier;
	private Map<String, Double> result;

	public Printer(Barrier printerBarrier, Map<String, Double> result) {
		super("Printer");
		this.printerBarrier = printerBarrier;
		this.result = result;
	}

	@Override
	public void run() {
		printerBarrier.await();
		
		for (String s : result.keySet()) {
			double avg = result.get(s);
			System.out.println(s + ": " + avg);
		}
	}

}
