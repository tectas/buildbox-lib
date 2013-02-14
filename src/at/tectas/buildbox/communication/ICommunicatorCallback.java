package at.tectas.buildbox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ICommunicatorCallback {
	public void updateWithJSONObject (JSONObject result);
	public void updateWithJSONArray (JSONArray result);
	public void updateWithImage(ImageView view, Bitmap bitmap);
}
