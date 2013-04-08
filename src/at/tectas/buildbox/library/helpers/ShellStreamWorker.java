package at.tectas.buildbox.library.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ShellStreamWorker extends Thread {
	
	private BufferedReader stream = null;
	public ArrayList<String> result = new ArrayList<String>();
	
	public ShellStreamWorker(BufferedReader stream) {
		this.stream = stream;
	}

	@Override
	public void run() {		
		try {
			String line = null;
			
			if (this.stream != null && !this.stream.ready()) {
				while ((line = this.stream.readLine()) != null) {
					result.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
