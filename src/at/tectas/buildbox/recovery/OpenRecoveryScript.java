package at.tectas.buildbox.recovery;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.DownloadStatus;
import at.tectas.buildbox.content.DownloadType;
import at.tectas.buildbox.helpers.ShellHelper;
import at.tectas.buildbox.recovery.RebootType;

@SuppressLint("DefaultLocale")
public class OpenRecoveryScript {
	public OpenRecoveryScriptConfiguration configuration = null;
	protected ArrayList<String> shellCommands = new ArrayList<String>();
	protected String scriptFilename = "openrecoveryscript";
	protected boolean headWritten = false;
	protected boolean tailWritten = false;
	
	public OpenRecoveryScript (OpenRecoveryScriptConfiguration config) {
		this.configuration = config;
	}
	
	public void rebootToRecovery() {
		ShellHelper.executeSingleRootCommand("reboot recovery");
	}
	
	@SuppressLint("SdCardPath")
	public String mutateStoragePathForRecovery (String path) {
		if (path != null && path.contains("extSdCard")) {
			path = path.replaceAll("/mnt/extSdCard", "/external_sd");
		}
		
		if (path != null && path.contains("sdcard0")) {
			path = path.replaceAll("/storage/sdcard0", "/sdcard");
		}
		
		if (path != null && !path.endsWith("/")) {
			path += "/";
		}
		
		return path;
	}
	
	public void addScriptHead() {
		if (!this.headWritten) {
			shellCommands.add(ShellHelper.getCdCommand("/cache/recovery"));
			
			if (this.configuration.backupFirst == true) {
				shellCommands.add(ShellHelper.getStringToFileCommand("backup SDR123BO", scriptFilename));
			}
			
			if (this.configuration.wipeData == true) {
				if (shellCommands.size() == 1) {
					shellCommands.add(ShellHelper.getStringToFileCommand("wipe data", scriptFilename));
				}
				else {
					shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe data", scriptFilename));
				}
			}
			
			if (shellCommands.size() == 1) {
				shellCommands.add(ShellHelper.getStringToFileCommand("wipe cache", scriptFilename));
			}
			else {
				shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe cache", scriptFilename));			
			}
			shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe dalvik", scriptFilename));
			
			this.headWritten = true;
		}
	}
	
	public void addScriptTail() {
		if (!this.tailWritten) {
			shellCommands.add(ShellHelper.getRebootCommand(RebootType.Recovery));
			
			this.tailWritten = true;
		}
	}
	
	public void execute() {
		String[] commands = new String[this.shellCommands.size()];
		
		shellCommands.toArray(commands);
		
		shellCommands = new ArrayList<String>();
		
		ShellHelper.executeRootCommands(commands);
	}
	
	public void addIntallToScript(DownloadPackage pack) {
		DownloadResponse response = pack.getResponse();
		
		if ((response.status == DownloadStatus.Successful || 
			response.status == DownloadStatus.Done ||
			(response.status == DownloadStatus.Md5mismatch && this.configuration.includeMd5mismatch == true)) &&
			(((response.mime != null && response.mime.toLowerCase().equals("zip")) || (pack.type != null && pack.type.equals(DownloadType.zip))))) {
			
			if ((pack.type != null && pack.type.equals(DownloadType.apk)))
				return;
			
			if (shellCommands.size() == 1) {
				shellCommands.add(ShellHelper.getStringToFileCommand("install " + this.mutateStoragePathForRecovery(pack.getDirectory()) + pack.getFilename(), scriptFilename));
			}
			else {
				shellCommands.add(ShellHelper.getAppendStringToFileCommand("install " + this.mutateStoragePathForRecovery(pack.getDirectory()) + pack.getFilename(), scriptFilename));
			}
		}
	}
	
	public void addInstallsToScript() {
		if (this.configuration != null && this.configuration.downloads != null) {
			for (int i = 0; i < this.configuration.downloads.size(); i++) {
				DownloadPackage pack = this.configuration.downloads.get(i);
				
				this.addIntallToScript(pack);
			}
		}
	}
	
	public void writeScriptFileAndReboot() {		
		this.addScriptHead();
		this.addInstallsToScript();
		this.addScriptTail();
		this.execute();
	}
}
