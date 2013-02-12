package at.tectas.buildbox.content;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Developer {
	public String Name;
	public String DonationUrl;
	
	public Developer(JSONObject json) throws JSONException {
		JSONArray names = json.names();
		
		int index = 0;
		
		for (; index < names.length(); index++) {
			this.Name = names.optString(index);
		}
		
		this.DonationUrl = json.optString(this.Name);
	}
}
