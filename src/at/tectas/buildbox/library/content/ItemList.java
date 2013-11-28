package at.tectas.buildbox.library.content;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.util.Log;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.ParentItem;

public class ItemList extends ArrayList<Item> {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "ItemList";

	public ItemList() {

	}

	public ItemList(ArrayList<Item> itemList) {
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

		for (Item item : this) {
			if (item.ID != null && item.ID.equals(key)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean contains(String title) {
		boolean result = false;

		for (Item item : this) {
			if (item.title != null && item.title.equalsIgnoreCase(title)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public int containsAtIndex(Item item) {
		for (int i = 0; i < this.size(); i++) {
			boolean result = false;
			Item listItem = this.get(i);
			if (item.equals(listItem)) {
				return i;
			} else {
				if (listItem instanceof ParentItem) {
					if (listItem.getChildren() != null) {
						result = this.contains(listItem.getChildren(), item);
					}
				}
			}
			if (result) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean contains(Item item) {
		return this.contains(this, item);
	}

	public boolean contains(ArrayList<Item> list, Item item) {
		for (Item listItem : list) {
			boolean result = false;
			if (item.equals(listItem)) {
				return true;
			} else {
				if (listItem instanceof ParentItem) {
					if (listItem.getChildren() != null) {
						result = this.contains(listItem.getChildren(), item);
					}
				}
			}
			if (result) {
				Log.e(TAG, listItem.ID.toString());
				Log.e(TAG, item.ID.toString());
				return true;
			}
		}
		return false;
	}

	public Item get(Object key) {
		for (Item item : this) {
			if (item.ID != null && item.ID.equals(key)) {
				return item;
			}
		}
		return null;
	}

	public Item get(Integer index) {
		return this.get((int) index);
	}

	public Item get(UUID id) {
		for (Item item : this) {
			if (item.ID != null && item.ID.equals(id)) {
				return item;
			}
		}
		return null;
	}

	public Item get(String title) {
		for (Item item : this) {
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
		for (Item item : this) {
			if (item.ID != null && item.ID.equals(key)) {
				this.remove(item);
				return true;
			}
		}
		return false;
	}

	public boolean remove(String title) {
		for (Item item : this) {
			if (item.title != null && item.title.equalsIgnoreCase(title)) {
				this.remove(item);
				return true;
			}
		}
		return false;
	}

	public Bundle getBundle() {
		Bundle result = new Bundle();

		for (Item item : this) {
			result.putBundle(item.ID.toString(), item.parseItemToBundle());
		}

		return result;
	}

	public void add(ItemList list) {
		if (list != null) {
			for (Item item : list) {
				this.add(item);
			}
		}
	}
}
