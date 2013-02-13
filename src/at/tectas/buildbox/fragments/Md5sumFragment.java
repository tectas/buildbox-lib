package at.tectas.buildbox.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tectas.buildbox.R;

public class Md5sumFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.md5sum_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null) {
			String md5sum = arguments.getString(getString(R.string.md5sum_property));
			
			TextView textView = (TextView) view.findViewById(R.id.md5sum);
			textView.setText(arguments.getString(getString(R.string.md5sum_property)));
		}
		
		return view;
	}
}
