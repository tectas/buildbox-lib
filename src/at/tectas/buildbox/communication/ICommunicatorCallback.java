package at.tectas.buildbox.communication;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ICommunicatorCallback {
	public void updateWithImage(ImageView view, Bitmap bitmap);
	public void updateWithJsonArray(JsonArray result);
	public void updateWithJsonObject(JsonObject result);
}
