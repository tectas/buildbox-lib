package at.tectas.buildbox.library.communication.handler.interfaces;

import at.tectas.buildbox.library.communication.DownloadMap;

public abstract class IMapSortingHandler {
	protected DownloadMap map = null;
	
	public void setMap(DownloadMap map) {
		this.map = map;
	}
	
	public DownloadMap getMap() {
		return this.map;
	}
	
	public abstract void sortMap();
}
