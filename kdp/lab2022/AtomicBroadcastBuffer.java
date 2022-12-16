package kdp.lab2022;

public interface AtomicBroadcastBuffer<T> {

	T get (int id);
	void put (T data);
}
