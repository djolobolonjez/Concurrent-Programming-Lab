package kdp_glumci;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;

public class Consumer extends Thread {

	private int processed;
	
	private final int id;
	private final int period;
	private final BoundedBuffer<String> lines;
	private final BoundedBuffer<Integer> shared;
	private final Queue<Integer> data = new LinkedList<>();
	private final Barrier barrier;
	private final ConcurrentMap<Integer, Integer> progress;

	public Consumer(int id, 
				int period,
				BoundedBuffer<String> lines,
				BoundedBuffer<Integer> shared, 
				Barrier barrier,
				ConcurrentMap<Integer, Integer> progress) {
		
		super("Consumer" + id);
		this.id = id;
		this.lines = lines;
		this.barrier = barrier;
		this.progress = progress;
		this.period = period;
		this.shared = shared;
		this.processed = 0;
	}

	@Override
	public void run() {
		String line = null;
		
		while ((line = lines.get()) != null) {
			parseLine(line);
			
			if (++processed % period == 0) {
				transfer();
			}
		}
		
		transfer();
		lines.put(null);
		barrier.arrived();
	}
	
	private void transfer() {
		while (!data.isEmpty()) {
			shared.put(data.poll());
		}
		
		progress.put(id, processed);
	}

	private void parseLine(String line) {
		String[] args = line.split("\t");
		FileData fd = new FileData(args);
		if (!fd.isValidDate() || !fd.isAlive() || !fd.isActor()) {
			return;
		}
		
		int decade = fd.getDecade();
		data.add(decade);
	}

}
