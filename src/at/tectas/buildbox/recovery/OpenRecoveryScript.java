package at.tectas.buildbox.recovery;

import java.util.ArrayList;

import at.tectas.buildbox.communication.DownloadMap;
import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;
import at.tectas.buildbox.helpers.ShellHelper;
import at.tectas.buildbox.helpers.ShellHelper.RebootType;

public class OpenRecoveryScript {
	public DownloadMap downloads = new DownloadMap();
	public OpenRecoveryScriptConfiguration configuration = null;
	
	public OpenRecoveryScript (OpenRecoveryScriptConfiguration config) {
		this.configuration = config;
	}
	
	public void rebootToRecovery() {
		ShellHelper.executeSingleRootCommand("reboot recovery");
	}
	
	public void mutateStoragePathForRecovery () {
		if (this.configuration.directoryPath.contains("extSdCard")) {
			this.configuration.directoryPath = this.configuration.directoryPath.replaceAll("/mnt/extSdCard", "/external_sd");
		}
		
		if (!this.configuration.directoryPath.endsWith("/")) {
			this.configuration.directoryPath += "/";
		}
	}
	
	public void writeScriptFile() {
		this.mutateStoragePathForRecovery();
		
		String filePath = "openrecoveryscript";
		
		ArrayList<String> shellCommands = new ArrayList<String>();
		
		shellCommands.add(ShellHelper.getCdCommand("/cache/recovery"));
		
		if (this.configuration.backupFirst == true) {
			shellCommands.add(ShellHelper.getStringToFileCommand("backup SDR123BO", filePath));
		}
		
		for (DownloadPackage pack: this.configuration.downloads.values()) {
			DownloadResponse response = pack.response;
			
			if ((response.status == DownloadStatus.Successful || 
				response.status == DownloadStatus.Done ||
				(response.status == DownloadStatus.Md5mismatch && this.configuration.includeMd5mismatch == true)) &&
				response.mime == "zip") {
				shellCommands.add(ShellHelper.getAppendStringToFileCommand("install " + this.configuration.directoryPath + pack.response.fileName, filePath));
			}
		}
		
		if (this.configuration.wipeData == true) {
			shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe data", filePath));
		}
		
		shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe cache", filePath));
		
		shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe dalvik", filePath));
		
		shellCommands.add(ShellHelper.getRebootCommand(RebootType.Recovery));
		
		String[] commands = new String[0];
		
		ShellHelper.executeRootCommands(shellCommands.toArray(commands));

	}
}
