package at.tectas.buildbox.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import at.tectas.buildbox.recovery.RebootType;

public class ShellHelper {
	
	public static String[] addShellPrefix(String[] commands) {
		String[] newCommands = new String[commands.length + 1];
		
		newCommands[0] = "/system/bin/sh";
		
		for (int i = 0; i < commands.length; i++) {
			newCommands[i + 1] = commands[i];
		}
		
		return newCommands;
	}
	
	public static String[] addRootPrefix(String[] commands) {
		String[] rootCommands = new String[commands.length + 1];
		
		rootCommands[0] = "su -c 'system/bin/sh'";
		
		for (int i = 0; i < commands.length; i++) {
			rootCommands[i + 1] = commands[i];
		}
		
		return rootCommands;
	}
	
	public static ArrayList<String> executeCommand(String[] commands) {
		DataOutputStream shellIn = null;
		BufferedReader shellOut = null;
		
		try {
			if (commands != null && commands.length > 0) {
				Process process = Runtime.getRuntime().exec(commands[0]);
				
			    shellIn = new DataOutputStream(process.getOutputStream());
			    shellOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		        
			    ShellStreamWorker outWorker = new ShellStreamWorker(shellOut);
			    
			    outWorker.start();
			    
			    for (int i = 1; i < commands.length; i++) {
			    	shellIn.writeBytes(commands[i] + "\n");
			    }
		        
		        shellIn.writeBytes("exit\n");
		        
			    process.waitFor();
			    
			    ArrayList<String> output = outWorker.result;
			    
			    process.destroy();
			    
			    return output;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (shellIn != null) {
					shellIn.close();
				}
				
				if (shellOut != null) {
					shellOut.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static ArrayList<String> executeSingleCommand(String command) {
		return ShellHelper.executeCommand(ShellHelper.addShellPrefix(new String[] { command }));
	}
	
	public static ArrayList<String> executeRootCommands(String[] commands) {
		return ShellHelper.executeCommand(ShellHelper.addRootPrefix(commands));
	}
	
	public static ArrayList<String> executeSingleRootCommand(String command) {
		return ShellHelper.executeRootCommands(new String[] { command });
	}
	
	public static String executeSingleCommandWithSingleLineOutput(String command) {
		ArrayList<String> result = ShellHelper.executeSingleCommand(command);
		
		if (result.size() == 0) {
			return null;
		}
		else {
			return result.get(0);
		}
	}
	
	public static String getBuildPropProperty(String property) {
		return ShellHelper.executeSingleCommandWithSingleLineOutput("getprop " + property);
	}
	
	public static String getEchoCommand(String input) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("echo ");
		buffer.append("\"");
		buffer.append(input);
		buffer.append("\"");
		
		return buffer.toString();
	}
	
	public static String getAppendStringToFileOperator() {
		return " >> ";
	}
	
	public static String getStringToFileOperator() {
		return " > ";
	}
	
	public static String getThreePartCommand(String firstCommand, String operator, String secondCommand) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(firstCommand);
		buffer.append(operator);
		buffer.append(secondCommand);
		
		return buffer.toString();
	}
	
	public static String getAppendStringToFileCommand(String command, String fullFilePath) {
		return ShellHelper.getThreePartCommand(ShellHelper.getEchoCommand(command), ShellHelper.getAppendStringToFileOperator(), fullFilePath);
	}
	
	public static String getStringToFileCommand(String command, String fullFilePath) {
		return ShellHelper.getThreePartCommand(ShellHelper.getEchoCommand(command), ShellHelper.getStringToFileOperator(), fullFilePath);
	}
	
	public static String getCdCommand(String directory) {
		return "cd " + directory;
	}
	
	public static String getMkdirCommand(String directoryname) {
		return "mkdir " + directoryname;
	}
	
	public static String getRebootCommand(RebootType type) {
		if (type == RebootType.Recovery) {
			return "reboot recovery";
		}
		else if (type == RebootType.Bootloader) {
			return "reboot bootloader";
		}
		else {
			return "reboot";
		}
	}
}
