package at.tectas.buildbox.communication;

import android.os.AsyncTask;

import com.google.gson.JsonArray;

public class JSONArrayAsyncCommunicator extends AsyncTask<String, Integer, JsonArray> {
	
	private ICommunicatorCallback callbackListener = null;
	private Communicator communicator = null;
	
	public JSONArrayAsyncCommunicator (Communicator communicator) {
		this.communicator = communicator;
	}
	
	public JSONArrayAsyncCommunicator (Communicator communicator, ICommunicatorCallback callback) {
		this(communicator);
		this.callbackListener = callback;
	}
	
	@Override
	protected JsonArray doInBackground(String... params) {
		try {
			return this.communicator.getJsonArray(params[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(JsonArray result) {
		if (this.callbackListener != null)
			this.callbackListener.updateWithJsonArray(result);
		super.onPostExecute(result);
	}
}

