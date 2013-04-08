package at.tectas.buildbox.library.recovery;

public class OpenRecoveryScriptConfiguration {
	public boolean wipeData = false;
	public boolean includeMd5mismatch = false;
	public boolean backupFirst = true;
	public String directoryPath = null;
	
	public OpenRecoveryScriptConfiguration (String directoryPath) {
		this.directoryPath = directoryPath;
	}
}
