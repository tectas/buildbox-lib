package at.tectas.buildbox.communication;

import java.util.Hashtable;

import com.google.gson.JsonArray;


public class DownloadMap extends Hashtable<DownloadKey, DownloadPackage> {

	private static final long serialVersionUID = 1L;

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
	
	private synchronized void ChangeIndex (int oldIndex, int newIndex) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == oldIndex) {
				key.index = newIndex;
				
				break;
			}
		}
	}
	
	public synchronized DownloadPackage put (String md5sum, DownloadPackage object) {
		if (md5sum != null) {
			DownloadKey oldKey = this.getKey(md5sum);
			
			if (oldKey != null) {
				this.remove(oldKey);
				
				for (int i = oldKey.index; i < this.size(); i++) {
					this.ChangeIndex(i, i - 1);
				}
			}
			
			DownloadKey key = new DownloadKey();
			key.md5sum = md5sum;
			key.index = this.size();
			
			return this.put(key, object);
		}
		else {
			return null;
		}
	}
	
	public synchronized DownloadPackage put (DownloadPackage value) {
		return this.put(value.md5sum == null || value.md5sum.isEmpty()? value.url: value.md5sum, value);
	}
	
	public synchronized boolean insert(int index, String md5sum, DownloadPackage object) {
		if (md5sum != null) {
			if (index > this.size()) {
				return false;
			}
			else {
				DownloadKey newKey = new DownloadKey();
				newKey.index = index;
				newKey.md5sum = md5sum;
				
				for (int i = index; i < this.size(); i++) {
					this.ChangeIndex(index, index + 1);
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
	
	public JsonArray serializeToJsonArray() {
		JsonArray objects = new JsonArray();
		
		for (int i = 0; i < this.size(); i++) {
			objects.add(this.get(i).serializeToJson());
		}
		
		return objects;
	}
	
	public static DownloadMap getDownloadMapFromJson(JsonArray objects) {
		DownloadMap map = new DownloadMap();
		
		for (int i = 0; i < objects.size(); i++) {
			map.put(new DownloadPackage(objects.get(i).getAsJsonObject()));
		}
		
		return map;
	}
}
