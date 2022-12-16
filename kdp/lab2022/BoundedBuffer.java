package kdp.lab2022;

public interface BoundedBuffer<T> {

	void put(T data);
	T get();
	T pollFirst();
}
