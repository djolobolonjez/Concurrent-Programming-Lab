package kdp.lab2022;

import java.util.HashMap;
import java.util.Map;

public class TVShow {
	
	private String id;
	private String title;
	private Map<Integer, Integer> episodesCount = new HashMap<>();

	public TVShow (String line) {
		String[] args = line.split("\t");
		this.id = args[0];
		this.title = args[2];
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void updateShow(Episode ep) {
		int seasonNum = ep.getSeasonNum();
		int episodeNum = ep.getEpisodeNum();
		
		if (seasonNum == 0) {
			return;
		}
		
		if (!episodesCount.containsKey(seasonNum)) {
			episodesCount.put(seasonNum, episodeNum);
			return;
		}
		
		Integer episode = episodesCount.get(seasonNum);
		int epNum = (episode == null ? 0 : episode.intValue());
		
		if (episodeNum > epNum) {
			epNum = episodeNum;
		}
		episodesCount.put(seasonNum, epNum);
	}
	
	public int getSeasons() {
		int max = 0;
		for (Integer i : episodesCount.keySet()) {
			if (i > max) {
				max = i;
			}
		}
		
		return max;
	}
	
	public int getEpisodes() {
		int sum = 0;
		for (Integer i : episodesCount.keySet()) {
			Integer cnt = episodesCount.get(i);
			sum += (cnt == null ? 0 : cnt.intValue());
		}
		
		return sum;
	}
}
