package kdp.lab2022;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Consumer extends Thread {
	
	private FIFOBuffer<String> lines;
	private AtomicBroadcastBuffer<TVShow> shows;
	private BoundedBuffer<TVShow> shared;
	private Barrier consumerBarrier;
	private Barrier combinerBarrier;
	
	private Map<String, Queue<Episode>> episodes = new HashMap<>();
	
	private int id;

	public Consumer (int id,
					FIFOBuffer<String> lines,
					AtomicBroadcastBuffer<TVShow> shows,
					BoundedBuffer<TVShow> shared,
					Barrier consumerBarrier,
					Barrier combinerBarrier) {
	
		super("Consumer" + id);
		this.id = id;
		this.lines = lines;
		this.shows = shows;
		this.shared = shared;
		this.consumerBarrier = consumerBarrier;
		this.combinerBarrier = combinerBarrier;
	}

	@Override
	public void run() {
		String line = null;
		while ((line = lines.get()) != null) {
			parseLine(line);
		}
		lines.put(null);
		
		consumerBarrier.arrived();
		
		TVShow show = null;
		while ((show = shows.get(id)) != null) {
			update(show);
		}
		shows.put(null);
		combinerBarrier.arrived();
	}

	private void update(TVShow show) {
		String id = show.getId();
		if (!episodes.containsKey(id)) {
			return;
		}
		
		Queue<Episode> eps = episodes.get(id);
		for (Episode ep : eps) {
			show.updateShow(ep);
		}
		
		shared.put(show);
	}

	private void parseLine(String line) {
		String[] args = line.split("\t");
		Episode ep = new Episode(args);
		
		Queue<Episode> eps;
		String parentId = ep.getParentId();
		if (!episodes.containsKey(parentId)) {
			eps = new LinkedList<>();
		} else {
			eps = episodes.get(parentId);
		}
		
		eps.add(ep);
		episodes.put(parentId, eps);
	}

}
