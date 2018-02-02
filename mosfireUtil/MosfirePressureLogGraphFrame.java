package edu.ucla.astro.irlab.mosfire.util;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.graph.LogGraphPanel;

public class MosfirePressureLogGraphFrame extends JFrame {
	LogGraphPanel graphPanel;
	PropertyList propList;
	public MosfirePressureLogGraphFrame(PropertyList propList) {
		this.propList = propList;
		graphPanel = new LogGraphPanel(true);
		graphPanel.setValueAxisLabel("Pressure (mTorr)");
		graphPanel.setValueType("Pressure");
		this.getContentPane().add(graphPanel, BorderLayout.CENTER);

		String logDir = (String)propList.getProperty("PressureLogDirectory").getValue();
		String logFile = (String)propList.getProperty("PressureLogFile").getValue();
		//. get logdir from properties
		graphPanel.setDefaultLogFilePath(logDir);
			try {
				graphPanel.openLogFile(logDir+File.separator+logFile);
				graphPanel.startTail();
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "Error opening pressure log file" +logDir+File.separator+logFile+".", "Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		
	}
}
