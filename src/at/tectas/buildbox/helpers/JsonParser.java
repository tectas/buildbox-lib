package at.tectas.buildbox.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.tectas.buildbox.content.ChildItem;
import at.tectas.buildbox.content.DetailItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.content.ParentItem;

public class JsonParser {
	public static final String TAG = "JsonParser";
	
	public static ItemList parseJson (JSONArray json) throws JSONException {
		ItemList items = new ItemList();
		
		for (int i = 0; i < json.length(); i++) {
			items.add(JsonParser.parseJsonToItem(json.optJSONObject(i)));
		}
		
		return items;
	}
	
	public static  Item parseJsonToItem(JSONObject json) throws JSONException {	
		if (json != null) {
			if (json.has("detail") || json.has("detailUrl")) {
				return new ChildItem(json);
			}
			else if (json.has("url")) {
				return new DetailItem(json);
			}
			else {
				return new ParentItem(json);
			}
		}
		return null;
	}
}
