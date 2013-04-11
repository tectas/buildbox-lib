package at.tectas.buildbox.library.communication.handler;

import at.tectas.buildbox.library.communication.DownloadKey;
import at.tectas.buildbox.library.communication.SortedDownloadMap;
import at.tectas.buildbox.library.communication.handler.interfaces.IMapSortingHandler;
import at.tectas.buildbox.library.content.items.properties.DownloadType;

public class MapTypeSortingHandler extends IMapSortingHandler {

	@Override
	public void sortMap() {
		int currentIndex = 0;
		
		for (DownloadType type: DownloadType.values()) {
			for (int i = currentIndex; i < this.map.size(); i++) {
				DownloadKey key = this.map.getKey(i, type);
				
				if (key == null) {
					break;
				}
				else {
					if (this.map instanceof SortedDownloadMap) {
						((SortedDownloadMap)map).move(key.index, currentIndex, false);
					}
					else {
						this.map.move(key.index, currentIndex);
					}
					
					currentIndex++;
				}
			}
		}
	}
}
