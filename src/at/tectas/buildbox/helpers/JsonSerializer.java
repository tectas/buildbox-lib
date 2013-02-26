package at.tectas.buildbox.helpers;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonSerializer {
	
	public JsonObject getJsonObject(String property, String value) {
		JsonObject json = new JsonObject();
		
		json.addProperty(property, value);
		
		return json;
	}
	
	public JsonObject getJsonObject(String property, int value) {
		JsonObject json = new JsonObject();
		
		json.addProperty(property, value);
		
		return json;
	}
	
	public JsonObject getJsonObject(String property, boolean value) {
		JsonObject json = new JsonObject();
		
		json.addProperty(property, value);
		
		return json;
	}
	
	public JsonObject getJsonObject(String[] properties, String[] values) {
		JsonObject json = new JsonObject();
		
		for (int i = 0; i < properties.length && i < values.length; i++) {
			json.addProperty(properties[i], values[i]);
		}
		
		return json;
	}
	
	public JsonObject getJsonObject(Map<String, String> map) {
		JsonObject json = new JsonObject();
		
		for (String key: map.keySet()) {
			json.addProperty(key, map.get(key));
		}
		
		return json;
	}
	
	public JsonObject getJsonObject(String property, JsonArray objects) {
		JsonObject json = new JsonObject();
		
		json.add(property, objects);
		
		return json;
	}
	
	public JsonArray getJsonArray(JsonObject[] objects) {
		JsonArray array = new JsonArray();
		
		for (JsonObject object: objects) {
			array.add(object);
		}
		
		return array;
	}
}
