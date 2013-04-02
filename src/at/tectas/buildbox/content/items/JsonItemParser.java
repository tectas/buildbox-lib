package at.tectas.buildbox.content.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import at.tectas.buildbox.R;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.helpers.JsonHelper;

@SuppressLint("DefaultLocale")
public class JsonItemParser {
	public static final String TAG = "JsonParser";
	public Context context = null;
	public String deviceModel = null;	
	protected JsonHelper helper = new JsonHelper();
	
	public JsonItemParser(Context context, String deviceModel) {
		this.context = context;
		this.deviceModel = deviceModel;
	}
	
	public ItemList parseJson (JsonArray json) {
		ItemList items = new ItemList();
		
		for (int i = 0; i < json.size(); i++) {
			Item item = this.parseJsonToItem(json.get(i).getAsJsonObject());
			
			if (item != null) {
				items.add(item);
			}
		}
		
		return items;
	}
	
	public  Item parseJsonToItem(JsonObject json) {	
		return this.parseJsonToItem(null, json);
	}
	
	public Item parseJsonToItem(Item parent, JsonObject json) {
		if (context == null) {
			Log.e(TAG, "no context set");
			return null;
		}
		
		if (Item.getActivity() == null) {
			Item.setActivity(context);
		}
		
		if (Item.parser == null) {
			Item.parser = this;
		}
		
		if (json != null) {
			if (json.has(context.getString(R.string.item_device_type))) {
				String device = helper.tryGetStringFromJson(context.getString(R.string.item_device_type), json);
				
				if (device != null && this.deviceModel != null && !this.deviceModel.toLowerCase().trim().equals(device.toLowerCase().trim())) {
					return null;
				}
			}

			if (json.has(context.getString(R.string.item_detail_type))) {
				return new ChildItem(parent, json);
			}
			else if (json.has(context.getString(R.string.item_child_type))) {
				return new ParentItem(parent, json);
			}
			else {
				return new DetailItem(parent, json);
			}
		}
		return null;
	}
}
