package at.tectas.buildbox.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.JsonParser;

public class ParentItem extends Item {
	public String thumbnailUrl;
	public ItemList childs = new ItemList();
	
	public ParentItem(JSONObject json) throws JSONException {
		super(json);
		this.thumbnailUrl = json.optString(Item.activity.getString(R.string.thumbnailurl_property));
		
		JSONArray children = json.optJSONArray(Item.activity.getString(R.string.children_property));
		
		if (children != null)
			for (int i = 0; i < children.length(); i++) {
				JSONObject object = children.optJSONObject(i);
				
				this.childs.put(JsonParser.parseJsonToItem(object));
			}
		
		this.type = ItemTypes.ParentItem;
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.activity.getString(R.string.item_type_property), Item.activity.getString(R.string.item_parent_type));
		
		result.putString(Item.activity.getString(R.string.thumbnailurl_property), this.thumbnailUrl);
		
		ArrayList<String> children = new ArrayList<String>();
		
		for (int i = 0; i < this.childs.size(); i++) {
			children.add(this.childs.get(i).ID.toString());
		}
		
		result.putStringArrayList(Item.activity.getString(R.string.children_property), children);
		
		return result;
	}
}
