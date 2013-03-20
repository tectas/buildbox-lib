package at.tectas.buildbox.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.params.CoreProtocolPNames;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;

public class Communicator {
	public enum CallbackType {
		UI, Service
	}
	
	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(
			DownloadPackage pack, 
			IDownloadProgressCallback updateCallback, 
			IDownloadFinishedCallback finishedCallback,
			IDownloadCancelledCallback cancelCallback
			) {
		
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(this, pack.getKey(), updateCallback, finishedCallback, cancelCallback);
		
		communicator = (DownloadAsyncCommunicator) communicator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pack);
		
		return communicator;
	}
	
	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(
			DownloadPackage pack, 
			Hashtable<CallbackType, IDownloadProgressCallback> updateCallback, 
			Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallback,
			Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallback) {
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(this, pack.getKey(), updateCallback, finishedCallback, cancelCallback);
		
		communicator = (DownloadAsyncCommunicator) communicator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pack);
		
		return communicator;
	}
	
	public BitmapAsyncCommunicator executeBitmapAsyncCommunicator (String url) {
		BitmapAsyncCommunicator communicator = new BitmapAsyncCommunicator(this);
		
		communicator = (BitmapAsyncCommunicator)communicator.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
		
		return communicator;
	}
	
	public BitmapAsyncCommunicator executeBitmapAsyncCommunicator (String url, ImageView view, ICommunicatorCallback callback) {
		BitmapAsyncCommunicator communicator = new BitmapAsyncCommunicator(this, view, callback);
		
		communicator = (BitmapAsyncCommunicator)communicator.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
		
		return communicator;
	}
	
	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator (String url) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator(this);
		
		communicator = (JSONObjectAsyncCommunicator)communicator.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
		
		return communicator;
	}

	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator (String url, ICommunicatorCallback callback) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator(this, callback);
		
		communicator = (JSONObjectAsyncCommunicator)communicator.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
		
		return communicator;
	}
	
	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator (String url) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator(this);

		communicator = (JSONArrayAsyncCommunicator)communicator.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
		
		return communicator;
	}
	
	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator (String url, ICommunicatorCallback callback) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator(this, callback);

		communicator = (JSONArrayAsyncCommunicator)communicator.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
		
		return communicator;
	}
	
	public String getString(String url) throws Exception {
		if (url != null) {
		    BufferedReader in = null;
		    try 
		    {
		    	HttpURLConnection connection = Communicator.getConnection(url);
		    	
		        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		        StringBuilder sb = new StringBuilder();
		        String line = "";
	
		        while ((line = in.readLine()) != null) 
		        {
		            sb.append(line);
		        }
		        in.close();
		        return sb.toString();
		    }
		    catch (Exception e) {
		    	e.printStackTrace();
		    	return null;
		    }
		    finally 
		    {
		        if (in != null) 
		        {
		            try 
		            {
		                in.close();
		            } 
		            catch (IOException e)    
		            {
		            	e.printStackTrace();
		            }
		        }
		    }
		}
		else {
			return null;
		}
	}
	
	public Bitmap getBitmap(String url, int width) throws IOException {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;

	    try {
	    	HttpURLConnection connection = Communicator.getConnection(url);
	        
	        boolean statusOk = Communicator.checkHttpStatus(connection);
	        
	        if (statusOk) {
	    	
		        in = new BufferedInputStream(connection.getInputStream());
		        
		        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		        out = new BufferedOutputStream(dataStream);
		        
		        byte[] buffer = new byte[1024];
		        
		        int len, processed = 0;
		        
		        while ((len = in.read(buffer)) != -1) {	        	
		            out.write(buffer, 0, len);
		            
		        	processed += len;
		            
		            if (processed % 10240 <= 512) {
		            	out.flush();
		            }
		        }
		        
		        out.flush();
	
		        final byte[] data = dataStream.toByteArray();
		        
		        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
	
		        boundsOptions.inJustDecodeBounds = true;
		        
		        BitmapFactory.decodeByteArray(data, 0, data.length, boundsOptions);
		        
		        BitmapFactory.Options options = new BitmapFactory.Options();
		        
		        if (boundsOptions.outWidth != 0 && width != 0) {
		        	options.inSampleSize = Math.round((float)boundsOptions.outWidth / (((float)width / 5) * 4));
		        }
		        
		        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
	        }
	        else {
	        	throw new Exception("Bad request at: " + url + ". " + "Statuscode: " + connection.getResponseCode());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	if (in != null)
	    		in.close();
	    	if (out != null)
	    		out.close();
	    }

	    return bitmap;
	}
	
	public JsonObject getJsonObject(String url) throws Exception 
	{	
		String respond = this.getString(url);
		
		JsonParser parser = new JsonParser();
		
        JsonObject json = parser.parse(respond).getAsJsonObject();
        
        return json;
	}
	
	public JsonArray getJsonArray(String url) throws Exception 
	{	
		String respond = this.getString(url);
		
		JsonParser parser = new JsonParser();
		
        JsonArray json = parser.parse(respond).getAsJsonArray();
        
        return json;
	}
	
	public DownloadResponse downloadFileToSd(DownloadPackage pack, IDownloadAsyncCommunicator progressHandler) {
		DownloadResponse result = new DownloadResponse(pack, DownloadStatus.Pending);
		
		if (pack.url != null) {
			
			InputStream in = null;
		    FileOutputStream out = null;
		    
		    try {
		    	HttpURLConnection connection = Communicator.getConnection(pack.url);
		        
		        boolean statusOk = Communicator.checkHttpStatus(connection);
		        
		        if (statusOk) {
		        	
			        String responseFilename = Communicator.tryGetFilenameFromConnection(connection);
			        
			        long fileSize = connection.getContentLength();
			        
			        in = new BufferedInputStream(connection.getInputStream());
			        
				    MessageDigest md = MessageDigest.getInstance("MD5");
			        
			        in = new DigestInputStream(in, md);
			        
			        File dir = new File(pack.directory);
			        
			        if (!dir.exists()) {
			        	dir.mkdir();
			        }
			        
			        pack.setFilename(responseFilename == null? pack.getFilename() : responseFilename);
			        
			        File file = new File(pack.directory, pack.getFilename());
			        
			        if (file.exists()) {
			        	file.delete();
			        }
			        
			        out = new FileOutputStream(file);
			        
			        byte[] buffer = new byte[1024];
			        
			        int len;
			        
			        Integer processed = 0, publishSteps = 0;
			        
			        publishSteps = (int)(fileSize / 100);
			        
			        result.progress = (int)((100d / fileSize) * processed);
	            	progressHandler.indirectPublishProgress(result);
			        
			        while ((len = in.read(buffer)) != -1) {
			            out.write(buffer, 0, len);
			            
			            processed += len;
			            
			            if (progressHandler.isCancelled()) {
			            	result.status = DownloadStatus.Aborted;
			            	return result;
			            }
			            
			            if (processed % (publishSteps < 1048576?1048576:publishSteps) <= 512) {
			            	out.flush();
			            	result.progress = (int)((100d / fileSize) * processed);
			            	progressHandler.indirectPublishProgress(result);
			            }
			        }
			        
			        result.progress = (int)((100d / fileSize) * processed);
			        progressHandler.indirectPublishProgress(result);
			        
			        out.flush();
			        
			        if (processed != (int)fileSize) {
			        	if (in != null) {
			        		in.close();
			        		in = null;
			        	}
			        		
			        	if (out != null) {
			        		out.close();
			        		out = null;
			        	}
			        	
			        	result = this.downloadFileToSd(pack, progressHandler, processed, 1);
			        }
			        else {
			        	if (pack.md5sum != null) {
					        byte[] sumBytes = md.digest();
					        
					        StringBuffer hexString = new StringBuffer();
					        
					        for (int i=0; i<sumBytes.length; i++) {
					        	
					        	StringBuffer hex = new StringBuffer();
					        	
					        	hex.append(Integer.toHexString(0xFF & sumBytes[i]));
					        	
					        	if (hex.length() == 1)
					        		hexString.append(0);
					        	
					            hexString.append(hex);
					        }
					        
					        String sum = hexString.toString();
					        
					        if (sum.equals(pack.md5sum)) 
					        	result.status = DownloadStatus.Successful;
					        else {
					        	
					        	result.status = DownloadStatus.Md5mismatch;
					        }
			        	}
			        	else {
			        		result.status = DownloadStatus.Done;
			        	}
			        }
		        }
		        else {
		        	result.status = DownloadStatus.Broken;
		        }
		    }
	        catch (Exception e) {
	        	e.printStackTrace();
	        	result.status = DownloadStatus.Broken;
	        }
		    finally {
		    	if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	if (out != null)
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    }
		}
		return result;
	}
	
	public DownloadResponse downloadFileToSd(DownloadPackage pack, IDownloadAsyncCommunicator progressHandler, int alreadyDownloaded, int retries) {
		
		DownloadResponse result = new DownloadResponse(pack, DownloadStatus.Pending);
		
		if (pack.url != null && !pack.url.isEmpty() && retries <= 5) {
			InputStream in = null;
		    FileOutputStream out = null;
		    
		    try {
		        HttpURLConnection connection = Communicator.getConnection(pack.url, alreadyDownloaded);
		        
		        boolean statusOk = Communicator.checkHttpStatus(connection);
		        
		        if (statusOk) {
		        	
			        Boolean ranges = Communicator.tryGetAcceptRangesFromConnection(connection);
			        
			        long fileSize = connection.getContentLength();
			        
			        in = new BufferedInputStream(connection.getInputStream());
			        
				    MessageDigest md = MessageDigest.getInstance("MD5");
			        
			        in = new DigestInputStream(in, md);
			        
			        File file = new File(pack.directory, pack.getFilename());
			        
			        if (!ranges && file.exists()) {
			        	file.delete();
			        }
			        
			        out = new FileOutputStream(file, ranges);
			        
			        byte[] buffer = new byte[1024];
			        
			        int len;
			        
			        Integer processed = ranges ? alreadyDownloaded : 0;
			        
			        Integer publishSteps = (int)(fileSize / 100);
			        
			        result.progress = (int)((100d / fileSize) * processed);
	            	progressHandler.indirectPublishProgress(result);
			        
			        while ((len = in.read(buffer)) != -1) {
			            out.write(buffer, 0, len);
			            processed += len;
			            
			            if (progressHandler.isCancelled()) {
			            	result.status = DownloadStatus.Aborted;
			            	return result;
			            }
			            
			            if (processed % (publishSteps < 1048576?1048576:publishSteps) <= 512) {
			            	out.flush();
			            	result.progress = (int)((100d / fileSize) * processed);
			            	progressHandler.indirectPublishProgress(result);
			            }
			        }
			        
	            	result.progress = (int)((100d / fileSize) * processed);
	            	progressHandler.indirectPublishProgress(result);
			        
			        out.flush();
			        
			        if (processed != (int)fileSize) {
			        	if (in != null) {
			        		in.close();
			        		in = null;
			        	}
			        		
			        	if (out != null) {
			        		out.close();
			        		out = null;
			        	}
			        	
			        	result = this.downloadFileToSd(pack, progressHandler, processed, retries + 1);
			        }
			        else {
			        	if (pack.md5sum != null) {
					        byte[] sumBytes = md.digest();
					        
					        StringBuffer hexString = new StringBuffer();
					        
					        for (int i=0; i<sumBytes.length; i++) {
					        	
					        	StringBuffer hex = new StringBuffer();
					        	
					        	hex.append(Integer.toHexString(0xFF & sumBytes[i]));
					        	
					        	if (hex.length() == 1)
					        		hexString.append(0);
					        	
					            hexString.append(hex);
					        }
					        
					        String sum = hexString.toString();
					        
					        if (sum.equals(pack.md5sum)) 
					        	result.status = DownloadStatus.Successful;
					        else {
					        	
					        	result.status = DownloadStatus.Md5mismatch;
					        }
			        	}
			        	else {
			        		result.status = DownloadStatus.Successful;
			        	}
			        }
		        }
		        else {
		        	result.status = DownloadStatus.Broken;
		        }
		    }
		    catch (Exception e) {
		    	e.printStackTrace();
	        	result.status = DownloadStatus.Broken;
		    }
		    finally {
		    	if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	if (out != null)
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    }
		}
		return result;
	}
	
	public static HttpURLConnection getConnection(String urlString) throws ClientProtocolException, IOException, URISyntaxException {
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.addRequestProperty(CoreProtocolPNames.USER_AGENT, "android");
		urlConnection.addRequestProperty("Cache-Control", "no-cache");
        
		urlConnection.connect();
		
        return urlConnection;
	}
	
	public static HttpURLConnection getConnection(String urlString, int alreadyDownloaded) throws ClientProtocolException, IOException, URISyntaxException {
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.addRequestProperty(CoreProtocolPNames.USER_AGENT, "android");
		urlConnection.addRequestProperty("Cache-Control", "no-cache");
		urlConnection.addRequestProperty("Range", "bytes=" + alreadyDownloaded);
        
		urlConnection.connect();
		
        return urlConnection;
	}
	
	public static Boolean tryGetAcceptRangesFromConnection(HttpURLConnection response) {
		String header = response.getHeaderField("Accept-Ranges");
		
		if (!header.isEmpty() || header != "none") {
			return true;
		}
		
		return false;
	}
	
	public static String tryGetFilenameFromConnection(HttpURLConnection connection) {
		String header = connection.getHeaderField("Content-Disposition");
		
		if (!(header.isEmpty()) && header.contains(".")) {
			return header.split("=")[1];
		}
		
		return null;
	}
	
	public static boolean checkHttpStatus(HttpURLConnection connection) throws IOException {
		int status = connection.getResponseCode();
		
		if (status >= 400) {
			return false;
		}
		
		return true;
	}
}

