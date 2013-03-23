package at.tectas.buildbox.communication;

public enum DownloadStatus {
	Pending, 
	Successful, 
	Broken, 
	Md5mismatch, 
	Done, 
	Aborted
}
