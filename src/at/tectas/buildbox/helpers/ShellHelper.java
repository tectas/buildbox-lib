package at.tectas.buildbox.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellHelper {

	public enum RebootType {
		Recovery, Bootloader, System
	}
	
	public static void executeSingleCommand(String command) {
		DataOutputStream shellIn = null;
		
		try {
			Process process = Runtime.getRuntime().exec(command);
		    
		    process.waitFor();
		    
		    process.destroy();
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
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void executeRootCommands(String[] commands) {
		DataOutputStream shellIn = null;
		
		try {
			Process process = Runtime.getRuntime().exec("su -c 'system/bin/sh'");
			
		    shellIn = new DataOutputStream(process.getOutputStream());
		    
		    for (String command: commands) {
		    	shellIn.writeBytes(command + "\n");
		    }
		    
		    shellIn.writeBytes("exit \n");
		    
		    process.waitFor();
		    
		    process.destroy();
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
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void executeSingleRootCommand(String command) {
		ShellHelper.executeRootCommands(new String[] { command });
	}
	
	public static String executeSingleRootCommandWithSingleLineOutput(String command) {

		DataOutputStream shellIn = null;
		BufferedReader shellOut = null;
		
		try {
			Process process = Runtime.getRuntime().exec("su -c 'system/bin/sh'");
			
		    shellIn = new DataOutputStream(process.getOutputStream());
		    shellOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
		    ShellStreamWorker outWorker = new ShellStreamWorker(shellOut);
		    
		    outWorker.start();
		    
	        shellIn.writeBytes(command + "\n");
	        
	        shellIn.writeBytes("exit\n");
	        
		    process.waitFor();
		    
		    String output = outWorker.result.toString();
		    
		    process.destroy();
		    
		    return output.toString();
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
	
	public static String executeSingleCommandWithSingleLineOutput(String command) {
		
		DataOutputStream shellIn = null;
		BufferedReader shellOut = null;
		
		try {
			Process process = Runtime.getRuntime().exec("/system/bin/sh");
		    shellIn = new DataOutputStream(process.getOutputStream());
		    shellOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
		    ShellStreamWorker outWorker = new ShellStreamWorker(shellOut);
		    
		    outWorker.start();
		    
	        shellIn.writeBytes(command + "\n");
	        
	        shellIn.writeBytes("exit\n");
	        
		    process.waitFor();
		    
		    String output = outWorker.result.toString();
		    
		    process.destroy();
		    
		    return output.toString();
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
