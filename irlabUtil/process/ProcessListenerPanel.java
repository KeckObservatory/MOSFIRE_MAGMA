package edu.ucla.astro.irlab.util.process;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//. TODO tabs for each process? or a list/table?
//. TODO filter by command ID or command name
//. TODO show PID, command, command args check boxes for message output.
//. TODO copy messages to stored list to that previous items can be restored to old messages
//. TODO color code messages according to command
//. TODO different format for err vs. out messages?
public class ProcessListenerPanel extends JPanel implements ProcessListener {
  /**
	 * 
	 */
	private static final long serialVersionUID = 5347734529871546191L;
	JScrollPane outputScrollPane = new JScrollPane();
  JTextArea outputText = new JTextArea();
  boolean autoscroll=true;
  boolean showPID = false;
  boolean showCommand = false;
  boolean showCommandArgs = false;
  
  final static Font FONT_PROCESS_DIALOG_OUTPUT_TEXT = new Font("Dialog", 0, 12);

	public ProcessListenerPanel() {
		jbInit();
	}
	
	private void jbInit() {
		setLayout(new BorderLayout());
		
    outputText.setLineWrap(true);
    outputText.setWrapStyleWord(true);
    outputText.setFont(FONT_PROCESS_DIALOG_OUTPUT_TEXT);

		outputScrollPane.getViewport().add(outputText);
		
		add(outputScrollPane, BorderLayout.CENTER);
		
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
    outputText.append(formatMessage(process, message)+"\n");
    if (autoscroll) {
      outputText.setCaretPosition(outputText.getDocument().getLength());
    }
	}

	public void processOutMessage(ProcessInfo process, String message) {
    outputText.append(formatMessage(process, message)+"\n");
    if (autoscroll) {
      outputText.setCaretPosition(outputText.getDocument().getLength());
    }
	}

	public void processExitCode(ProcessInfo process, int code) {
		String message = "Process has exited with code (" + code + ")";
    outputText.append(formatMessage(process, message)+"\n");
    if (autoscroll) {
      outputText.setCaretPosition(outputText.getDocument().getLength());
    }
	}

}
