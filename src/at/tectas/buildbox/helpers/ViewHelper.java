package at.tectas.buildbox.helpers;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewHelper {
	private final View view;
	
	public ViewHelper (View _view) {
		this.view = _view;
	}
	
	public void changeTextViewText(int viewId, String Text) {
		TextView dummy = (TextView) this.view.findViewById(viewId);
		dummy.setText(Text);
	}
	
	public void changeTextViewText(int viewId, ArrayList<String> text) {
		TextView dummy = (TextView) this.view.findViewById(viewId);
		
		StringBuilder textDummy = new StringBuilder();
		
		for (String item: text) {
			textDummy.append(item);
			textDummy.append(System.getProperty("line.seperator"));
		}
		
		dummy.setText(textDummy.toString());
	}
	
	public ViewGroup getLayout (int viewId) {
		return (ViewGroup) view.findViewById(viewId);
	}
}
