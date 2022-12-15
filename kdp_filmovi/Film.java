package kdp_filmovi;

public class Film {

	private String title;
	private String tconst;
	private String[] genres;
	private Rating rating;
	private int decade;
	
	public Film (String[] args) {
		this.tconst = args[0];
		this.title = args[2];
		this.genres = args[8].split(",");
		this.decade = Integer.parseInt(args[5]) / 10;
	}
	
	public Rating getRating() {
		return this.rating;
	}
	
	public void setRating(Rating rating) {
		this.rating = rating;
	}
	
	public String getId() {
		return this.tconst;
	}
	
	public int getDecade() {
		return this.decade;
	}
	
	public String[] getGenres() {
		return this.genres;
	}
}
