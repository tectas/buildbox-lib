package at.tectas.buildbox.recovery;

import java.util.ArrayList;

import at.tectas.buildbox.communication.DownloadPackage;
import at.tectas.buildbox.communication.DownloadResponse;
import at.tectas.buildbox.communication.DownloadResponse.DownloadStatus;
import at.tectas.buildbox.helpers.ShellHelper;
import at.tectas.buildbox.helpers.ShellHelper.RebootType;

public class OpenRecoveryScript {
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
		
		for (int i = 0; i < this.configuration.downloads.size(); i++) {
			DownloadPackage pack = this.configuration.downloads.get(i);
			
			DownloadResponse response = pack.getResponse();
			
			if ((response.status == DownloadStatus.Successful || 
				response.status == DownloadStatus.Done ||
				(response.status == DownloadStatus.Md5mismatch && this.configuration.includeMd5mismatch == true)) &&
				response.mime.equals("zip")) {
				
				if (shellCommands.size() == 1) {
					shellCommands.add(ShellHelper.getStringToFileCommand("install " + this.configuration.directoryPath + pack.getFilename(), filePath));
				}
				else {
					shellCommands.add(ShellHelper.getAppendStringToFileCommand("install " + this.configuration.directoryPath + pack.getFilename(), filePath));
				}
			}
		}
		
		if (this.configuration.wipeData == true) {
			if (shellCommands.size() == 1) {
				shellCommands.add(ShellHelper.getStringToFileCommand("wipe data", filePath));
			}
			else {
				shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe data", filePath));
			}
		}
		
		if (shellCommands.size() == 1) {
			shellCommands.add(ShellHelper.getStringToFileCommand("wipe cache", filePath));
		}
		else {
			shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe cache", filePath));			
		}
		shellCommands.add(ShellHelper.getAppendStringToFileCommand("wipe dalvik", filePath));
		
		shellCommands.add(ShellHelper.getRebootCommand(RebootType.Recovery));
		
		String[] commands = new String[0];
		
		ShellHelper.executeRootCommands(shellCommands.toArray(commands));

	}
}
