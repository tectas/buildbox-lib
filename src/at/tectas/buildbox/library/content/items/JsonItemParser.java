package at.tectas.buildbox.library.content.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import at.tectas.buildbox.library.R;
import at.tectas.buildbox.library.communication.callbacks.interfaces.ICommunicatorCallback;
import at.tectas.buildbox.library.content.ItemList;
import at.tectas.buildbox.library.content.items.properties.DownloadType;
import at.tectas.buildbox.library.helpers.JsonHelper;

@SuppressLint("DefaultLocale")
public class JsonItemParser {
	public static final String TAG = "JsonParser";
	public Context context = null;
	public String deviceModel = null;
	public DownloadType defaultType = null;
	protected JsonHelper helper = new JsonHelper();
	protected ICommunicatorCallback callback = null;

	public JsonItemParser(Context context, String deviceModel) {
		this.context = context;
		this.deviceModel = deviceModel;
	}

	public JsonItemParser(Context context, String deviceModel,
			DownloadType type) {
		this(context, deviceModel);
		this.defaultType = type;
	}

	public ItemList parseJson(JsonArray json) {
		ItemList items = new ItemList();

		for (int i = 0; i < json.size(); i++) {
			Item item = this.parseJsonItem(json.get(i).getAsJsonObject());

			if (item != null) {
				items.add(item);
			}
		}

		return items;
	}

	public Item parseJsonItem(JsonObject json) {
		return this.parseJsonItem(null, json);
	}

	public void setOnDemandItemCallback(ICommunicatorCallback callback) {
		this.callback = callback;
	}

	public Item parseJsonItem(Item parent, JsonObject json) {
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
				String device = helper.tryGetStringFromJson(
						context.getString(R.string.item_device_type), json);

				if (device != null
						&& this.deviceModel != null
						&& !this.deviceModel.toLowerCase().trim()
								.equals(device.toLowerCase().trim())) {
					return null;
				}
			}

			if (json.has(context.getString(R.string.item_detail_type))) {
				return new ChildItem(parent, json);
			} else if (json.has(context.getString(R.string.item_child_type))) {
				return new ParentItem(parent, json, this.callback);
			} else {
				return new DetailItem(parent, json);
			}
		}
		return null;
	}
}
