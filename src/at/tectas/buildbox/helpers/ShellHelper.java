package at.tectas.buildbox.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellHelper {

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
	
	public static void executeSingleRootCommand(String command) {
		DataOutputStream shellIn = null;
		
		try {
			Process process = Runtime.getRuntime().exec("su -c 'system/bin/sh'");
			
		    shellIn = new DataOutputStream(process.getOutputStream());
		    
		    shellIn.writeBytes(command + "\n");
		    
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
	
	public static String executeSingleRootCommandWithSingleLineOutput(String command) {

		DataOutputStream shellIn = null;
		BufferedReader shellOut = null;
		
		try {
			Process process = Runtime.getRuntime().exec("su -c 'system/bin/sh'");
			
		    shellIn = new DataOutputStream(process.getOutputStream());
		    
		    shellIn.writeBytes(command + "\n");
		    
		    shellOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    
	        StringBuffer output = new StringBuffer();
	        	
	        output.append(shellOut.readLine());
	        
	        shellIn.writeBytes("exit\n");
	        
		    process.waitFor();
		    
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
	
	public static String executeSingleCommandWithOutput(String command) {
		
		DataOutputStream shellIn = null;
		BufferedReader shellOut = null;
		
		try {
			Process process = Runtime.getRuntime().exec(command);
		    shellIn = new DataOutputStream(process.getOutputStream());
		    shellOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    
	        StringBuffer output = new StringBuffer();
	        String line = "";
	        
	        while ((line = shellOut.readLine()) != null) {
	            output.append(line);
	        }
		    
		    process.waitFor();
		    
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
		return ShellHelper.executeSingleCommandWithOutput("getprop " + property);
	}
}
