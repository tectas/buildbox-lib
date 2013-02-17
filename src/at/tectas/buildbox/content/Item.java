package at.tectas.buildbox.content;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import at.tectas.buildbox.R;

public class Item {
	public enum ArrayTypes {
		DEPENDENCIES, DEVELOPERS, HOMEPAGES, IMAGEURLS, DONATIONLINKS, CHANGELOG
	}
	
	public enum ItemTypes {
		ParentItem, ChildItem, DetailItem
	}
	
	public static final String TAG = "ITEM";
	protected static Activity activity;
	
	public Item parent = null;
	public ItemTypes type = null;
	public final UUID ID = UUID.randomUUID();
	public String title;
	public ArrayList<String> dependencies = new ArrayList<String>();
	
	public Item (JSONObject json) throws JSONException {
		this(null, json);
	}
	
	public Item (Item parent, JSONObject json) throws JSONException {
		this.parent = parent;
		
		this.title = json.optString(Item.activity.getString(R.string.title_property));
		
		this.parseJSONArray(json.optJSONArray(Item.activity.getString(R.string.dependencies_property)), ArrayTypes.DEPENDENCIES);
	}
	
	public static void setActivity(Activity activity_) {
		Item.activity = activity_;
	}
	
	public Bundle parseItemToBundle() {
		Bundle result = new Bundle();
		
		result.putString(Item.activity.getString(R.string.id_property), this.ID.toString());
		
		result.putString(Item.activity.getString(R.string.title_property), this.title);
		
		result.putStringArrayList(Item.activity.getString(R.string.dependencies_property), this.dependencies);
		
		return result;
	};
	
	protected void parseJSONArray (JSONArray json, ArrayTypes detail) throws JSONException {
		Object dummyObject = null;
		
		if (detail == ArrayTypes.DEPENDENCIES) {
			dummyObject = this.dependencies;
		}
		else
		{
			DetailItem item = (DetailItem) this;
			
			if (detail == ArrayTypes.DEVELOPERS) {
				dummyObject = item.developers;
			}
			else if (detail == ArrayTypes.HOMEPAGES) {
				dummyObject = item.homePages;
			}
			else if (detail == ArrayTypes.IMAGEURLS) {
				dummyObject = item.imageUrls;
			}
			else if (detail == ArrayTypes.CHANGELOG) {
				dummyObject = item.changelog;
			}
		}
		if (json != null && dummyObject != null) {
			if (detail != ArrayTypes.DEVELOPERS) {
				@SuppressWarnings("unchecked")
				ArrayList<String> list = (ArrayList<String>) dummyObject;
				
				for (int i = 0; i < json.length(); i++) {
					String object = json.optString(i);
					
					if (object != null)
						list.add(object);
				}
			}
			else {
				@SuppressWarnings("unchecked")
				ArrayList<Developer> list = (ArrayList<Developer>) dummyObject;
				
				for (int i = 0; i < json.length(); i++) {
					Developer object = null;
					
					try {
						object = new Developer(json.optJSONObject(i));
					}
					catch (JSONException e) {
						Log.e(Item.TAG, "Couldn't parse developer: " + e.getMessage());
					}
					
					if (object != null) {
						list.add(object);
					}
				}
			}
			
			//Log.e(Item.TAG, ((Boolean)(((DetailItem)(this)).homePages.size() == 0)).toString());
		}
	}
}
