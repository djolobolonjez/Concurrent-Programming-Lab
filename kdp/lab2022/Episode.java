package kdp.lab2022;

public class Episode {
	
	private String id;
	private String parentId;
	private int seasonNum;
	private int episodeNum;

	public Episode(String[] args) {
		this.id = args[0];
		this.parentId = args[1];
		this.seasonNum = (args[2].equals("\\N") ? 0 : Integer.parseInt(args[2]));
		this.episodeNum = (args[3].equals("\\N") ? 0 : Integer.parseInt(args[3]));
	}

	public String getId() {
		return this.id;
	}

	public String getParentId() {
		return this.parentId;
	}

	public int getSeasonNum() {
		return this.seasonNum;
	}

	public int getEpisodeNum() {
		return this.episodeNum;
	}

}
