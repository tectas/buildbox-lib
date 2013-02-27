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
	
	public static final String TAG = "Communicator";
	
	public class JSONObjectAsyncCommunicator extends AsyncTask<String, Integer, JsonObject> {
		
		private ICommunicatorCallback callbackListener = null;
		private Communicator communicator = null;
		
		public JSONObjectAsyncCommunicator (Communicator communicator) {
			this.communicator = communicator;
		}
		
		public JSONObjectAsyncCommunicator (Communicator communicator, ICommunicatorCallback callback) {
			this(communicator);
			this.callbackListener = callback;
		}
		
		@Override
		protected JsonObject doInBackground(String... params) {
			try {
				return this.communicator.getJsonObject(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(JsonObject result) {
			if (this.callbackListener != null)
				this.callbackListener.updateWithJsonObject(result);
			super.onPostExecute(result);
		}
	}
	
	public class JSONArrayAsyncCommunicator extends AsyncTask<String, Integer, JsonArray> {
		
		private ICommunicatorCallback callbackListener = null;
		private Communicator communicator = null;
		
		public JSONArrayAsyncCommunicator (Communicator communicator) {
			this.communicator = communicator;
		}
		
		public JSONArrayAsyncCommunicator (Communicator communicator, ICommunicatorCallback callback) {
			this(communicator);
			this.callbackListener = callback;
		}
		
		@Override
		protected JsonArray doInBackground(String... params) {
			try {
				return this.communicator.getJsonArray(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(JsonArray result) {
			if (this.callbackListener != null)
				this.callbackListener.updateWithJsonArray(result);
			super.onPostExecute(result);
		}
	}
	
	public class BitmapAsyncCommunicator extends AsyncTask<String, Integer, Bitmap> {
		
		private ImageView view = null;
		private ICommunicatorCallback callbackListener = null;
		private Communicator communicator = null;
		
		public BitmapAsyncCommunicator (Communicator communicator) {
			this.communicator = communicator;
		}
		
		public BitmapAsyncCommunicator (Communicator communicator, ImageView view, ICommunicatorCallback callback) {
			this(communicator);
			this.view = view;
			this.callbackListener = callback;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				return this.communicator.getBitmap(params[0], view.getWidth());
			}
			catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (this.callbackListener != null)
				this.callbackListener.updateWithImage(this.view, result);
			super.onPostExecute(result);
		}
	}
	
	public class DownloadAsyncCommunicator extends IDownloadAsyncCommunicator {
		
		public String ID = null;
		private Communicator communicator = null;
		private Hashtable<CallbackType, IDownloadProgressCallback> updateListener = new Hashtable<CallbackType, IDownloadProgressCallback>();
		private Hashtable<CallbackType, IDownloadFinishedCallback> finishedListener = new Hashtable<CallbackType, IDownloadFinishedCallback>();
		private Hashtable<CallbackType, IDownloadCancelledCallback> cancelledListener = new Hashtable<Communicator.CallbackType, IDownloadCancelledCallback>();
		
		private DownloadAsyncCommunicator (Communicator communicator, String ID) {
			this.ID = ID;
			this.communicator = communicator;
		}
		
		public DownloadAsyncCommunicator (Communicator communicator, String ID, IDownloadProgressCallback updateCallback, IDownloadFinishedCallback finishedCallback, IDownloadCancelledCallback cancelCallback) {
			this(communicator, ID);
			this.updateListener.put(CallbackType.Service, updateCallback);
			
			this.finishedListener.put(CallbackType.Service, finishedCallback);
			
			this.cancelledListener.put(CallbackType.Service, cancelCallback);
		}
		
		public DownloadAsyncCommunicator (Communicator communicator, String ID, Hashtable<CallbackType, IDownloadProgressCallback> updateCallback, Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallback, Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallback) {
			this(communicator, ID);
			this.updateListener = updateCallback;
			this.finishedListener = finishedCallback;
			this.cancelledListener = cancelCallback;
		}
		
		public synchronized boolean removeProgressListener (CallbackType type) {
			if (this.updateListener.containsKey(type)) {
				IDownloadProgressCallback removedCallback = this.updateListener.remove(type);
				if (removedCallback != null) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		public synchronized boolean addProgressListener (CallbackType type, IDownloadProgressCallback callback) {
			if (this.updateListener.containsKey(type)) {
				this.updateListener.remove(type);
			}
			
			IDownloadProgressCallback newCallback = this.updateListener.put(type, callback);
			
			if (newCallback != null) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public synchronized boolean removeResultListener(CallbackType type) {
			if (this.finishedListener.containsKey(type)) {
				
				IDownloadFinishedCallback removedCallback = this.finishedListener.remove(type);
				
				if (removedCallback != null) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		public synchronized boolean addResultListener(CallbackType type, IDownloadFinishedCallback callback) {
			if (this.finishedListener.containsKey(type))
				this.finishedListener.remove(type);
			
			IDownloadFinishedCallback newCallback = this.finishedListener.put(type, callback);
				
			if (newCallback != null) {
				return true;
			}
			else {
				return false;
			}
			
		}
		
		public synchronized boolean removeCancelledListener (CallbackType type) {
			if (this.cancelledListener.containsKey(type)) {
				IDownloadCancelledCallback removedCallback = this.cancelledListener.remove(type);
				if (removedCallback != null) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		public synchronized boolean addCancelledListener (CallbackType type, IDownloadCancelledCallback callback) {
			if (this.cancelledListener.containsKey(type)) {
				this.cancelledListener.remove(type);
			}
			
			IDownloadCancelledCallback newCallback = this.cancelledListener.put(type, callback);
			
			if (newCallback != null) {
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		protected DownloadResponse doInBackground(String... params) {
			try {
				return this.communicator.downloadFileToSd(params[0], params[1], params[2], params[3], this);
			}
			catch (Exception e) {
				return new DownloadResponse();
			}
		}
		
		public void indirectPublishProgress(DownloadResponse response) {
			this.publishProgress(response);
		}
		
		protected void onProgressUpdate(DownloadResponse... response) {
			if (this.updateListener != null && this.updateListener.size() != 0)
				for (CallbackType callbackKey: this.updateListener.keySet()) {
					
					IDownloadProgressCallback listener = this.updateListener.get(callbackKey);
					
					if (listener != null) {
						listener.updateDownloadProgess(response[0]);
					}
				}
	     }
		
		@Override
		protected void onPostExecute(DownloadResponse result) {
			if (this.finishedListener != null && this.finishedListener.size() != 0) {
				for (CallbackType callbackKey: this.finishedListener.keySet()) {
					
					IDownloadFinishedCallback listener = this.finishedListener.get(callbackKey);
					
					if (listener != null) {
						listener.downloadFinished(result);
					}
				}
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled(DownloadResponse result) {
			
			if (result != null) {
				result.status = DownloadStatus.Aborted;
				result.md5sum = this.ID;
			}
			else {
				result = new DownloadResponse();
				result.status = DownloadStatus.Aborted;
				result.md5sum = this.ID;
			}
			
			for (CallbackType callbackKey: this.cancelledListener.keySet()) {
				
				IDownloadCancelledCallback listener = this.cancelledListener.get(callbackKey);
				
				if (listener != null) {
					listener.downloadCancelled(result);
				}
			}
		}
	}
	
	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(
			String url, 
			String directory, 
			String filename, 
			String md5sum, 
			IDownloadProgressCallback updateCallback, 
			IDownloadFinishedCallback finishedCallback,
			IDownloadCancelledCallback cancelCallback
			) {
		
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(this, md5sum == null? url: md5sum, updateCallback, finishedCallback, cancelCallback);
		
		communicator = (DownloadAsyncCommunicator) communicator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[] { url, directory, filename, md5sum });
		
		return communicator;
	}
	
	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(
			String url, 
			String directory, 
			String filename, 
			String md5sum, 
			Hashtable<CallbackType, IDownloadProgressCallback> updateCallback, 
			Hashtable<CallbackType, IDownloadFinishedCallback> finishedCallback,
			Hashtable<CallbackType, IDownloadCancelledCallback> cancelCallback) {
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(this, md5sum == null? url: md5sum, updateCallback, finishedCallback, cancelCallback);
		
		communicator = (DownloadAsyncCommunicator) communicator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[] { url, directory, filename, md5sum });
		
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
		        
		        if (boundsOptions.outWidth != 0 && width != 0)
		        	options.inSampleSize = (int)Math.floor(boundsOptions.outWidth / (((double)width / 4) * 3));
		        
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
	
	public DownloadResponse downloadFileToSd(String url, String directory, String filename, String md5sum, IDownloadAsyncCommunicator progressHandler) {
		DownloadResponse result = new DownloadResponse(DownloadStatus.Pending, filename, url, md5sum);
		
		if (url != null) {
			
			InputStream in = null;
		    FileOutputStream out = null;
		    
		    try {
		    	HttpURLConnection connection = Communicator.getConnection(url);
		        
		        boolean statusOk = Communicator.checkHttpStatus(connection);
		        
		        if (statusOk) {
		        	
			        String responseFilename = Communicator.tryGetFilenameFromConnection(connection);
			        
			        long fileSize = connection.getContentLength();
			        
			        in = new BufferedInputStream(connection.getInputStream());
			        
				    MessageDigest md = MessageDigest.getInstance("MD5");
			        
			        in = new DigestInputStream(in, md);
			        
			        File dir = new File(directory);
			        
			        if (!dir.exists()) {
			        	dir.mkdir();
			        }
			        
			        File file = new File(directory, responseFilename == null? filename : responseFilename);
			        
			        if (file.exists()) {
			        	file.delete();
			        }
			        
			        out = new FileOutputStream(file);
			        
			        byte[] buffer = new byte[1024];
			        
			        int len;
			        
			        Integer processed = 0;
			        
			        while ((len = in.read(buffer)) != -1) {
			            out.write(buffer, 0, len);
			            
			            processed += len;
			            
			            if (processed % 2097152 <= 512) {
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
			        	
			        	result = this.downloadFileToSd(url, directory, responseFilename == null? filename : responseFilename, md5sum, progressHandler, processed, 1);
			        }
			        else {
			        	if (md5sum != null) {
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
					        
					        if (sum.equals(md5sum)) 
					        	result.status = DownloadStatus.Successful;
					        else {
					        	
					        	result.status = DownloadStatus.Md5mismatch;
					        	
					        	result = this.downloadFileToSd(url, directory, responseFilename == null? filename : responseFilename, md5sum, progressHandler, processed, 1);
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
	
	public DownloadResponse downloadFileToSd(String url, String directory, String filename, String md5sum, IDownloadAsyncCommunicator progressHandler, int alreadyDownloaded, int retries) {
		
		DownloadResponse result = new DownloadResponse(DownloadStatus.Pending, filename, url, md5sum);
		
		if (url != null && !url.isEmpty() && retries <= 5) {
			InputStream in = null;
		    FileOutputStream out = null;
		    
		    try {
		        HttpURLConnection connection = Communicator.getConnection(url, alreadyDownloaded);
		        
		        boolean statusOk = Communicator.checkHttpStatus(connection);
		        
		        if (statusOk) {
		        	
			        Boolean ranges = Communicator.tryGetAcceptRangesFromConnection(connection);
			        
			        long fileSize = connection.getContentLength();
			        
			        in = new BufferedInputStream(connection.getInputStream());
			        
				    MessageDigest md = MessageDigest.getInstance("MD5");
			        
			        in = new DigestInputStream(in, md);
			        
			        File file = new File(directory, filename);
			        
			        if (!ranges && file.exists()) {
			        	file.delete();
			        }
			        
			        out = new FileOutputStream(file, ranges);
			        
			        byte[] buffer = new byte[1024];
			        
			        int len;
			        
			        Integer processed = ranges ? alreadyDownloaded : 0;
			        
			        while ((len = in.read(buffer)) != -1) {
			            out.write(buffer, 0, len);
			            processed += len;
			            
			            if (processed % 2097152 <= 128) {
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
			        	
			        	result = this.downloadFileToSd(url, directory, filename, md5sum, progressHandler, processed, 1);
			        }
			        else {
			        	if (md5sum != null) {
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
					        
					        if (sum.equals(md5sum)) 
					        	result.status = DownloadStatus.Successful;
					        else {
					        	
					        	result.status = DownloadStatus.Md5mismatch;
					        	
					        	result = this.downloadFileToSd(url, directory, filename, md5sum, progressHandler, processed, 1);
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

