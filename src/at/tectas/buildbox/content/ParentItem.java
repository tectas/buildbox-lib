package at.tectas.buildbox.content;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.os.Bundle;
import at.tectas.buildbox.R;
import at.tectas.buildbox.helpers.JsonItemParser;

public class ParentItem extends Item {
	public String thumbnailUrl = null;
	public ItemList childs = new ItemList();
	
	public ParentItem(JsonObject json) {
		this(null, json);
	}
	
	public ParentItem(Item parent, JsonObject json) {
		super(parent, json);
		
		this.thumbnailUrl = Item.helper.tryGetStringFromJson(Item.context.getString(R.string.thumbnailurl_property), json);
		
		JsonArray children = Item.helper.tryGetJsonArrayFromJson(Item.context.getString(R.string.children_property), json);
		
		if (children != null)
			for (int i = 0; i < children.size(); i++) {	
				JsonElement element = children.get(i);
				
				if (element.isJsonObject()) {
					JsonObject object = element.getAsJsonObject();
					
					this.childs.add(JsonItemParser.parseJsonToItem(this, object));
				}
			}
		
		this.type = ItemTypes.ParentItem;
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.context.getString(R.string.item_type_property), Item.context.getString(R.string.item_parent_type));
		
		result.putString(Item.context.getString(R.string.thumbnailurl_property), this.thumbnailUrl);
		
		ArrayList<String> children = new ArrayList<String>();
		
		for (int i = 0; i < this.childs.size(); i++) {
			children.add(this.childs.get(i).ID.toString());
		}
		
		result.putStringArrayList(Item.context.getString(R.string.children_property), children);
		
		return result;
	}
}
