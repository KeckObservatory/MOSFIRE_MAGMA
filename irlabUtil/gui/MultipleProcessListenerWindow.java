package edu.ucla.astro.irlab.util.gui;


import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;

import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.process.ProcessListener;
import edu.ucla.astro.irlab.util.process.ProcessListenerPanel;

public class MultipleProcessListenerWindow extends ConvertibleFrame {

	ProcessListenerPanel plPanel = new ProcessListenerPanel();
	public MultipleProcessListenerWindow() {
		jbInit();
	}
  public MultipleProcessListenerWindow(JDesktopPane desktop) {
			super(desktop, "");
			jbInit();
  }
  private void jbInit() {
		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(plPanel, BorderLayout.CENTER);
		
	}
	public ProcessListener getProcessListener() {
		return plPanel;
	}

}
