package at.tectas.buildbox.communication;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

public class JSONObjectAsyncCommunicator extends AsyncTask<String, Integer, JsonObject> {
	
	private ICommunicatorCallback callbackListener = null;
	private Communicator communicator = null;
	
	public JSONObjectAsyncCommunicator (Communicator communicator) {
		this.communicator = communicator;
	}
	
	public JSONObjectAsyncCommunicator (Communicator communicator, ICommunicatorCallback callback) {
		this(communicator);
		this.callbackListener = callback;
	}
	
	@Override
	protected JsonObject doInBackground(String... params) {
		try {
			return this.communicator.getJsonObject(params[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(JsonObject result) {
		if (this.callbackListener != null)
			this.callbackListener.updateWithJsonObject(result);
		super.onPostExecute(result);
	}
}
