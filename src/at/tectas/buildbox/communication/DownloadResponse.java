package at.tectas.buildbox.communication;

import at.tectas.buildbox.helpers.IJsonSerialize;
import at.tectas.buildbox.helpers.JsonHelper;

import com.google.gson.JsonObject;

public class DownloadResponse implements IJsonSerialize {
	public enum DownloadStatus {
		Pending, Successful, Broken, Md5mismatch, Done, Aborted
	}
	
	public static JsonHelper helper = new JsonHelper();
	
	public DownloadStatus status = DownloadStatus.Pending;
	public String fileName = null;
	public String mime = null;
	public String md5sum = null;
	public String url = null;
	public int progress = 0;
	
	public DownloadResponse () {
		
	}
	
	public DownloadResponse (JsonObject json) {
		this.status = this.getStatusFromString(DownloadResponse.helper.tryGetStringFromJson("status", json));
		this.fileName = DownloadResponse.helper.tryGetStringFromJson("filename", json);
		this.mime = DownloadResponse.helper.tryGetStringFromJson("mime", json);
		this.md5sum = DownloadResponse.helper.tryGetStringFromJson("md5sum", json);
		this.url = DownloadResponse.helper.tryGetStringFromJson("url", json);
		this.progress = json.get("progress").getAsInt();
	}
	
	public DownloadResponse (DownloadStatus status, String filename, String url, String md5sum) {
		this.status = status;
		this.fileName = filename;
		this.mime = this.getMimeType(filename, filename.length() - 3);
		this.url = url;
		this.md5sum = md5sum;
	}
	
	private String getMimeType (String filename, int offset) {
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
			return index;
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
		json.addProperty("filename", this.fileName);
		json.addProperty("mime", this.mime);
		json.addProperty("md5sum", this.md5sum);
		json.addProperty("url", this.url);
		json.addProperty("progress", this.progress);
		
		return json;
	}
}
