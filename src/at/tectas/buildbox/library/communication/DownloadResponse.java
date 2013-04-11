package at.tectas.buildbox.library.communication;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import at.tectas.buildbox.library.helpers.IJsonSerialize;
import at.tectas.buildbox.library.helpers.JsonHelper;

import com.google.gson.JsonObject;

public class DownloadResponse implements IJsonSerialize, Parcelable {

	private static final String TAG = "DownloadResponse";
	
	public static JsonHelper helper = new JsonHelper();
	
	public DownloadStatus status = DownloadStatus.Pending;
	public DownloadPackage pack = null;
	public String mime = null;
	public int progress = 0;
	public int fileSize = 0;
	
	public DownloadResponse () {
		
	}
	
	public DownloadResponse (DownloadPackage pack, JsonObject json) {
		this.pack = pack;
		this.status = this.getStatusFromString(DownloadResponse.helper.tryGetStringFromJson("status", json));
		this.mime = DownloadResponse.helper.tryGetStringFromJson("mime", json);
		this.progress = json.get("progress").getAsInt();
	}
	
	public DownloadResponse (DownloadPackage pack, DownloadStatus status) {
		this.status = status;
		this.pack = pack;
		this.mime = this.getMimeType(pack.getFilename(), pack.getFilename().length() - 3);
	}
	
	public DownloadResponse(Parcel source) {
		String statusString = source.readString();
		
		try {
			this.status = DownloadStatus.valueOf(statusString);
		}
		catch (IllegalArgumentException e) {
			Log.w(TAG, "DownloadStatus couldn't be parsed: " + statusString);
		}
		
		this.mime = source.readString();
		this.progress = source.readInt();
	}

	public String getKey() {
		if (this.pack != null) {
			return this.pack.getKey();
		}
		else {
			return null;
		}
	}
	
	public String getMimeType (String filename, int offset) {
		int index = this.getIndex(filename, ".", offset);
		
		if (index == -1) {
			return filename;
		}
		else {
			return filename.substring(index + 1, filename.length());
		}
		
	}
	
	private int getIndex (String filename, String substring, int offset) {
		
		int index = -1;
		
		if (offset <= 0) {
			return filename.indexOf(substring, 0);
		}
		else {
			index = filename.indexOf(substring, offset);
			
			if (index == -1)
				return this.getIndex(filename, substring, offset - 3);
		}
		
		return index;
	}
	
	public String getStatusString(DownloadStatus status) {
		if (status == DownloadStatus.Pending) {
			return "Pending";
		}
		else if (status == DownloadStatus.Successful) {
			return "Successfull";
		}
		else if (status == DownloadStatus.Md5mismatch) {
			return "Md5mismatch";
		}
		else if (status == DownloadStatus.Done) {
			return "Done";
		}
		else if (status == DownloadStatus.Aborted) {
			return "Aborted";
		}
		else {
			return "Broken";
		}
	}
	
	public DownloadStatus getStatusFromString(String status) {
		if (status.equals("Pending")) {
			return DownloadStatus.Pending;
		}
		else if (status.equals("Successfull")) {
			return DownloadStatus.Successful;
		}
		else if (status.equals("Md5mismatch")) {
			return DownloadStatus.Md5mismatch;
		}
		else if (status.equals("Done")) {
			return DownloadStatus.Done;
		}
		else if (status.equals("Aborted")) {
			return DownloadStatus.Aborted;
		}
		else {
			return DownloadStatus.Broken;
		}
	}
	
	public JsonObject serializeToJson() {
		JsonObject json = new JsonObject();
		
		json.addProperty("status", this.getStatusString(this.status));
		json.addProperty("mime", this.mime);
		json.addProperty("progress", this.progress);
		
		return json;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.status.name());
		dest.writeString(this.mime);
		dest.writeInt(this.progress);
	}
	
	public static final Parcelable.Creator<DownloadResponse> CREATOR = new Parcelable.Creator<DownloadResponse>() {

		@Override
		public DownloadResponse createFromParcel(Parcel source) {
			return new DownloadResponse(source);
		}

		@Override
		public DownloadResponse[] newArray(int size) {
			return new DownloadResponse[size];
		}
	};
}
