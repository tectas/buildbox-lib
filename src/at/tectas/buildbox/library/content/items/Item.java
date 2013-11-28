package at.tectas.buildbox.library.content.items;

import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.content.items.properties.ArrayTypes;
import at.tectas.buildbox.library.content.items.properties.Developer;
import at.tectas.buildbox.library.content.items.properties.ItemTypes;
import at.tectas.buildbox.library.helpers.JsonHelper;

public abstract class Item {

	public static final String TAG = "ITEM";
	protected static Context context;
	public static JsonHelper helper = new JsonHelper();
	public static JsonItemParser parser = null;
	public String device = null;
	public Item parent = null;
	public ItemTypes type = null;
	public final UUID ID = UUID.randomUUID();
	public String title;
	public ArrayList<String> dependencies = new ArrayList<String>();

	public Item(JsonObject json) {
		this(null, json);
	}

	public Item(Item parent, JsonObject json) {
		this.parent = parent;

		this.title = Item.helper.tryGetStringFromJson(
				Item.context.getString(R.string.title_property), json);

		this.tryGetArrayFromJson(
				Item.context.getString(R.string.dependencies_property), json,
				ArrayTypes.DEPENDENCIES);
	}

	protected Item() {
	}

	public static void setActivity(Context activity) {
		Item.context = activity;
	}

	public static Context getActivity() {
		return Item.context;
	}

	public Bundle parseItemToBundle() {
		Bundle result = new Bundle();

		result.putString(Item.context.getString(R.string.id_property),
				this.ID.toString());

		result.putString(Item.context.getString(R.string.title_property),
				this.title);

		result.putStringArrayList(
				Item.context.getString(R.string.dependencies_property),
				this.dependencies);

		return result;
	};

	protected void parseJsonArray(JsonArray json, ArrayTypes detail) {
		Object dummyObject = null;

		if (detail == ArrayTypes.DEPENDENCIES) {
			dummyObject = this.dependencies;
		} else {
			DetailItem item = (DetailItem) this;

			if (detail == ArrayTypes.DEVELOPERS) {
				dummyObject = item.developers;
			} else if (detail == ArrayTypes.HOMEPAGES) {
				dummyObject = item.homePages;
			} else if (detail == ArrayTypes.IMAGEURLS) {
				dummyObject = item.imageUrls;
			} else if (detail == ArrayTypes.CHANGELOG) {
				dummyObject = item.changelog;
			}
		}
		if (json != null && dummyObject != null) {
			if (detail != ArrayTypes.DEVELOPERS) {
				@SuppressWarnings("unchecked")
				ArrayList<String> list = (ArrayList<String>) dummyObject;

				for (int i = 0; i < json.size(); i++) {
					String object = json.get(i).getAsString();

					if (object != null)
						list.add(object);
				}
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<Developer> list = (ArrayList<Developer>) dummyObject;

				for (int i = 0; i < json.size(); i++) {
					Developer object = null;

					try {
						object = new Developer(json.get(i).getAsJsonObject());
					} catch (Exception e) {
						Log.e(Item.TAG,
								"Couldn't parse developer: " + e.getMessage());
					}

					if (object != null) {
						list.add(object);
					}
				}
			}
		}
	}

	public void tryGetArrayFromJson(String property, JsonObject json,
			ArrayTypes type) {
		this.parseJsonArray(
				Item.helper.tryGetJsonArrayFromJson(property, json), type);
	}

	public void addToParent(Item parent) {
		parent.getChildren().add(this);
		this.parent = parent;
	}
	
	public abstract ArrayList<Item> getChildren();
}