package kdp_filmovi;

public interface BoundedBuffer<T> {

	void put(T data);
	T get();
	T pollFirst();
}
