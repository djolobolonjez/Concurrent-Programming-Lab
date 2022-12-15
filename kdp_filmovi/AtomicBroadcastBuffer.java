package kdp_filmovi;

public interface AtomicBroadcastBuffer<T> {

	T get (int id);
	void put (T data);
}
