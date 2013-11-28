package at.tectas.buildbox.library.content.items;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.os.Bundle;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.library.content.ItemList;
import at.tectas.buildbox.library.content.items.properties.ItemTypes;

public class ParentItem extends Item {
	public static Communicator communicator = null;
	public String thumbnailUrl = null;
	public ItemList children = new ItemList();
	public String childsUrl = null;
	public ICommunicatorCallback callback = null;

	public ParentItem(JsonObject json, ICommunicatorCallback callback) {
		this(null, json, callback);
	}

	public ParentItem(JsonObject json) {
		this(null, json, null);
	}

	public ParentItem(Item parent, JsonObject json) {
		this(parent, json, null);
	}

	public ParentItem(Item parent, JsonObject json,
			ICommunicatorCallback callback) {
		super(parent, json);

		this.callback = callback;

		this.thumbnailUrl = Item.helper.tryGetStringFromJson(
				Item.context.getString(R.string.thumbnailurl_property), json);

		JsonArray children = Item.helper.tryGetJsonArrayFromJson(
				Item.context.getString(R.string.children_property), json);

		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				JsonElement element = children.get(i);

				if (element.isJsonObject()) {
					JsonObject object = element.getAsJsonObject();

					Item child = Item.parser.parseJsonItem(this, object);

					if (child != null)
						this.children.add(child);
				}
			}
		} else {
			this.childsUrl = Item.helper.tryGetStringFromJson(
					Item.context.getString(R.string.children_property), json);
		}

		if (this.callback != null && ParentItem.communicator != null) {
			this.childsUrl = Item.helper.tryGetStringFromJson(
					Item.context.getString(R.string.children_property), json);

			communicator.executeJSONElementAsyncCommunicator(this.childsUrl,
					this.callback, this);
		}
		this.type = ItemTypes.ParentItem;
	}

	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();

		result.putString(Item.context.getString(R.string.item_type_property),
				Item.context.getString(R.string.item_parent_type));

		result.putString(
				Item.context.getString(R.string.thumbnailurl_property),
				this.thumbnailUrl);

		ArrayList<String> children = new ArrayList<String>();

		for (int i = 0; i < this.children.size(); i++) {
			children.add(this.children.get(i).ID.toString());
		}

		result.putStringArrayList(
				Item.context.getString(R.string.children_property), children);

		return result;
	}

	@Override
	public ArrayList<Item> getChildren() {
		return this.children;
	}
}
