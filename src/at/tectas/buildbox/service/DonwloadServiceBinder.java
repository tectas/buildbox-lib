package at.tectas.buildbox.service;

import android.app.Service;
import android.os.Binder;

public class DonwloadServiceBinder extends Binder {
	private Service boundService = null;
	
	public DonwloadServiceBinder(Service service) {
		this.boundService = service;
	}
	
	public Service getservice() {
		return this.boundService;
	}
}
