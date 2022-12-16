package kdp.lab2022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Producer extends Thread {
	
	private FIFOBuffer<String> lines;
	private AtomicBroadcastBuffer<TVShow> shows;
	private Barrier consumerBarrier;
	private String titleBasics;
	private String titleEpisodes; 

	public Producer (FIFOBuffer<String> lines,
					AtomicBroadcastBuffer<TVShow> shows,
					Barrier consumerBarrier,
					String titleBasics,
					String titleEpisodes) {
		
		super("Producer");
		this.lines = lines;
		this.shows = shows;
		this.consumerBarrier = consumerBarrier;
		this.titleBasics = titleBasics;
		this.titleEpisodes = titleEpisodes;
	}

	@Override
	public void run() {
		File titleFile = new File(titleBasics);
		File episodeFile = new File(titleEpisodes);
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(episodeFile));
			in.readLine();
			
			String line = null;
			while ((line = in.readLine()) != null) {
				lines.put(line);
			}
			lines.put(null);
			
		} catch (IOException e) { }
		
		consumerBarrier.await();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(titleFile));
			in.readLine();
			
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] args = line.split("\t");
				
				if (args[1].equals("tvSeries") || args[1].equals("tvMiniSeries")) {
					shows.put(new TVShow(line));
				}
			}
			shows.put(null);
			
		} catch (IOException e) { }
	}

}
