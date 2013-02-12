package at.tectas.buildbox.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;

public class ItemList implements Map<ItemListKey, Item>  {
	
	private Map<ItemListKey, Item> items = new HashMap<ItemListKey, Item>();
	
	public ItemList () {
		
	}
	
	public ItemList (ArrayList<Item> itemList) {
		for (Item item : itemList) {
			this.items.put(new ItemListKey(item.ID, item.title), item);
		}
	}
	
	public ItemList (Map<ItemListKey, Item> itemMap) {
		this.items = itemMap;
	}
	
	@Override
	public void clear() {
		this.items.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.items.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.items.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<ItemListKey, Item>> entrySet() {
		return this.items.entrySet();
	}

	@Override
	public Item get(Object key) {
		return this.items.get(key);
	}

	public Item get(UUID id) {
		for (ItemListKey key: this.items.keySet()) {
			if (key.getID() == id) {
				return this.items.get(key);
			}
		}
		return null;
	}
	
	public Item get (String title) {
		for (ItemListKey key: this.items.keySet()) {
			if (key.getTitle() == title) {
				return this.items.get(key);
			}
		}
		return null;
	}
	
	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	@Override
	public Set<ItemListKey> keySet() {
		return this.items.keySet();
	}
	
	public Item put(Item item) {
		return this.items.put(new ItemListKey(item.ID, item.title), item);
	}
	
	@Override
	public Item put(ItemListKey key, Item value) {
		return this.items.put(key, value);
	}

	@Override
	public void putAll(Map<? extends ItemListKey, ? extends Item> arg0) {
		this.items.putAll(arg0);
	}

	@Override
	public Item remove(Object key) {
		return this.items.remove(key);
	}

	@Override
	public int size() {
		return this.items.size();
	}

	@Override
	public Collection<Item> values() {
		return this.items.values();
	}
	
	public Bundle getBundle() {
		Bundle result = new Bundle();
		
		for (ItemListKey itemKey: this.items.keySet()) {
			result.putBundle(itemKey.getID().toString(), this.items.get(itemKey).parseItemToBundle());
		}
		
		return result;
	}
}
