package at.tectas.buildbox.helpers;

import java.io.BufferedReader;
import java.io.IOException;

public class ShellStreamWorker extends Thread {
	
	private BufferedReader stream = null;
	public StringBuffer result = new StringBuffer();
	
	public ShellStreamWorker(BufferedReader stream) {
		this.stream = stream;
	}

	@Override
	public void run() {		
		try {
			String line = null;
		
			while ((line = this.stream.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
