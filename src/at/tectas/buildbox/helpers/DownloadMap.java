package at.tectas.buildbox.helpers;

import java.util.Hashtable;

public class DownloadMap extends Hashtable<DownloadKey, DownloadPackage> {

	private static final long serialVersionUID = 1L;

	public DownloadPackage get (String md5sum) {
		for (DownloadKey key: this.keySet()) {
			if (key.md5sum == md5sum) {
				return this.get(key);
			}
		}
		
		return null;
	}
	
	public DownloadPackage get (int index) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == index) {
				return this.get(key);
			}
		}
		
		return null;
	}
	
	public DownloadKey getKey(int index) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == index) {
				return key;
			}
		}
		return null;
	}
	
	public DownloadKey getKey(String md5sum) {
		for (DownloadKey key: this.keySet()) {
			if (key.md5sum == md5sum) {
				return key;
			}
		}
		return null;
	}
	
	private void ChangeIndex (int oldIndex, int newIndex) {
		for (DownloadKey key: this.keySet()) {
			if (key.index == oldIndex) {
				key.index = newIndex;
				
				break;
			}
		}
	}
	
	public DownloadPackage put (String md5sum, DownloadPackage object) {
		DownloadKey key = new DownloadKey();
		key.index = this.size();
		key.md5sum = md5sum;
		
		return this.put(key, object);
	}
	
	public boolean insert(int index, String md5sum, DownloadPackage object) {
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
	
	public DownloadPackage remove(int index) {
		DownloadKey key = this.getKey(index);
		
		return this.remove(key);
	}
	
	public DownloadPackage remove(String md5sum) {
		DownloadKey key = this.getKey(md5sum);
		
		return this.remove(key);
	}
	
	public boolean containsKey(String md5sum) {
		DownloadKey key = this.getKey(md5sum);
		
		if (key == null) {
			return false;
		}
		
		return this.containsKey(key);
	}
	
	public boolean containsKey(int index) {
		DownloadKey key = this.getKey(index);
		
		if (key == null) {
			return false;
		}
		
		return this.containsKey(key);
	}
}
