package kdp_filmovi;

public class Rating {

	private final String tconst;
	private final double averageRating;
	private final int numVotes;
	
	public Rating (String line) {
		String[] args = line.split("\t");
		tconst = args[0];
		averageRating = Double.parseDouble(args[1]);
		numVotes = Integer.parseInt(args[2]);
	}
	
	public double getAverageRating() {
		return this.averageRating;
	}
	
	public String getId() {
		return this.tconst;
	}
	
	public int getVotes() {
		return this.numVotes;
	}
}
