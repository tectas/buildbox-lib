package at.tectas.buildbox.content;

import java.util.UUID;

public class ItemListKey {
	private UUID id;
	private String title;
	
	public ItemListKey (UUID id, String title) { 
		this.id = id;
		this.title = title;
	}
	
	public UUID getID() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
}
