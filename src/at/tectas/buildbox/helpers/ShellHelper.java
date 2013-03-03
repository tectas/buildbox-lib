package at.tectas.buildbox.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShellHelper {

	public enum RebootType {
		Recovery, Bootloader, System
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
			Process process = Runtime.getRuntime().exec("/system/bin/sh");
			
		    shellIn = new DataOutputStream(process.getOutputStream());
		    shellOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
		    ShellStreamWorker outWorker = new ShellStreamWorker(shellOut);
		    
		    outWorker.start();
		    
		    for (String command: commands) {
		    	shellIn.writeBytes(command + "\n");
		    }
	        
	        shellIn.writeBytes("exit\n");
	        
		    process.waitFor();
		    
		    ArrayList<String> output = outWorker.result;
		    
		    process.destroy();
		    
		    return output;
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
		return ShellHelper.executeCommand(new String[] { command });
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
