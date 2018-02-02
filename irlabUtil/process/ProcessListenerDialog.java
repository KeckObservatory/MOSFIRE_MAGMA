package edu.ucla.astro.irlab.util.process;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: UCLA IRLAB Utility </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class ProcessListenerDialog extends JDialog implements ProcessListener {
  JPanel mainPanel = new JPanel();
  JPanel commandPanel = new JPanel();
  JPanel outputPanel = new JPanel();
  JPanel exitCodePanel = new JPanel();
  JLabel commandLabel = new JLabel();
  JLabel exLabel = new JLabel();
  JLabel outputLabel = new JLabel();
  JScrollPane outputScrollPane = new JScrollPane();
  JTextArea outputText = new JTextArea();
  JLabel exitLabel = new JLabel();
  JLabel exitCodeLabel = new JLabel();
  JButton closeButton = new JButton();
  String command;
  ProcessInfo process;
  boolean destroy = false;
  boolean autoscroll=true;
  final static Font FONT_PROCESS_DIALOG_COMMAND_TITLE = new Font("Dialog", Font.BOLD, 12);
  final static Font FONT_PROCESS_DIALOG_COMMAND_VALUE = new Font("Dialog", 0, 12);
  final static Font FONT_PROCESS_DIALOG_OUTPUT_TEXT = new Font("Dialog", 0, 12);
  final static Font FONT_PROCESS_DIALOG_OUTPUT_TITLE = new Font("Dialog", Font.BOLD, 12);
  final static Font FONT_PROCESS_DIALOG_EXITCODE_TITLE = new Font("Dialog", Font.BOLD, 12);
  final static Font FONT_PROCESS_DIALOG_EXITCODE_VALUE = new Font("Dialog", 0, 12);
  final static Font FONT_PROCESS_DIALOG_CLOSE_BUTTON = new Font("Dialog", Font.BOLD, 12);

  public ProcessListenerDialog(Frame owner, ProcessInfo process, boolean modal) {
    super(owner, process.getCommandString(), modal);
    this.process = process;
    this.command = process.getCommandString();
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  public ProcessListenerDialog(Dialog owner, ProcessInfo process, boolean modal) {
    super(owner, process.getCommandString(), modal);
    this.process = process;
    this.command = process.getCommandString();
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    exLabel.setText("Executing Command:");
    exLabel.setFont(FONT_PROCESS_DIALOG_COMMAND_TITLE);
    commandLabel.setText(command);
    commandLabel.setFont(FONT_PROCESS_DIALOG_COMMAND_VALUE);

    outputLabel.setText("Output:");
    outputLabel.setFont(FONT_PROCESS_DIALOG_OUTPUT_TITLE);
    outputText.setLineWrap(true);
    outputText.setWrapStyleWord(true);
    outputText.setFont(FONT_PROCESS_DIALOG_OUTPUT_TEXT);

    exitLabel.setText("Exit Code: ");
    exitLabel.setFont(FONT_PROCESS_DIALOG_EXITCODE_TITLE);
    exitCodeLabel.setText(" ");
    exitCodeLabel.setFont(FONT_PROCESS_DIALOG_EXITCODE_VALUE);

    closeButton.setText("Dismiss");
    closeButton.setFont(FONT_PROCESS_DIALOG_CLOSE_BUTTON);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });
    getContentPane().add(mainPanel);
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(commandPanel, BorderLayout.NORTH);
    mainPanel.add(outputPanel, BorderLayout.CENTER);
    mainPanel.add(closeButton, BorderLayout.SOUTH);

    commandPanel.setLayout(new BorderLayout());
    commandPanel.add(exLabel, BorderLayout.NORTH);
    commandPanel.add(commandLabel, BorderLayout.CENTER);

    outputPanel.setLayout(new BorderLayout());
    outputPanel.add(outputLabel, BorderLayout.NORTH);
    outputPanel.add(outputScrollPane, BorderLayout.CENTER);
    outputPanel.add(exitCodePanel, BorderLayout.SOUTH);

    exitCodePanel.setLayout(new BorderLayout());
    exitCodePanel.add(exitLabel, BorderLayout.WEST);
    exitCodePanel.add(exitCodeLabel, BorderLayout.CENTER);

    outputScrollPane.getViewport().add(outputText);
  }
  public void processErrMessage(ProcessInfo process, String message) {
    if (process.equals(this.process)) {
      outputText.append(message+"\n");
      if (autoscroll)
        outputText.setCaretPosition(outputText.getDocument().getLength());
    }
  }
  public void processOutMessage(ProcessInfo process, String message) {
    if (process.equals(this.process)) {
      outputText.append(message+"\n");
      if (autoscroll)
        outputText.setCaretPosition(outputText.getDocument().getLength());
    }
  }
  public void processExitCode(ProcessInfo process, int code) {
    if (process.equals(this.process)) {
      exitCodeLabel.setText(Integer.toString(code));
      destroy=true;
    }
  }
  void closeButton_actionPerformed(ActionEvent e) {
    if (destroy)
      this.dispose();
    else
      this.setVisible(false);
  }
  public void setCommand(String command) {
    this.command = command;
    commandLabel.setText(command);
  }
  public ProcessInfo getProcess() {
    return process;
  }
}