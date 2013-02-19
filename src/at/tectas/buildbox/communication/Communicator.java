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
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;

public class Communicator {
	public static final String TAG = "Communicator";
	
	public class JSONObjectAsyncCommunicator extends AsyncTask<String, Integer, JSONObject> {
		
		private ICommunicatorCallback callbackListener = null;
		
		public JSONObjectAsyncCommunicator () {
			
		}
		
		public JSONObjectAsyncCommunicator (ICommunicatorCallback callback) {
			this.callbackListener = callback;
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				return Communicator.getJSONObject(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			if (this.callbackListener != null)
				this.callbackListener.updateWithJSONObject(result);
			super.onPostExecute(result);
		}
	}
	
	public class JSONArrayAsyncCommunicator extends AsyncTask<String, Integer, JSONArray> {
		
		private ICommunicatorCallback callbackListener = null;
		
		public JSONArrayAsyncCommunicator () {
			
		}
		
		public JSONArrayAsyncCommunicator (ICommunicatorCallback callback) {
			this.callbackListener = callback;
		}
		
		@Override
		protected JSONArray doInBackground(String... params) {
			try {
				return Communicator.getJSONArray(params[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(JSONArray result) {
			if (this.callbackListener != null)
				this.callbackListener.updateWithJSONArray(result);
			super.onPostExecute(result);
		}
	}
	
	public class BitmapAsyncCommunicator extends AsyncTask<String, Integer, Bitmap> {
		
		private ImageView view = null;
		private ICommunicatorCallback callbackListener = null;
		
		public BitmapAsyncCommunicator () {

		}
		
		public BitmapAsyncCommunicator (ImageView view, ICommunicatorCallback callback) {
			this.view = view;
			this.callbackListener = callback;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				return Communicator.getBitmap(params[0], view.getWidth());
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
		
		private ArrayList<IDownloadProcessProgressCallback> updateListener = new ArrayList<IDownloadProcessProgressCallback>();
		private ArrayList<IDownloadProcessFinishedCallback> finishedListener = new ArrayList<IDownloadProcessFinishedCallback>();
		
		public DownloadAsyncCommunicator (IDownloadProcessProgressCallback updateCallback, IDownloadProcessFinishedCallback finishedCallback) {
			this.updateListener.add(updateCallback);
			this.finishedListener.add(finishedCallback);
		}
		
		public DownloadAsyncCommunicator (ArrayList<IDownloadProcessProgressCallback> updateCallback, ArrayList<IDownloadProcessFinishedCallback> finishedCallback) {
			this.updateListener = updateCallback;
			this.finishedListener = finishedCallback;
		}
		
		public boolean removeProgressListener (IDownloadProcessProgressCallback callback) {
			return this.updateListener.remove(callback);
		}
		
		@Override
		protected DownloadResponse doInBackground(String... params) {
			try {
				return Communicator.downloadFileToSd(params[0], params[1], params[2], params[3], this);
			}
			catch (Exception e) {
				return new DownloadResponse();
			}
		}
		
		public void indirectPublishProgress(Integer progress) {
			this.publishProgress(progress);
		}
		
		protected void onProgressUpdate(Integer... progress) {
			if (this.updateListener != null && this.updateListener.size() != 0)
				for (IDownloadProcessProgressCallback callback: this.updateListener) {
					callback.updateDownloadProgess(progress[0]);
				}
	     }
		
		@Override
		protected void onPostExecute(DownloadResponse result) {
			if (this.finishedListener != null && this.finishedListener.size() != 0) {
				for (IDownloadProcessFinishedCallback callback: this.finishedListener) {
					callback.downloadFinished(result);
				}
			}
			super.onPostExecute(result);
		}
	}
	
	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(String url, String directory, String filename, String md5sum, IDownloadProcessProgressCallback updateCallback, IDownloadProcessFinishedCallback finishedCallback) {
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(updateCallback, finishedCallback);
		
		communicator.execute(new String[] { url, directory, filename, md5sum });
		
		return communicator;
	}
	
	public DownloadAsyncCommunicator executeDownloadAsyncCommunicator(String url, String directory, String filename, String md5sum, ArrayList<IDownloadProcessProgressCallback> updateCallback, ArrayList<IDownloadProcessFinishedCallback> finishedCallback) {
		DownloadAsyncCommunicator communicator = new DownloadAsyncCommunicator(updateCallback, finishedCallback);
		
		communicator.execute(new String[] { url, directory, filename, md5sum });
		
		return communicator;
	}
	
	public BitmapAsyncCommunicator executeBitmapAsyncCommunicator (String url) {
		BitmapAsyncCommunicator communicator = new BitmapAsyncCommunicator();
		
		communicator.execute(url);
		
		return communicator;
	}
	
	public BitmapAsyncCommunicator executeBitmapAsyncCommunicator (String url, ImageView view, ICommunicatorCallback callback) {
		BitmapAsyncCommunicator communicator = new BitmapAsyncCommunicator(view, callback);
		
		communicator.execute(url);
		
		return communicator;
	}
	
	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator (String url) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator();
		
		communicator.execute(url);
		
		return communicator;
	}

	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator (String url, ICommunicatorCallback callback) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator(callback);
		
		communicator.execute(url);
		
		return communicator;
	}
	
	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator (String url) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator();
		
		communicator.execute(url);
		
		return communicator;
	}
	
	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator (String url, ICommunicatorCallback callback) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator(callback);
		
		communicator.execute(url);
		
		return communicator;
	}
	
