package at.tectas.buildbox.communication;

import java.util.Hashtable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import at.tectas.buildbox.communication.CallbackType;
import at.tectas.buildbox.content.DownloadType;
import at.tectas.buildbox.helpers.IJsonSerialize;
import at.tectas.buildbox.helpers.JsonHelper;

public class DownloadPackage implements IJsonSerialize, Parcelable {
	
	public static JsonHelper helper = new JsonHelper();
	public String url = null;
	public DownloadType type = null;
	public String title = null;
	protected String directory = null;
	protected String filename = null;
	public String md5sum = null;
	public Hashtable<CallbackType, IDownloadProgressCallback> updateCallbacks = new Hashtable<CallbackType, IDownloadProgressCallback>();
	public Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallbacks = new Hashtable<CallbackType, IDownloadFinishedCallback>();
	public Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallbacks = new Hashtable<CallbackType, IDownloadCancelledCallback>();
	public IInstallDownloadHandler installHandler = null;
	protected DownloadResponse response = null;
	
	public void setDirectory(String directory) {
		if (directory.endsWith("/") == false) {
			directory += "/";
		}
		
		this.directory = directory;
	}
	
	public String getDirectory() {
		return this.directory;
	}
	
	public String getKey() {
		return this.md5sum == null ? this.url : this.md5sum;
	}
	
	@SuppressLint("DefaultLocale")
	public void setFilename(String filename) {
		this.filename = filename;
		
		if (response != null) {
			response.mime = response.getMimeType(this.filename, this.filename.length() - 3);
			
			if ((response.mime != null && response.mime.toLowerCase().equals("apk") && type == null) || (type != null && type.equals(DownloadType.apk))) {
				this.installHandler = new ApkInstallDownloadHandler(this);
				this.type = DownloadType.apk;
			}
			else if ((response.mime != null && response.mime.toLowerCase().equals("zip") && type == null) || (type != null && type.equals(DownloadType.zip))) {
				this.installHandler = new ZipInstallDownloadHandler(this);
				this.type = DownloadType.zip;
			}
			else {
				this.installHandler = new DummyInstallDownloadHandler();
			}
		}
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public void setResponse(DownloadResponse response) {
		if (response != null) {
			response.pack = this;
		}
		
		this.response = response;
	}
	
	public DownloadResponse getResponse() {
		return this.response;
	}
	
	public DownloadPackage() {
		
	}
	
	public DownloadPackage(JsonObject json) {
		JsonElement element = json.get("response");
		
		if (element != null && element.isJsonObject())
			this.response = new DownloadResponse(this, element.getAsJsonObject());
		
		this.url = DownloadPackage.helper.tryGetStringFromJson("url", json);
		this.title = DownloadPackage.helper.tryGetStringFromJson("title", json);
		this.directory = DownloadPackage.helper.tryGetStringFromJson("directory", json);
		this.md5sum = DownloadPackage.helper.tryGetStringFromJson("md5sum", json);
		this.setFilename(DownloadPackage.helper.tryGetStringFromJson("filename", json));
	}
	
	public DownloadPackage(Parcel source) {
		this.setResponse((DownloadResponse)source.readParcelable(DownloadResponse.class.getClassLoader()));
		this.url = source.readString();
		
		try {
			this.type = DownloadType.valueOf(source.readString());
		}
		catch (IllegalArgumentException e) {
			this.type = DownloadType.other;
		}
		
		this.title = source.readString();
		this.setDirectory(source.readString());
		this.setFilename(source.readString());
		this.md5sum = source.readString();
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
		
		if (this.response != null) {
			json.add("response", this.response.serializeToJson());
		}
			
		return json;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.response, flags);
		dest.writeString(this.url);
		dest.writeString(this.type.name());
		dest.writeString(this.title);
		dest.writeString(this.directory);
		dest.writeString(this.filename);
		dest.writeString(this.md5sum);
	}
	
	public static final Parcelable.Creator<DownloadPackage> CREATOR = new Parcelable.Creator<DownloadPackage>() {
		
		@Override
		public DownloadPackage[] newArray(int size) {
			return new DownloadPackage[size];
		}
		
		@Override
		public DownloadPackage createFromParcel(Parcel source) {
			return new DownloadPackage(source);
		}
	};
}
