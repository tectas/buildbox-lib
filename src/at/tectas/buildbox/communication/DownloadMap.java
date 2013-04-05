package at.tectas.buildbox.communication;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import at.tectas.buildbox.msteam.R;
import at.tectas.buildbox.communication.callbacks.interfaces.IDeserializeMapFinishedCallback;
import at.tectas.buildbox.content.items.properties.DownloadType;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


@SuppressLint("DefaultLocale")
public class DownloadMap extends Hashtable<DownloadKey, DownloadPackage> implements Parcelable {

	protected static final String TAG = "DownloadMap";
	private static final long serialVersionUID = 1L;

	public DownloadMap(Parcel source) {
		for (int i = 0; i < source.dataSize(); i++) {
			this.put((DownloadPackage)source.readParcelable(DownloadPackage.class.getClassLoader()));
		}
	}
	
	public DownloadMap() { }

	public synchronized DownloadPackage get (String md5sum) {
		for (DownloadKey key: this.keySet()) {
			if (key.md5sum.equals(md5sum)) {
				return this.get(key);
			}
		}
		
		return null;
	}
	
	public synchronized DownloadPackage get (int index) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == index) {
				return this.get(key);
			}
		}
		
		return null;
	}
	
	public synchronized DownloadPackage get (int startingIndex, DownloadType type) {
		if (startingIndex >= this.size()) {
			return null;
		}
		
		for (int i = startingIndex; i < this.size(); i++) {
			DownloadPackage pack = this.get(i);
			
			if (pack != null) {
				if (pack.type != null)
					if (pack.type.equals(type))
					return pack;
			}
		}
		
		return null;
	}
	
	public synchronized DownloadKey getKey(int index) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == index) {
				return key;
			}
		}
		return null;
	}
	
	public synchronized DownloadKey getKey(String md5sum) {
		for (DownloadKey key: this.keySet()) {
			if (key.md5sum.equals(md5sum)) {
				return key;
			}
		}
		return null;
	}
	
	public synchronized int getIndex(DownloadPackage pack) {
		Set<Entry<DownloadKey, DownloadPackage>> set = this.entrySet();
		
		for (Entry<DownloadKey, DownloadPackage> entry: set) {
			if (entry.getValue() != null && entry.getValue().equals(pack))
				return entry.getKey().index;
		}
		
		return -1;
	}
	
	private synchronized void changeIndex (int oldIndex, int newIndex) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == oldIndex) {
				key.index = newIndex;
				
				break;
			}
		}
	}
	
	public synchronized DownloadPackage put (String md5sum, DownloadPackage object) {
		if (md5sum != null) {
			DownloadKey oldKey = this.getKey((String)md5sum);
			DownloadPackage oldPackage = null;
			
			if (oldKey != null) {
				for (int i = oldKey.index + 1; i < this.size(); i++) {
					this.changeIndex(i, i - 1);
				}
				
				oldPackage = this.get(oldKey);
				
				this.remove(oldKey);
			}
			
			DownloadKey key = new DownloadKey();
			key.md5sum = md5sum;
			key.index = this.size();
			
			this.put(key, object);
			
			return oldPackage;
		}
		else {
			return null;
		}
	}
	
	public synchronized DownloadPackage put (DownloadPackage value) {
		return this.put((String)value.getKey(), value);
	}
	
	public synchronized boolean insert(int index, DownloadPackage object) {
		if (object != null && object.getKey() != null) {
			if (index > this.size()) {
				return false;
			}
			else {
				DownloadKey newKey = new DownloadKey();
				newKey.index = index;
				newKey.md5sum = object.getKey();
				
				for (int i = index + 1; i < this.size(); i++) {
					this.changeIndex(i, i + 1);
				}
				
				this.put(newKey, object);
				return true;
			}
		}
		return false;
	}
	
	public synchronized DownloadPackage remove(int index) {
		DownloadKey key = this.getKey(index);
		
		return this.remove(key);
	}
	
	
	public synchronized DownloadPackage remove(String md5sum) {
		DownloadKey key = this.getKey(md5sum);
		
		return this.remove(key);
	}
	
	@Override
	public synchronized DownloadPackage remove(Object key) {
		DownloadKey downloadKey = (DownloadKey) key;
		
		for (int i = downloadKey.index + 1; i < this.size(); i++) {
			this.changeIndex(i, i - 1);
		}
		
		return super.remove(key);
	}
	
	public synchronized boolean containsKey(String md5sum) {
		DownloadKey key = this.getKey(md5sum);
		
		if (key == null) {
			return false;
		}
		
		return this.containsKey(key);
	}
	
	public synchronized boolean containsKey(int index) {
		DownloadKey key = this.getKey(index);
		
		if (key == null) {
			return false;
		}
		
		return this.containsKey(key);
	}
	
	public synchronized void move(int oldIndex, int newIndex) {
		if (oldIndex == newIndex)
			return;
		
		boolean moveUp = oldIndex > newIndex? false:true;
		
		DownloadKey key = this.getKey(oldIndex);
		
		if (moveUp) {
			for (int i = oldIndex + 1; i <= newIndex; i++) {			
				this.changeIndex(i, i - 1);
			}
		}
		else {
			for (int i = oldIndex - 1; i >= newIndex; i--) {			
				this.changeIndex(i, i + 1);
			}
		}
		
		key.index = newIndex;
	}
	
	public JsonArray serializeToJsonArray() {
		JsonArray objects = new JsonArray();
		
		for (int i = 0; i < this.size(); i++) {
			objects.add(this.get(i).serializeToJson());
		}
		
		return objects;
	}
	
	public void addPackagesFromJson(JsonArray objects) {
		if (objects != null) {
			for (int i = 0; i < objects.size(); i++) {
				this.put(new DownloadPackage(objects.get(i).getAsJsonObject()));
			}
		}
	}
	
	public static DownloadMap getDownloadMapFromJson(JsonArray objects) {
		DownloadMap map = new DownloadMap();
		
		map.addPackagesFromJson(objects);
		
		return map;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (dest != null) {
			for (int i = 0; i < this.size(); i++) {
				dest.writeParcelable(this.get(i), flags);
			}
		}
	}
	
	public static final Parcelable.Creator<DownloadMap> CREATOR = new Parcelable.Creator<DownloadMap>() {
		
		@Override
		public DownloadMap[] newArray(int size) {
			return new DownloadMap[size];
		}
		
		@Override
		public DownloadMap createFromParcel(Parcel source) {
			return new DownloadMap(source);
		}
	};
	
	public void deserializeMapFromCache(final Context context) {
		this.deserializeMapFromCache(context, null);
	}
	
	public boolean checkIfCacheFileExists(final Context context, String filename) {
		String[] files = context.fileList();
		
		boolean exists = false;
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(filename)) {
				exists = true;
				break;
			}
		}
		
		return exists;
	}
	
	public void deserializeMapFromCache(final Context context, final IDeserializeMapFinishedCallback callback) {
		boolean exists = this.checkIfCacheFileExists(context, context.getString(R.string.downloads_cache_filename));
		
		if (exists) {
			this.deserializeMapFromFile(context, context.getString(R.string.downloads_cache_filename), true, callback);
		}
		else {
			Log.i(TAG, "cache file didn't exist");
		}
	}
	
	public void deserializeMapFromStorage(final Context context, final String filePath) {
		this.deserializeMapFromFile(context, filePath, false, null);
	}
	
	public void deserializeMapFromStorage(final Context context, final String filePath, final IDeserializeMapFinishedCallback callback) {
		this.deserializeMapFromFile(context, filePath, false, callback);
	}
	
	public void deserializeMapFromFile(final Context context, final String filename, final boolean cache, final IDeserializeMapFinishedCallback callback) {
		Handler handler = new Handler();
		
		final DownloadMap map = this;
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				BufferedReader stream = null;
				
				try {
					
					if (cache) {
						stream = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
					}
					else {
						stream = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
					}
					
					StringBuilder builder = new StringBuilder();
					String line = "";
			
					while ((line = stream.readLine()) != null) 
					{
						builder.append(line);
					}
					
			        stream.close();
			        
			        JsonParser parser = new JsonParser();
			        
			        JsonArray elements = parser.parse(builder.toString()).getAsJsonArray();
			        
			        map.addPackagesFromJson(elements);
			        
			        if (cache) {
			        	context.deleteFile(filename);
			        }
			        
			        if (callback != null) {
			        	callback.mapDeserializedCallback();
			        }
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void serializeMapToCache(final Context context) {
		this.serializeMapToFile(context, context.getString(R.string.downloads_cache_filename), true);
	}
	
	public void serializeMapToStorage(final Context context, final String filePath) {
		this.serializeMapToFile(context, filePath, false);
	}
	
	public void serializeMapToFile(final Context context, final String filename, final boolean cache) {
		Handler handler = new Handler();
		
		final DownloadMap map = this;
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				JsonArray jsonMap = map.serializeToJsonArray();
				
				try {
					BufferedOutputStream stream = null;
					
					if (cache) {
						stream = new BufferedOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE));
					}
					else {
						stream = new BufferedOutputStream(new FileOutputStream(filename));
					}
					
					stream.write(jsonMap.toString().getBytes());
					
					stream.flush();
					
					if (stream != null)
						stream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
