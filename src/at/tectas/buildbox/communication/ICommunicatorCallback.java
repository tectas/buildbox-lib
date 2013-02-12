package at.tectas.buildbox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ICommunicatorCallback {
	public void updateWithJSONObject (JSONObject result);
	public void updateWithJSONArray (JSONArray result);
}
