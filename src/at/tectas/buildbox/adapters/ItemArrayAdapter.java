package at.tectas.buildbox.adapters;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.tectas.buildbox.content.ChildItem;
import at.tectas.buildbox.content.Item;
import at.tectas.buildbox.content.Item.ItemTypes;
import at.tectas.buildbox.content.ParentItem;
import at.tectas.buildbox.R;

public class ItemArrayAdapter extends ArrayAdapter<at.tectas.buildbox.content.Item> {
	
	private final static String TAG = "ItemArrayAdapter";
	private final Activity context;
	private final ArrayList<Item> items;
	private Item currentItem = null;
	
	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}
	
	public ItemArrayAdapter(Activity context, int textViewResourceId,
			Item[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.items = new ArrayList<Item>(Arrays.asList(objects));
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = this.context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, parent);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.ListItemTextView);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.ListItemImageView);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Item item = this.items.get(position);
		this.currentItem = item;
	    holder.text.setText(item.title);
	    
	    if (item.type == ItemTypes.ParentItem || item.type == ItemTypes.ChildItem) {
	    	
	    	ChildItem child = (ChildItem) item;
	    	
		    try {
		        URL thumb_u = new URL(child.thumbnailUrl);
		        Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
		        holder.image.setImageDrawable(thumb_d);
		    }
		    catch (Exception e) {
		        Log.e(ItemArrayAdapter.TAG, e.getMessage());
		        for (StackTraceElement element: e.getStackTrace()) {
		        	Log.e(ItemArrayAdapter.TAG, element.toString());
		        }
		    }
	    }
	    else if (item.type == ItemTypes.DetailItem) {
	    	
	    }
	    
		return rowView;
	}
}
