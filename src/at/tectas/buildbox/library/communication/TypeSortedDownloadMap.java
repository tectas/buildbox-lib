package at.tectas.buildbox.library.communication;

import android.os.Parcel;
import at.tectas.buildbox.library.communication.handler.MapTypeSortingHandler;

public class TypeSortedDownloadMap extends SortedDownloadMap {

	private static final long serialVersionUID = 1L;
	
	public TypeSortedDownloadMap(Parcel source) {
		super(source, new MapTypeSortingHandler());
	}
	
	public TypeSortedDownloadMap() {
		super((Parcel)null, new MapTypeSortingHandler());
	}
}
