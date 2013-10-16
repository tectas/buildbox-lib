package at.tectas.buildbox.library.changelist;

import java.util.HashMap;

import android.content.Context;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.content.ItemList;
import at.tectas.buildbox.library.content.items.ChildItem;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.ParentItem;
import at.tectas.buildbox.library.helpers.PropertyHelper;

public class ChangeList extends Thread {
	
	protected Context context = null;
	protected ChangeListBackend changelist = null;
	protected ItemList newList = null;
	protected IChangeListBuiltCallback callback = null;
	public HashMap<ChangeType, String> changes = new HashMap<ChangeType, String>();

	public ChangeList (Context context, ItemList newList, IChangeListBuiltCallback callback) {
		this.context = context;
		this.newList = newList;
		this.callback = callback;
		this.changelist = 
			ChangeListBackend.getInstance(
				this.context, 
				this.context.getString(R.string.changelist_database_name), 
				this.context.getResources().getInteger(R.integer.changelist_database_version)
			);
	}
	
	public ItemList getDetailList(ItemList fullList) {
		ItemList detailList = new ItemList();
		
		if (fullList != null) {
			for (Item item: fullList) {
				if (item instanceof ParentItem || item instanceof ChildItem) {
					ParentItem parentItem = (ParentItem) item;
					
					if (parentItem.childs != null) {
						ItemList detailListPart = this.getDetailList(parentItem.childs);
						detailList.add(detailListPart);
					}
				}
				else if (item instanceof DetailItem) {
					detailList.add(item);
				}
			}
		}
		
		return detailList;
	}
	
	@Override
	public void run() {
		ItemList detailList = this.getDetailList(this.newList);
		
		for (Item item: detailList) {
			if (item instanceof DetailItem && !PropertyHelper.stringIsNullOrEmpty(item.title)) {
				DetailItem newItem = (DetailItem) item;
				DetailItem dbItem = this.changelist.getItem(item.title);
				
				if (dbItem == null) {
					this.addItemToChangeList(ChangeType.added, newItem);
						
					this.changelist.insertItem(newItem);
				}
				else {
					int versionComparsion = PropertyHelper.compareVersions(dbItem.version, newItem.version);
					
					if (versionComparsion >= 1) {
						this.addItemToChangeList(ChangeType.updated, newItem);
						this.changelist.updateItem(newItem);
					}
					else if (versionComparsion <= -1) {
						this.addItemToChangeList(ChangeType.downgraded, newItem);
						this.changelist.updateItem(newItem);
					}
				}
			}
		}
		
		ItemList dbList = this.changelist.getItems();
		
		for (Item item: dbList) {
			if (!detailList.contains(item.title)) {
				if (item instanceof DetailItem) {
					this.addItemToChangeList(ChangeType.removed, item);
					this.changelist.removeItem((DetailItem)item);
				}
			}
		}
		
		if (this.callback != null) {
			callback.notifyBuilt(this.changes);
		}
	}
	
	public void addItemToChangeList(ChangeType type, Item item) {
		if (!changes.containsKey(type))
			changes.put(type, item.title);
		else {
			String change = changes.get(type);
			
			changes.remove(type);
			
			change = change + "\n" + item.title;
			
			changes.put(type, change);
		}
	}
}
