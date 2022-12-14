package kdp;

public interface Barrier {
	
	void arrived();
	void await();
	boolean await(long timeout);
}
