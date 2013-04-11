package at.tectas.buildbox.library.communication;

import android.os.Parcel;
import at.tectas.buildbox.library.communication.handler.interfaces.IMapSortingHandler;

public class SortedDownloadMap extends DownloadMap {

	private static final long serialVersionUID = 1L;
	
	public SortedDownloadMap(Parcel source, IMapSortingHandler handler) {
		super(source);
		
		this.sorter = handler;
		
		this.sorter.setMap(this);
		
		this.sort();
	}
	
	public SortedDownloadMap(Parcel source) {
		this(source, null);
	}
	
	public SortedDownloadMap(IMapSortingHandler handler) {
		this((Parcel)null, handler);
	}
	
	public SortedDownloadMap() {
		this((Parcel)null, null);
	}
	
	@Override
	public synchronized DownloadPackage put(String md5sum, DownloadPackage object) {
		DownloadPackage pack = super.put(md5sum, object);
		
		this.sort();
		
		return pack;
	}
	
	@Override
	public synchronized boolean insert(int index, DownloadPackage object) {
		boolean inserted = super.insert(index, object); 
		
		if (inserted) {
			this.sort();
		}
		
		return inserted;
	}
	
	@Override
	public synchronized DownloadPackage remove(Object key) {
		DownloadPackage pack = super.remove(key);
		
		if (key != null) {
			this.sort();
		}
		
		return pack;
	}
	
	@Override
	public synchronized void move(int oldIndex, int newIndex) {
		this.move(oldIndex, newIndex, true);
	}
	
	public synchronized void move(int oldIndex, int newIndex, boolean sort) {
		super.move(oldIndex, newIndex);
		
		if (sort)
			this.sort();
	}
}
