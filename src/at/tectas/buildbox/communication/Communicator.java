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
				return Communicator.getBitmap(params[0]);
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
	
	public static Bitmap getBitmap(String url) throws IOException {
	    Bitmap bitmap = null;
	    InputStream in = null;
	    BufferedOutputStream out = null;

	    try {
	    	HttpResponse response = Communicator.getResponse(url);
	        
	        in = new BufferedInputStream(response.getEntity().getContent());
	        
	        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	        out = new BufferedOutputStream(dataStream);
	        
	        byte[] buffer = new byte[1024];
	        
	        int len;
	        
	        while ((len = in.read(buffer)) != -1) {
	            out.write(buffer, 0, len);
	        }
	        
	        out.flush();

	        final byte[] data = dataStream.toByteArray();
	        BitmapFactory.Options options = new BitmapFactory.Options();

	        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
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
	
	public static void downloadFileToSd(String url, String directory, String filename) throws IOException {
		if (url != null && !url.isEmpty()) {
			InputStream in = null;
		    FileOutputStream out = null;
	
		    try {
		        HttpResponse response = Communicator.getResponse(url);
		        
		        String responseFilename = Communicator.tryGetFilenameFromResponse(response);
		        
		        in = new BufferedInputStream(response.getEntity().getContent());
		        
		        File file = new File(directory, responseFilename == null? filename : responseFilename);
		        
		        out = new FileOutputStream(file);
		        
		        byte[] buffer = new byte[1024];
		        
		        int len;
		        
		        while ((len = in.read(buffer)) != -1) {
		            out.write(buffer, 0, len);
		        }
		        
		        out.flush();
	
		    } catch (Exception e) {
		        Log.e(TAG, e.getMessage());
		    } finally {
		    	if (in != null)
		    		in.close();
		    	if (out != null)
		    		out.close();
		    }
		}
	}
	
	@SuppressWarnings("null")
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
	
	public static String tryGetFilenameFromResponse(HttpResponse response) {
		Header[] headers = response.getHeaders("Content-Disposition");
		
		for (Header header: headers) {
			String headerValue = header.getValue();
			
			if (headerValue.contains(".")) {
				return headerValue;
			}
		}
		
		return null;
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

