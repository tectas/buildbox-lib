package at.tectas.buildbox.content;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;

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
			if (item.ID == key) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean contains(String key) {
		boolean result = false;
		
		for (Item item: this) {
			if (item.ID.toString() == key) {
				result = true;
				break;
			}
		}
		return result;
	}

	public Item get(Object key) {
		for (Item item: this) {
			if (item.ID == key) {
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
			if (item.ID == id) {
				return item;
			}
		}
		return null;
	}
	
	public Item get (String title) {
		for (Item item: this) {
			if (item.title == title) {
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
			if (item.ID == key) {
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
}
