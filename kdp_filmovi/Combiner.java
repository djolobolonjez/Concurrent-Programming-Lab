package kdp_filmovi;

import java.util.HashMap;
import java.util.Map;

public class Combiner extends Thread {

	private final BoundedBuffer<Film> bestFilms;
	
	private Map<Integer, Map<String, Film>> globalMax;
	
	private final Barrier combinerBarrier;
	private final Barrier printerBarrier;
	
	public Combiner(BoundedBuffer<Film> bestFilms,
					Barrier combinerBarrier,
					Barrier printerBarrier, 
					Map<Integer, Map<String, Film>> globalMax) {
		
		super("Combiner");
		this.bestFilms = bestFilms;
		this.combinerBarrier = combinerBarrier;
		this.printerBarrier = printerBarrier;
		this.globalMax = globalMax;
	}

	@Override
	public void run() {
		combinerBarrier.await();
		
		Film film = null;
		while ((film = bestFilms.pollFirst()) != null) {
			updateGlobal(film);
		}
		
		printerBarrier.arrived();
	}
	
	private void updateGlobal(Film film) {
		Map<String, Film> m;
		int decade = film.getDecade();
		
		if (!globalMax.containsKey(decade)) {
			m = new HashMap<>();
			globalMax.put(decade, m);
		} else {
			m = globalMax.get(decade);
		}
		
		String[] genres = film.getGenres();
		for (String s : genres) {
			if (!m.containsKey(s)) {
				m.put(s, film);
				continue;
			}
			
			Film curr = m.get(s);
			Rating currAvg = curr.getRating(), filmAvg = film.getRating();
			
			if (currAvg.getAverageRating() < filmAvg.getAverageRating()
				|| currAvg.getAverageRating() == filmAvg.getAverageRating()
				&& currAvg.getVotes() < filmAvg.getVotes()) {
				
				m.put(s, film);
			}
		}
		
	}

}
