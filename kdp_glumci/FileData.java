package kdp_glumci;

public class FileData {
	
	private int birthYear;
	private int deathYear;
	private String primaryProfession;
	
	public FileData (String[] args) {

		this.birthYear = (isYear(args[2]) ? Integer.parseInt(args[2]) : -1);
		this.deathYear = (isYear(args[3]) ? Integer.parseInt(args[3]) : -1);
		this.primaryProfession = args[4];
	}
	
	private boolean isYear (String s) {
		if (s.equals("\\N")) {
			return false;
		}
		return true;
	}
	
	public boolean isValidDate() {
		return birthYear != -1;
	}
	
	public boolean isAlive() {
		return deathYear == -1;
	}
	
	public boolean isActor() {
		return primaryProfession.contains("actor") || primaryProfession.contains("actress");
	}
	
	public int getDecade() {
		
		return birthYear / 10;
	}
}
