package at.tectas.buildbox.helpers;

import java.util.ArrayList;

import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.IDownloadProcessFinishedCallback;
import at.tectas.buildbox.communication.IDownloadProcessProgressCallback;

public class DownloadPackage {
	public String url = null;
	public String directory = null;
	public String filename = null;
	public String md5sum = null;
	public ArrayList<IDownloadProcessProgressCallback> updateCallbacks = new ArrayList<IDownloadProcessProgressCallback>();
	public ArrayList<IDownloadProcessFinishedCallback> finishedCallbacks = new ArrayList<IDownloadProcessFinishedCallback>();
	public DownloadResponse response = null;	
}
