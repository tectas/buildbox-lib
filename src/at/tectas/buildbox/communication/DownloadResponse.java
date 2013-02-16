package at.tectas.buildbox.communication;

public class DownloadResponse {
	public enum DownloadStatus {
		Successful, Broken, Md5mismatch
	}
	
	public DownloadStatus status = DownloadStatus.Broken;
	public String fileName = null;
	public String mime = null;
	
	public DownloadResponse () {
		
	}
	
	public DownloadResponse (DownloadStatus status, String filename, String mime) {
		this.status = status;
		this.fileName = filename;
		this.mime = mime;
	}
}
