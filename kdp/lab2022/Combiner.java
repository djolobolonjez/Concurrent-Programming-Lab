package kdp.lab2022;

import java.util.Map;

public class Combiner extends Thread {
	
	private Barrier combinerBarrier;
	private Barrier printerBarrier;
	
	private BoundedBuffer<TVShow> shared;
	private Map<String, Double> result;

	public Combiner (Barrier combinerBarrier,
					Barrier printerBarrier,
					BoundedBuffer<TVShow> shared,
					Map<String, Double> result) {
		
		super("Combiner");
		this.combinerBarrier = combinerBarrier;
		this.printerBarrier = printerBarrier;
		this.shared = shared;
		this.result = result;
	}

	@Override
	public void run() {
		combinerBarrier.await();
		TVShow show = null;
		
		while ((show = shared.pollFirst()) != null) {
			if (show.getSeasons() != 0) {
				double average = (double)show.getEpisodes() / show.getSeasons();
				result.put(show.getTitle(), average);
			}
		}
		
		printerBarrier.arrived();
	}

}
