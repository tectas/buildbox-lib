package at.tectas.buildbox.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonHelper {
	
	public String tryGetStringFromJson(String property, JsonObject json) {
		String result = null;
		JsonElement dummy = json.get(property);
		
		if (dummy != null && dummy.isJsonPrimitive())
			result = dummy.getAsString();
		
		return result;
	}
	
	public int tryGetIntFromJson(String property, JsonObject json) {
		int result = Integer.MIN_VALUE;
		JsonElement dummy = json.get(property);
		
		if (dummy != null && dummy.isJsonPrimitive())
			result = dummy.getAsInt();
		
		return result;
	}
	
	public JsonArray tryGetJsonArrayFromJson(String property, JsonObject json) {
		JsonElement dummy = null;
		dummy = json.get(property);
		
		JsonArray dummyArray = null;
		
		if (dummy != null && dummy.isJsonArray())
			dummyArray = dummy.getAsJsonArray();
		
		return dummyArray;
	}
	
	public JsonObject tryGetJsonObjectFromJson(String property, JsonObject json) {
		JsonElement dummy = null;
		dummy = json.get(property);
		
		JsonObject dummyArray = null;
		
		if (dummy != null && dummy.isJsonObject())
			dummyArray = dummy.getAsJsonObject();
		
		return dummyArray;
	}
}