	public static String getString(String url) throws Exception {
		if (url != null) {
		    BufferedReader in = null;
		    try 
		    {
		    	HttpResponse response = Communicator.getResponse(url);
		        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
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
		    	Log.e(Communicator.TAG, e.getMessage());
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
		                Log.d(Communicator.TAG, e.toString());
		            }
		        }
		    }
		}
		else {
			return null;
		}
	}
	
	public static Bitmap getBitmap(String url, int width) throws IOException {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;

	    try {
	    	HttpResponse response = Communicator.getResponse(url);
	        
	        in = new BufferedInputStream(response.getEntity().getContent());
	        
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
	    } catch (Exception e) {
	        Log.e(TAG, e.getMessage());
	    } finally {
	    	if (in != null)
	    		in.close();
	    	if (out != null)
	    		out.close();
	    }

	    return bitmap;
	}
	
	public static DownloadResponse downloadFileToSd(String url, String directory, String filename, String md5sum, IDownloadAsyncCommunicator progressHandler) throws IOException {
		String[] splittedFilename = filename.split(".");
		
		DownloadResponse result = new DownloadResponse(DownloadStatus.Broken, filename, splittedFilename[splittedFilename.length -1], md5sum);
		if (url != null && !url.isEmpty()) {
			InputStream in = null;
		    FileOutputStream out = null;
		    
		    try {
		        HttpResponse response = Communicator.getResponse(url);
		        
		        String responseFilename = Communicator.tryGetFilenameFromResponse(response);
		        
		        long fileSize = response.getEntity().getContentLength();
		        
		        in = new BufferedInputStream(response.getEntity().getContent());
		        
			    MessageDigest md = MessageDigest.getInstance("MD5");
		        
		        in = new DigestInputStream(in, md);
		        
		        File file = new File(directory, responseFilename == null? filename : responseFilename);
		        
		        out = new FileOutputStream(file);
		        
		        byte[] buffer = new byte[1024];
		        
		        int len;
		        
		        Integer processed = 0;
		        
		        while ((len = in.read(buffer)) != -1) {
		            out.write(buffer, 0, len);
		            
		            processed += len;
		            
		            progressHandler.indirectPublishProgress((int)((100 / fileSize) * processed));
		            
		            if (processed % 10240 <= 512) {
		            	out.flush();
		            }
		        }
		        
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
		        	
		        	result = Communicator.downloadFileToSd(url, directory, responseFilename == null? filename : responseFilename, md5sum, progressHandler, processed, 1);
		        }
		        else {
			        byte[] sumBytes = md.digest();
			        
			        String sum = new String(sumBytes);
			        
			        if (sum == md5sum)
			        	result.status = DownloadStatus.Successful;
			        else {
			        	result.status = DownloadStatus.Md5mismatch;
			        	
			        	result = Communicator.downloadFileToSd(url, directory, responseFilename == null? filename : responseFilename, md5sum, progressHandler, processed, 1);
			        }
		        }
		        
		    } catch (Exception e) {
		        Log.e(TAG, e.getMessage());
		    } finally {
		    	if (in != null)
		    		in.close();
		    	if (out != null)
		    		out.close();
		    }
		}
		return result;
	}
	
	public static DownloadResponse downloadFileToSd(String url, String directory, String filename, String md5sum, IDownloadAsyncCommunicator progressHandler, int alreadyDownloaded, int retries) throws IOException {
		String[] splittedFilename = filename.split(".");
		
		DownloadResponse result = new DownloadResponse(DownloadStatus.Broken, filename, splittedFilename[splittedFilename.length -1], md5sum);
		
		if (url != null && !url.isEmpty() && retries <= 10) {
			InputStream in = null;
		    FileOutputStream out = null;
		    
		    try {
		        HttpResponse response = Communicator.getResponse(url, alreadyDownloaded);
		        
		        Boolean ranges = Communicator.tryGetAcceptRangesFromResponse(response);
		        
		        long fileSize = response.getEntity().getContentLength();
		        
		        in = new BufferedInputStream(response.getEntity().getContent());
		        
			    MessageDigest md = MessageDigest.getInstance("MD5");
		        
		        in = new DigestInputStream(in, md);
		        
		        File file = new File(directory, filename);
		        
		        out = new FileOutputStream(file, ranges);
		        
		        byte[] buffer = new byte[1024];
		        
		        int len;
		        
		        Integer processed = ranges ? alreadyDownloaded : 0;
		        
		        while ((len = in.read(buffer)) != -1) {
		            out.write(buffer, 0, len);
		            processed += len;
		            progressHandler.indirectPublishProgress((int)((100 / fileSize) * processed));
		            
		            if (processed % 10240 <= 512) {
		            	out.flush();
		            }
		        }
		        
		        out.flush();
		        
		        byte[] sumBytes = md.digest();
		        
		        String sum = new String(sumBytes);
		        
		        if (sum == md5sum)
		        	result.status = DownloadStatus.Successful;
		        else {
		        	result.status = DownloadStatus.Md5mismatch;
		        	
		        	result = Communicator.downloadFileToSd(url, directory, filename, md5sum, progressHandler, processed, retries + 1);
		        }
		        
		    } catch (Exception e) {
		        Log.e(TAG, e.getMessage());
		    } finally {
		    	if (in != null)
		    		in.close();
		    	if (out != null)
		    		out.close();
		    }
		}
		return result;
	}
	
	public static HttpResponse getResponse(String url) throws ClientProtocolException, IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
        HttpGet request = new HttpGet();
        request.setURI(new URI(url));
        HttpResponse response = client.execute(request);
        Header header = response.getFirstHeader("Location");
        
        if (header != null) {
        	response = Communicator.getResponse(header.getValue());
        }
        
        return response;
	}
	
	public static HttpResponse getResponse(String url, int alreadyDownloaded) throws ClientProtocolException, IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
        client.getParams().setParameter("Range", "bytes=" + alreadyDownloaded + "-");
        HttpGet request = new HttpGet();
        request.setURI(new URI(url));
        HttpResponse response = client.execute(request);
        Header header = response.getFirstHeader("Location");
        
        if (header != null) {
        	response = Communicator.getResponse(header.getValue());
        }
        
        return response;
	}
	
	public static Boolean tryGetAcceptRangesFromResponse(HttpResponse response) {
		Header header = response.getFirstHeader("Accept-Ranges");
		
		if (header.getValue().isEmpty() || header.getValue() != "none") {
			return true;
		}
		
		return false;
	}
	
	public static String tryGetFilenameFromResponse(HttpResponse response) {
		Header[] headers = response.getHeaders("Content-Disposition");
		
		for (Header header: headers) {
			String headerValue = header.getValue();
			
			if (!(headerValue.isEmpty()) && headerValue.contains(".")) {
				return headerValue.split("=")[1];
			}
		}
		
		return null;
	}
	
	public static Integer tryGetFilesizeFromResponse(HttpResponse response) {
		Header header = response.getFirstHeader("Content-Length");
		
		String headerValue = header.getValue();
		try {
			return Integer.valueOf(headerValue);
		}
		catch (NumberFormatException e) {
			Log.e(TAG, e.getMessage());
			return -1;
		}
	}
	
	public static JSONObject getJSONObject(String url) throws Exception 
	{	
		String respond = Communicator.getString(url);
		
        JSONObject json = new JSONObject(respond);
        
        return json;
	}
	
	public static JSONArray getJSONArray(String url) throws Exception 
	{	
		String respond = Communicator.getString(url);
		
        JSONArray json = new JSONArray(respond);
        
        return json;
	}
}

