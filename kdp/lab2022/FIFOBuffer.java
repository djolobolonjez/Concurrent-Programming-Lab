package kdp.lab2022;

public interface FIFOBuffer<T> {
	void put (T data);
	T get ();
}
