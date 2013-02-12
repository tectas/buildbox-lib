package at.tectas.buildbox.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

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
	
	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator (String URL) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator();
		
		communicator.execute(URL);
		
		return communicator;
	}

	public JSONObjectAsyncCommunicator executeJSONObjectAsyncCommunicator (String URL, ICommunicatorCallback callback) {
		JSONObjectAsyncCommunicator communicator = new JSONObjectAsyncCommunicator(callback);
		
		communicator.execute(URL);
		
		return communicator;
	}
	
	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator (String URL) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator();
		
		communicator.execute(URL);
		
		return communicator;
	}
	
	public JSONArrayAsyncCommunicator executeJSONArrayAsyncCommunicator (String URL, ICommunicatorCallback callback) {
		JSONArrayAsyncCommunicator communicator = new JSONArrayAsyncCommunicator(callback);
		
		communicator.execute(URL);
		
		return communicator;
	}
	
	public static String getString(String URL) throws Exception {
		if (URL != null) {
		    BufferedReader in = null;
		    try 
		    {
		        HttpClient client = new DefaultHttpClient();
		        client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "android");
		        HttpGet request = new HttpGet();
		        request.setURI(new URI(URL));
		        HttpResponse response = client.execute(request);
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
	
	public static JSONObject getJSONObject(String URL) throws Exception 
	{	
		String respond = Communicator.getString(URL);
		
        JSONObject json = new JSONObject(respond);
        
        return json;
	}
	
	public static JSONArray getJSONArray(String URL) throws Exception 
	{	
		String respond = Communicator.getString(URL);
		
        JSONArray json = new JSONArray(respond);
        
        return json;
	}
}

