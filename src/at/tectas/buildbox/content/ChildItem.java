package at.tectas.buildbox.content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import at.tectas.buildbox.R;

public class ChildItem extends ParentItem {
	public String detailUrl;
	
	public ChildItem(JSONObject json) throws JSONException, NullPointerException {
		this(null, json);
	}
	
	public ChildItem(Item parent, JSONObject json) throws JSONException, NullPointerException {
		super(parent, json);
		
		this.detailUrl = json.optString(Item.activity.getString(R.string.detailurl_property));
		
		JSONArray children = json.optJSONArray(Item.activity.getString(R.string.detail_property));
		
		if (children != null) {
			for (int i = 0; i < children.length(); i++) {
				this.childs.put(new DetailItem(children.optJSONObject(i)));
			}
		}
		else {
			if (detailUrl == null)
				throw new NullPointerException("Childitem must contain an detail item or url!!");
		}
		
		this.type = ItemTypes.ChildItem;
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.activity.getString(R.string.item_type_property), Item.activity.getString(R.string.item_child_type));
		
		result.putString(Item.activity.getString(R.string.detailurl_property), this.detailUrl);
		
		return result;
	}
}
