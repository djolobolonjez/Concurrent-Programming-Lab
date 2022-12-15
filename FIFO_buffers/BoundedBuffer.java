package FIFO_buffers;

public interface BoundedBuffer<T> {

	void put(T data);
	T get();
}
