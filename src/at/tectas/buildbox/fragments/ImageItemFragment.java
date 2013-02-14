package at.tectas.buildbox.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import at.tectas.buildbox.MainActivity;
import at.tectas.buildbox.R;
import at.tectas.buildbox.communication.Communicator;

public class ImageItemFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ImageView view = (ImageView) inflater.inflate(R.layout.image_item_fragment, container, false);
		
		Bundle arguments = this.getArguments();
		
		if (arguments != null && !arguments.isEmpty()) {
			String url = arguments.getString(getString(R.string.imageurls_property));
			
			view.setImageResource(R.drawable.spinner);
			
			RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			
			animation.setDuration(1000);
			
			animation.setRepeatCount(Animation.INFINITE);
			
			animation.setRepeatMode(Animation.INFINITE);
			
			animation.setInterpolator(new LinearInterpolator());
			
			view.startAnimation(animation);
			
			MainActivity activity = (MainActivity) getActivity();
			
			Communicator communicator = activity.getCommunicator();
			
			communicator.executeBitmapAsyncCommunicator(url, view, activity);
		}
		
		return view;
	}
}
