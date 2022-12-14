package kdp_glumci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Producer extends Thread {
	
	private final String fileName;
	private final BoundedBuffer<String> lines;
	

	public Producer(String fileName, BoundedBuffer<String> lines) {
		super("Producer");
		this.fileName = fileName;
		this.lines = lines;
	}

	@Override
	public void run() {
		
		File file = new File(fileName);
		try (BufferedReader TSVReader = new BufferedReader(new FileReader(file))) {
			String line = TSVReader.readLine();
			while ((line = TSVReader.readLine()) != null) {
				lines.put(line);
			}
			lines.put(null); // end-of-file
		} catch (IOException e) { }
	}

}
