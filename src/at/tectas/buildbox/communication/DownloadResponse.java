package at.tectas.buildbox.communication;

public class DownloadResponse {
	public enum DownloadStatus {
		Pending, Successful, Broken, Md5mismatch
	}
	
	public DownloadStatus status = DownloadStatus.Pending;
	public String fileName = null;
	public String mime = null;
	public String md5sum = null;
	public int progress = 0;
	
	public DownloadResponse () {
		
	}
	
	public DownloadResponse (DownloadStatus status, String filename, String mime, String md5sum) {
		this.status = status;
		this.fileName = filename;
		this.mime = mime;
		this.md5sum = md5sum;
	}
}
