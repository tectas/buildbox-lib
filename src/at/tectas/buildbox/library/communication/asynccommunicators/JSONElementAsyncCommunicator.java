package at.tectas.buildbox.library.communication.asynccommunicators;

import at.tectas.buildbox.library.content.items.ParentItem;
import android.os.AsyncTask;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;

public class JSONElementAsyncCommunicator extends
		AsyncTask<String, Integer, JSONElementAsyncCommunicatorResult> {

	private ICommunicatorCallback callbackListener = null;
	private Communicator communicator = null;
	private ParentItem parent = null;

	public JSONElementAsyncCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	public JSONElementAsyncCommunicator(Communicator communicator,
			ICommunicatorCallback callback) {
		this(communicator);
		this.callbackListener = callback;
	}

	public JSONElementAsyncCommunicator(Communicator communicator,
			ICommunicatorCallback callback, ParentItem parent) {
		this(communicator, callback);
		this.parent = parent;
	}

	public JSONElementAsyncCommunicator(Communicator communicator, ParentItem parent) {
		this(communicator, null, parent);
	}

	@Override
	protected JSONElementAsyncCommunicatorResult doInBackground(
			String... params) {
		try {
			JSONElementAsyncCommunicatorResult result = new JSONElementAsyncCommunicatorResult();
			result.parent = parent;
			result.element = this.communicator.getJsonElement(params[0]);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(JSONElementAsyncCommunicatorResult result) {
		if (this.callbackListener != null)
			this.callbackListener.updateJsonElement(result);
		super.onPostExecute(result);
	}
}
