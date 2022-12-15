package kdp_filmovi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer extends Thread {
	
	private AtomicLong filmsRead;
	private AtomicLong filmsProcessed;
	
	private int id;
	
	private final BoundedBuffer<String> lines;
	private final AtomicBroadcastBuffer<Rating> ratings;
	private Map<String, Film> films = new HashMap<>(); /* id - film pairs */
	private Map<Integer, Map<String, Film>> localMax = new HashMap<>();
	private final BoundedBuffer<Film> bestFilms;
	
	private final Barrier consumerBarrier;
	private final Barrier combinerBarrier;

	public Consumer (int id,
					BoundedBuffer<String> lines,
					BoundedBuffer<Film> bestFilms,
					AtomicBroadcastBuffer<Rating> ratings,
					Barrier consumerBarrier,
					Barrier combinerBarrier,
					AtomicLong filmsRead,
					AtomicLong filmsProcessed) {
		
		super("Consumer" + id);
		this.lines = lines;
		this.ratings = ratings;
		this.consumerBarrier = consumerBarrier;
		this.id = id;
		this.bestFilms = bestFilms;
		this.combinerBarrier = combinerBarrier;
		this.filmsRead = filmsRead;
		this.filmsProcessed = filmsProcessed;
	}

	@Override
	public void run() {
		String line = null;
		
		while ((line = lines.get()) != null) {
			parseLine(line);
			filmsRead.getAndIncrement();
		}
		
		lines.put(null);
		consumerBarrier.arrived();
		
		Rating rating;
		while ((rating = ratings.get(id)) != null) {
			String filmID = rating.getId();
			if (!films.containsKey(filmID)) {
				continue;
			}
			
			filmsProcessed.incrementAndGet();
			Film film = films.get(filmID);
			film.setRating(rating);
			
			update(film);
			films.remove(film.getId());
		}
		
		for (Integer i : localMax.keySet()) {
			Map<String, Film> m = localMax.get(i);
			
			for (String s : m.keySet()) {
				bestFilms.put(m.get(s));
			}
		}
		
		combinerBarrier.arrived();
	}

	private void parseLine(String line) {
		String[] args = line.split("\t");
		
		if (args[1].equals("movie") && !args[5].equals("\\N") && !args[8].equals("\\N")) {
			Film film = new Film(args);
			films.put(film.getId(), film);
		}
	}
	
	private void update(Film film) {
		Map<String, Film> m;
		int decade = film.getDecade();
		
		if (!localMax.containsKey(decade)) {
			m = new HashMap<>();
			localMax.put(decade, m);
		} else {
			m = localMax.get(decade);
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
