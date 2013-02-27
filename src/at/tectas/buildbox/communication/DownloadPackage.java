package at.tectas.buildbox.communication;

import java.util.Hashtable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.tectas.buildbox.communication.Communicator.CallbackType;
import at.tectas.buildbox.helpers.IJsonSerialize;
import at.tectas.buildbox.helpers.JsonHelper;

public class DownloadPackage implements IJsonSerialize {
	public static JsonHelper helper = new JsonHelper();
	public String url = null;
	public String title = null;
	public String directory = null;
	public String filename = null;
	public String md5sum = null;
	public Hashtable<CallbackType, IDownloadProgressCallback> updateCallbacks = new Hashtable<CallbackType, IDownloadProgressCallback>();
	public Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallbacks = new Hashtable<CallbackType, IDownloadFinishedCallback>();
	public Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallbacks = new Hashtable<CallbackType, IDownloadCancelledCallback>();
	public DownloadResponse response = null;
	
	public DownloadPackage() {
		
	}
	
	public DownloadPackage(JsonObject json) {
		this.url = DownloadPackage.helper.tryGetStringFromJson("url", json);
		this.title = DownloadPackage.helper.tryGetStringFromJson("title", json);
		this.directory = DownloadPackage.helper.tryGetStringFromJson("directory", json);
		this.filename = DownloadPackage.helper.tryGetStringFromJson("filename", json);
		this.md5sum = DownloadPackage.helper.tryGetStringFromJson("md5sum", json);
		
		JsonElement element = json.get("response");
		
		if (element != null && element.isJsonObject())
			this.response = new DownloadResponse(element.getAsJsonObject());
	}
	
	public void addProgressListener (CallbackType type, IDownloadProgressCallback callback) {
		if (this.updateCallbacks.containsKey(type)) {
			this.updateCallbacks.remove(type);
		}

		this.updateCallbacks.put(type, callback);
	}
	
	public void addFinishedListener (CallbackType type, IDownloadFinishedCallback callback) {
		if (this.finishedCallbacks.containsKey(type)) {
			this.finishedCallbacks.remove(type);
		}
	
		this.finishedCallbacks.put(type, callback);
	}
	
	public void addCancelledListener (CallbackType type, IDownloadCancelledCallback callback) {
		if (this.cancelCallbacks.containsKey(type)) {
			this.cancelCallbacks.remove(type);
		}
	
		this.cancelCallbacks.put(type, callback);
	}
	
	public void removeProgressListener(CallbackType type) {
		if (this.updateCallbacks.containsKey(type)) {
			this.updateCallbacks.remove(type);
		}
	}
	
	public void removeFinishedListener(CallbackType type) {
		if (this.finishedCallbacks.containsKey(type)) {
			this.finishedCallbacks.remove(type);
		}
	}
	
	public void removeCancelledListener(CallbackType type) {
		if (this.cancelCallbacks.containsKey(type)) {
			this.cancelCallbacks.remove(type);
		}
	}
	
	@Override
	public JsonObject serializeToJson() {		
		JsonObject json = new JsonObject();
		
		json.addProperty("url", this.url);
		json.addProperty("title", this.title);
		json.addProperty("directory", this.directory);
		json.addProperty("filename", this.filename);
		json.addProperty("md5sum", this.md5sum);
		json.add("response", this.response.serializeToJson());
		
		return json;
	}
}
