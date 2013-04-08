package at.tectas.buildbox.library.adapters;

import java.util.ArrayList;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.tectas.buildbox.R;
import at.tectas.buildbox.library.communication.Communicator;
import at.tectas.buildbox.library.content.items.ChildItem;
import at.tectas.buildbox.library.content.items.DetailItem;
import at.tectas.buildbox.library.content.items.Item;
import at.tectas.buildbox.library.content.items.ParentItem;
import at.tectas.buildbox.library.content.items.properties.ItemTypes;
import at.tectas.buildbox.library.download.DownloadActivity;
import at.tectas.buildbox.library.helpers.PropertyHelper;
import at.tectas.buildbox.library.listeners.ChildItemListItemListener;
import at.tectas.buildbox.library.listeners.ParentItemListItemListener;

public class ItemArrayAdapter extends ArrayAdapter<at.tectas.buildbox.library.content.items.Item> {
	
	private final static String TAG = "ItemArrayAdapter";
	private final DownloadActivity context;
	private final Item[] items;
	private FragmentManager manager = null;
	
	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}
	
	public ItemArrayAdapter(DownloadActivity context, int textViewResourceId,
			Item[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.items = objects;
	}
	
	public ItemArrayAdapter(DownloadActivity context, int textViewResourceId,
			ArrayList<Item> objects, FragmentManager manager) {
		this(context, textViewResourceId, objects.toArray(new Item[objects.size()]));
		
		this.manager = manager;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = this.context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.ListItemTextView);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.ListItemImageView);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		Item item = null;
		
		if (position < this.items.length)
			item = this.items[position];
		
		if (item != null) {
		    holder.text.setText(item.title);
		    
		    try {
		        String thumbnail = (((ParentItem)item).thumbnailUrl);
		        
		        holder.image.setTag(thumbnail);
		        
		        if (thumbnail != null && this.context.getRemoteDrawables().containsKey(thumbnail)) {
		        	holder.image.setImageBitmap(this.context.getRemoteDrawables().get(thumbnail));
		        }
		        else if (thumbnail != null){
			        holder.image.setImageResource(R.drawable.spinner);
					
					RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					
					animation.setDuration(1000);
					
					animation.setRepeatCount(Animation.INFINITE);
					
					animation.setRepeatMode(Animation.INFINITE);
					
					animation.setInterpolator(new LinearInterpolator());
					
					holder.image.startAnimation(animation);
					
					Communicator communicator = this.context.getCommunicator();
					
					communicator.executeBitmapAsyncCommunicator(thumbnail, holder.image, this.context);
		        }
		        else {
		        	holder.image.setVisibility(ImageView.GONE);
		        }
		    }
		    catch (Exception e) {
		        Log.e(ItemArrayAdapter.TAG, " " + e.getMessage());
		        for (StackTraceElement element: e.getStackTrace()) {
		        	Log.e(ItemArrayAdapter.TAG, element.toString());
		        }
		    }
		    
		    if (item.type == ItemTypes.ParentItem) {
		    	
		    	ParentItem child = (ParentItem) item;
			    
			    rowView.setOnClickListener(new ParentItemListItemListener(this.context, child.childs, manager));
		    }
		    
		    else if (item.type == ItemTypes.ChildItem) {
		    	
		    	ChildItem child = (ChildItem) item;
		    	
		    	if (child.childs.size() == 0 && PropertyHelper.stringIsNullOrEmpty(child.detailUrl)) {
		    		
		    	}
		    	else {
		    		rowView.setOnClickListener(new ChildItemListItemListener((DetailItem) child.childs.get(0), manager));
		    	}
		    }
		}
		return rowView;
	}
}
