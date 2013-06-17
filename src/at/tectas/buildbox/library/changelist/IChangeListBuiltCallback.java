package at.tectas.buildbox.library.changelist;

import java.util.HashMap;

public interface IChangeListBuiltCallback {
	public void notifyBuilt(HashMap<ChangeType, String> changes);
}
