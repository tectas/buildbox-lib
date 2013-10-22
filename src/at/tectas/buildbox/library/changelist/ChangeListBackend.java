package at.tectas.buildbox.library.changelist;

import java.util.HashMap;

import at.tectas.buildbox.library.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import at.tectas.buildbox.library.content.ItemList;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.helpers.PropertyHelper;
import at.tectas.buildbox.library.helpers.SqlQueryHelper;

public class ChangeListBackend extends SQLiteOpenHelper { 
	
	protected Context context = null;
	
	protected static ChangeListBackend currentInstance = null;
	
	public static ChangeListBackend getInstance(Context context, String dbName, int version) {
		if (currentInstance == null) {
			currentInstance = new ChangeListBackend(context, dbName, version);
		}
		
		return currentInstance;
	}
	
	protected ChangeListBackend(Context context, String name, int version) {
		super(context, name, null, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		HashMap<String, String> fields = new HashMap<String, String>();
		
		fields.put("id", "INTEGER PRIMARY KEY");
		fields.put(context.getString(R.string.changelist_title_field_name), "TEXT");
		fields.put(context.getString(R.string.changelist_version_field_name), "TEXT DEFAULT NULL");
		fields.put(context.getString(R.string.changelist_url_field_name), "TEXT DEFAULT NULL");
		
		db.execSQL(SqlQueryHelper.getCreateTableQuery(context.getString(R.string.changelist_item_table_name), fields));
		
		db.execSQL(SqlQueryHelper.getCreateIndexQuery(context.getString(R.string.changelist_item_table_name), context.getString(R.string.changelist_title_field_name)));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void insertItem(DetailItem item) {
		if (item != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			
			values.put(context.getString(R.string.changelist_title_field_name), item.title);
			
			if (!PropertyHelper.stringIsNullOrEmpty(item.version)) {
				values.put(context.getString(R.string.changelist_version_field_name), item.version);
			}
			
			if (!PropertyHelper.stringIsNullOrEmpty(item.url)) {
				values.put(context.getString(R.string.changelist_url_field_name), item.url);
			}
			
			db.insert(context.getString(R.string.changelist_item_table_name), null, values);
		}
	}
	
	public ItemList getItems(String filterField, String filterValue) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] fields = new String[3];
		
		fields[0] = context.getString(R.string.changelist_title_field_name);
		fields[1] = context.getString(R.string.changelist_version_field_name);
		fields[2] = context.getString(R.string.changelist_url_field_name);
		
		Cursor cursor = null;
		
		if (!PropertyHelper.stringIsNullOrEmpty(filterField)) {
			cursor = db.query(
				context.getString(R.string.changelist_item_table_name), 
				fields, 
				filterField + "=?", 
				new String[] { filterValue }, 
				null, 
				null, 
				null
			);
 		}
		else {
			cursor = db.query(
				context.getString(R.string.changelist_item_table_name), 
				fields, 
				null, 
				null, 
				null, 
				null, 
				null
			);
		}

		ItemList list = new ItemList();
		
		if (cursor.moveToFirst()) {
            do {
            	String dbTitle = cursor.getString(0);
    			String dbVersion = cursor.getString(1);
    			String dbUrl = cursor.getString(2);
    			
    			list.add(new DetailItem(dbTitle, dbVersion, dbUrl));
            } while (cursor.moveToNext());
        }
		
		if (cursor != null) {
			cursor.close();
		}
		
		return list;
	}
	
	public ItemList getItems() {
		return this.getItems(null, null);
	}
	
	public DetailItem getItem(String title) {
		if (!PropertyHelper.stringIsNullOrEmpty(title)) {
			ItemList list = this.getItems(context.getString(R.string.changelist_title_field_name), title);
			
			if (list.size() > 0) {
				Item item = list.get(0);
				
				if (item instanceof DetailItem) {
					return (DetailItem)item;
				}
			}
 		}
		
		return null;
	}
	
	public int updateItem(DetailItem item) {
		if (item != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			
			values.put(context.getString(R.string.changelist_title_field_name), item.title);
			values.put(context.getString(R.string.changelist_version_field_name), item.version);
			values.put(context.getString(R.string.changelist_url_field_name), item.url);
			
			return db.update(
				context.getString(R.string.changelist_item_table_name), 
				values, 
				context.getString(R.string.changelist_title_field_name) + "=?", 
				new String[] { item.title }
			);
		}
		
		return 0;
	}
	
	public int removeItem(DetailItem item) {
		if (item != null) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			return db.delete(
				context.getString(R.string.changelist_item_table_name), 
				context.getString(R.string.changelist_title_field_name) + "=?", 
				new String[] { item.title }
			);
		}
		return 0;
	}
	
	public int getItemCount(String filterField, String filterValue) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] fields = new String[3];
		
		fields[0] = context.getString(R.string.changelist_title_field_name);
		fields[1] = context.getString(R.string.changelist_version_field_name);
		fields[2] = context.getString(R.string.changelist_url_field_name);
		
		Cursor cursor = null;
		
		if (!PropertyHelper.stringIsNullOrEmpty(filterField)) {
			cursor = db.query(
				context.getString(R.string.changelist_item_table_name), 
				fields, 
				filterField + "=?", 
				new String[] { filterValue }, 
				null, 
				null, 
				null
			);
 		}
		else {
			cursor = db.query(
				context.getString(R.string.changelist_item_table_name), 
				fields, 
				null, 
				null, 
				null, 
				null, 
				null
			);
		}
		
		int count = 0;
		
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		
		return count;
	}
	
	public int getItemCount() {
		return this.getItemCount(null, null);
	}
	
	public boolean containsItem(DetailItem item) {
		if (item != null) {	 		
			int count = this.getItemCount(context.getString(R.string.changelist_title_field_name), item.title);
			
			if (count > 0) {
				return true;
			}
		}
		return false;
	}
	
	public void closeDb() {
		this.close();
	}
}
