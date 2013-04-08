package at.tectas.buildbox.library.recovery;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import at.tectas.buildbox.library.communication.DownloadMap;
import at.tectas.buildbox.library.communication.DownloadPackage;
import at.tectas.buildbox.library.communication.DownloadResponse;
import at.tectas.buildbox.library.communication.DownloadStatus;
import at.tectas.buildbox.library.content.items.properties.DownloadType;
import at.tectas.buildbox.library.helpers.ShellHelper;
import at.tectas.buildbox.library.recovery.RebootType;

@SuppressLint("DefaultLocale")
public class OpenRecoveryScript {
	@SuppressLint("SdCardPath")
	public static final String RECOVERY_SDCARD_ROOT = "/sdcard";
	public static final String RECOVERY_EXTERNAL_SDCARD_ROOT = "/external_sd";
	
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
		if (path != null && path.contains("extSdCard") || path.contains("sdcard1")) {
			path = path.replaceAll("/mnt/extSdCard", RECOVERY_EXTERNAL_SDCARD_ROOT)
					.replaceAll("/storage/sdcard1", RECOVERY_EXTERNAL_SDCARD_ROOT)
					.replaceAll("/extSdCard", RECOVERY_EXTERNAL_SDCARD_ROOT);
		}
		
		if (path != null && path.contains("sdcard0") || path.contains("emulated") || path.contains("mnt")) {
			path = path.replaceAll("/storage/sdcard0", RECOVERY_SDCARD_ROOT)
					.replaceAll("/storage/emulated/0", RECOVERY_SDCARD_ROOT)
					.replaceAll("/storage/emulated/legacy", RECOVERY_SDCARD_ROOT)
					.replaceAll("/mnt/sdcard", RECOVERY_SDCARD_ROOT);
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
	
	public void executeAndReboot() {
		if (this.tailWritten == false) {
			this.addScriptTail();
		}
		
		this.execute();
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
	
	public void addInstallsToScript(DownloadMap map) {
		if (this.configuration != null && map != null) {
			for (int i = 0; i < map.size(); i++) {
				DownloadPackage pack = map.get(i);
				
				this.addIntallToScript(pack);
			}
		}
	}
}
