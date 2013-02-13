package at.tectas.buildbox.content;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import at.tectas.buildbox.R;

public class DetailItem extends Item {	
	public String description;
	public String version;
	public String url;
	public String md5sum;
	public ArrayList<Developer> developers = new ArrayList<Developer>();
	public ArrayList<String> homePages = new ArrayList<String>();
	public ArrayList<String> imageUrls = new ArrayList<String>();
	public ArrayList<String> changelog = new ArrayList<String>();
	
	public DetailItem (JSONObject json) throws JSONException {
		super(json);
		this.description = json.optString(Item.activity.getString(R.string.description_property));
		this.version = json.optString(Item.activity.getString(R.string.version_property));
		this.url = json.optString(Item.activity.getString(R.string.url_property));
		this.md5sum = json.optString(Item.activity.getString(R.string.md5sum_property));
		
		JSONArray dummyArray = json.optJSONArray(Item.activity.getString(R.string.developers_property));
		
		this.parseJSONArray(dummyArray, ArrayTypes.DEVELOPERS);
		
		dummyArray = null;
		dummyArray = json.optJSONArray(Item.activity.getString(R.string.homepages_property));
		
		this.parseJSONArray(dummyArray, ArrayTypes.HOMEPAGES);
		
		dummyArray = null;
		dummyArray = json.optJSONArray(Item.activity.getString(R.string.imageurls_property));
		
		this.parseJSONArray(dummyArray, ArrayTypes.IMAGEURLS);
		
		dummyArray = null;
		dummyArray = json.optJSONArray(Item.activity.getString(R.string.changelog_property));

		this.parseJSONArray(dummyArray, ArrayTypes.CHANGELOG);
		
		this.type = ItemTypes.DetailItem;
	}
	
	@Override
	public Bundle parseItemToBundle() {
		Bundle result = super.parseItemToBundle();
		
		result.putString(Item.activity.getString(R.string.item_type_property), Item.activity.getString(R.string.item_detail_type));
		
		result.putString(Item.activity.getString(R.string.description_property), this.description);
		result.putString(Item.activity.getString(R.string.version_property), this.version);
		result.putString(Item.activity.getString(R.string.md5sum_property), this.md5sum);
		result.putString(Item.activity.getString(R.string.url_property), this.url);
		
		result.putStringArrayList(Item.activity.getString(R.string.changelog_property), this.changelog);
		
		result.putStringArrayList(Item.activity.getString(R.string.homepages_property), this.homePages);
		
		result.putStringArrayList(Item.activity.getString(R.string.imageurls_property), this.imageUrls);
		
		if (this.developers.size() > 0) {
			Bundle developers = new Bundle();
			
			ArrayList<String> names = new ArrayList<String>();
			
			ArrayList<String> donation = new ArrayList<String>();
			
			for (int i = 0; i < this.developers.size(); i++) {
				Developer current = this.developers.get(i);
				
				names.add(current.Name);
				
				donation.add(current.DonationUrl == null?"": current.DonationUrl);
			}
			
			developers.putStringArrayList(Item.activity.getString(R.string.developer_names_property), names);
			
			developers.putStringArrayList(Item.activity.getString(R.string.developers_donationurls_property), donation);
			
			result.putBundle(Item.activity.getString(R.string.developers_property), developers);
		}
		
		return result;
	}
}
