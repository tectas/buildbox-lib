package at.tectas.buildbox.content.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.os.Bundle;
import at.tectas.buildbox.msteam.R;
import at.tectas.buildbox.content.items.properties.ItemTypes;

public class ChildItem extends ParentItem {
	public String detailUrl;
	public String version = null;
	
	public ChildItem(JsonObject json) throws NullPointerException {
		this(null, json);
	}
	
	public ChildItem(Item parent, JsonObject json) throws NullPointerException {
		super(parent, json);
		
		JsonElement detail = json.get(Item.context.getString(R.string.detail_property));
		
		JsonObject children = null;
		
		if (detail.isJsonPrimitive())
			this.detailUrl = detail.getAsString();
		else if (detail.isJsonObject())
			children = detail.getAsJsonObject();
		
		this.version = Item.helper.tryGetStringFromJson(Item.context.getString(R.string.version_property), json);
		
		if (children != null) {
			Item child = Item.parser.parseJsonToItem(this, children);
			
			if (child != null)
				this.childs.add(child);
		}
		
		this.type = ItemTypes.ChildItem;
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.context.getString(R.string.item_type_property), Item.context.getString(R.string.item_child_type));
		
		result.putString(Item.context.getString(R.string.detailurl_property), this.detailUrl);
		
		return result;
	}
}
