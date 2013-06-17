package at.tectas.buildbox.library.content;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import at.tectas.buildbox.library.content.items.Item;

public class ItemList extends ArrayList<Item>  {
	
	private static final long serialVersionUID = 1L;
	
	public ItemList () {
		
	}
	
	public ItemList (ArrayList<Item> itemList) {
		for (Item item : itemList) {
			this.add(item);
		}
	}
	
	@Override
	public void clear() {
		this.clear();
	}

	public boolean contains(UUID key) {
		boolean result = false;
		
		for (Item item: this) {
			if (item.ID != null && item.ID.equals(key)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean contains(String title) {
		boolean result = false;
		
		for (Item item: this) {
			if (item.title != null && item.title.equalsIgnoreCase(title)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public Item get(Object key) {
		for (Item item: this) {
			if (item.ID != null && item.ID.equals(key)) {
				return item;
			}
		}
		return null;
	}
	
	public Item get(Integer index) {
		return this.get((int)index);
	}
	
	public Item get(UUID id) {
		for (Item item: this) {
			if (item.ID != null && item.ID.equals(id)) {
				return item;
			}
		}
		return null;
	}
	
	public Item get (String title) {
		for (Item item: this) {
			if (item.title != null && item.title.equalsIgnoreCase(title)) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public boolean isEmpty() {
		return this.isEmpty();
	}

	public boolean remove(UUID key) {
		for (Item item: this) {
			if (item.ID != null && item.ID.equals(key)) {
				this.remove(item);
				return true;
			}
		}
		return false;
	}
	
	public boolean remove(String title) {
		for (Item item: this) {
			if (item.title != null && item.title.equalsIgnoreCase(title)) {
				this.remove(item);
				return true;
			}
		}
		return false;
	}
	
	public Bundle getBundle() {
		Bundle result = new Bundle();
		
		for (Item item: this) {
			result.putBundle(item.ID.toString(), item.parseItemToBundle());
		}
		
		return result;
	}
	
	public void add(ItemList list) {
		if (list != null) {
			for (Item item: list) {
				this.add(item);
			}
		}
	}
}
