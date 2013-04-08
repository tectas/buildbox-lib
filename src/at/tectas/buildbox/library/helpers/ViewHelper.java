package at.tectas.buildbox.library.helpers;

import java.util.ArrayList;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHelper {
	private final View view;
	
	public ViewHelper (View _view) {
		this.view = _view;
	}
	
	public void changeTextViewText(int viewId, String text) {
		TextView dummy = (TextView) this.view.findViewById(viewId);
		dummy.setText(this.getSpannedFromHtmlString(text));
	}
	
	public void changeTextViewText(int viewId, ArrayList<String> text) {
		TextView dummy = (TextView) this.view.findViewById(viewId);
		
		StringBuilder textDummy = new StringBuilder();
		
		for (String item: text) {
			if (!PropertyHelper.stringIsNullOrEmpty(item)) {
				textDummy.append(this.getSpannedFromHtmlString(item));
				textDummy.append(System.getProperty("line.seperator"));
			}
		}
		
		dummy.setText(textDummy.toString());
	}
	
	public ViewGroup getLayout (int viewId) {
		return (ViewGroup) view.findViewById(viewId);
	}
	
	public Spanned getSpannedFromHtmlString(String html) {
		return Html.fromHtml(html);
	}
	
    public static boolean setAlphaOfView(int alpha, View view)
    {
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
            	setAlphaOfView(alpha, ((ViewGroup) view).getChildAt(i));
                if (((ViewGroup) view).getBackground() != null) ((ViewGroup) view).getBackground().setAlpha(alpha);
            }
        }
        else if (view instanceof ImageView)
        {
            if (((ImageView) view).getDrawable() != null) ((ImageView) view).getDrawable().setAlpha(alpha);
            if (((ImageView) view).getBackground() != null) ((ImageView) view).getBackground().setAlpha(alpha);
        }
        else if (view instanceof TextView)
        {
            ((TextView) view).setTextColor(((TextView) view).getTextColors().withAlpha(alpha));
            if (((TextView) view).getBackground() != null) ((TextView) view).getBackground().setAlpha(alpha);
        }
        else if (view instanceof EditText)
        {
            ((EditText) view).setTextColor(((EditText) view).getTextColors().withAlpha(alpha));
            if (((EditText) view).getBackground() != null) ((EditText) view).getBackground().setAlpha(alpha);
        }
        return true;
    }
}
