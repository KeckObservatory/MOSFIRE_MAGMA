package edu.ucla.astro.irlab.util.process;

public class ConsoleProcessListener implements ProcessListener {

  boolean showPID = false;
  boolean showCommand = false;
  boolean showCommandArgs = false;
 
  public ConsoleProcessListener() {
  	this(true, true, true);
  }
  public ConsoleProcessListener(boolean printPid, boolean printCommand, boolean printArgs) {
  	showPID = printPid;
  	showCommand = printCommand;
  	showCommandArgs = printArgs;
  }
  
	private String formatMessage(ProcessInfo process, String message) {
		StringBuffer output = new StringBuffer();
	
		if (showPID) {
			output.append("[");
			output.append(process.getIdNumber());
			output.append("] ");
		}
		
		if (showCommandArgs) {
			output.append(process.getCommandString());
			output.append("-> ");
		} else if (showCommand) {
			output.append(process.getCommand()[0]);
			output.append("-> ");		
		}
		
		output.append(message);
		
		return output.toString();
	}
	
  
	public void processErrMessage(ProcessInfo process, String message) {
		System.err.println(formatMessage(process, message));
	}

	public void processOutMessage(ProcessInfo process, String message) {
		System.out.println(formatMessage(process, message));
	}

	public void processExitCode(ProcessInfo process, int code) {
		String message = "Process has exited with code (" + code + ")";
		System.out.println(formatMessage(process, message));
	}

}
