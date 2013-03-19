package at.tectas.buildbox.communication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class BitmapAsyncCommunicator extends AsyncTask<String, Integer, Bitmap> {
	
	private ImageView view = null;
	private ICommunicatorCallback callbackListener = null;
	private Communicator communicator = null;
	
	public BitmapAsyncCommunicator (Communicator communicator) {
		this.communicator = communicator;
	}
	
	public BitmapAsyncCommunicator (Communicator communicator, ImageView view, ICommunicatorCallback callback) {
		this(communicator);
		this.view = view;
		this.callbackListener = callback;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			int width = view.getWidth();
			
			if (width == 0) {
				DisplayMetrics displaymetrics = new DisplayMetrics();
				((Activity)view.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				width = displaymetrics.widthPixels;
			}
			
			return this.communicator.getBitmap(params[0], width);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if (this.callbackListener != null)
			this.callbackListener.updateWithImage(this.view, result);
		super.onPostExecute(result);
	}
}