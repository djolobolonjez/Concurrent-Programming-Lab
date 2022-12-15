package kdp_filmovi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Producer extends Thread {
	
	private final BoundedBuffer<String> lines;
	private final AtomicBroadcastBuffer<Rating> ratings;
	private final Barrier consumerBarrier;
	
	private final String titleFileName = "title_basics/data.tsv";
	private final String ratingFileName = "title_ratings/data.tsv";

	public Producer (BoundedBuffer<String> lines,
					AtomicBroadcastBuffer<Rating> ratings,
					Barrier barrier) {
		
		super("Producer");
		this.lines = lines;
		this.consumerBarrier = barrier;
		this.ratings = ratings;
	}

	@Override
	public void run() {
		
		File titleFile = new File(titleFileName);
		File ratingFile = new File(ratingFileName);
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(titleFile));
			String line = in.readLine();
			while ((line = in.readLine()) != null) {
				lines.put(line);
			}
			lines.put(null); /* queue is empty */
			
		} catch (IOException e) { }
		
		consumerBarrier.await();
		
		try {

			BufferedReader in = new BufferedReader(new FileReader(ratingFile));
			String line = in.readLine();
			while ((line = in.readLine()) != null) {
				ratings.put(new Rating(line));
			}
			ratings.put(null); /* queue is empty */
			
		} catch (IOException e) { }
	}

}
