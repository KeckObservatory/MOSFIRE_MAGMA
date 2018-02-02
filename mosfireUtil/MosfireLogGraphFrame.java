package edu.ucla.astro.irlab.mosfire.util;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.graph.LogGraphPanel;

public class MosfireLogGraphFrame extends JFrame {
	LogGraphPanel graphPanel;
	PropertyList propList;
	public MosfireLogGraphFrame(PropertyList propList) {
		this.propList = propList;
		graphPanel = new LogGraphPanel(true);
		//. todo get logdir from properties
//		graphPanel.setDefaultLogFilePath(propList.getProperty(name));
		graphPanel.setDefaultLogFilePath("/u/mosdev/kroot/kss/mosfire/gui/temp/");
		this.getContentPane().add(graphPanel, BorderLayout.CENTER);
	}

}
