package kdp;

public interface BoundedBuffer<T> {

	void put(T data);
	T get();
	T pollFirst();
}
