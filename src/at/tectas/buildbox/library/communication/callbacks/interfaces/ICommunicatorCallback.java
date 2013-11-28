package at.tectas.buildbox.library.communication.callbacks.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.graphics.Bitmap;
import android.widget.ImageView;
import at.tectas.buildbox.library.communication.asynccommunicators.JSONElementAsyncCommunicatorResult;

public interface ICommunicatorCallback {
	public void updateImage(ImageView view, Bitmap bitmap);

	public void updateJsonArray(JsonArray result);

	public void updateJsonObject(JsonObject result);

	public void updateJsonElement(JSONElementAsyncCommunicatorResult result);
}
