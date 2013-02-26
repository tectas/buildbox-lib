package at.tectas.buildbox.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import at.tectas.buildbox.content.ChildItem;
import at.tectas.buildbox.content.DetailItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.content.ItemList;
import at.tectas.buildbox.content.ParentItem;

public class JsonItemParser {
	public static final String TAG = "JsonParser";
	
	public static ItemList parseJson (JsonArray json) {
		ItemList items = new ItemList();
		
		for (int i = 0; i < json.size(); i++) {
			items.add(JsonItemParser.parseJsonToItem(json.get(i).getAsJsonObject()));
		}
		
		return items;
	}
	
	public static  Item parseJsonToItem(JsonObject json) {	
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
	
	public static  Item parseJsonToItem(Item parent, JsonObject json) {	
		if (json != null) {
			if (json.has("detail")) {
				return new ChildItem(parent, json);
			}
			else if (json.has("children")) {
				return new ParentItem(parent, json);
			}
			else {
				return new DetailItem(parent, json);
			}
		}
		return null;
	}
}
