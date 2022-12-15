package kdp_filmovi;

public interface Barrier {
	
	void arrived();
	void await();
	boolean await(long timeout);
}
