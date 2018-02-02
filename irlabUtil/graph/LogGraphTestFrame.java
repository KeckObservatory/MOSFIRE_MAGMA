package edu.ucla.astro.irlab.util.graph;
/* 
 * This class is used for experimenting with JFreeChart 
 * It is not included in Makefile
 */

import java.awt.BorderLayout;
import javax.swing.JFrame;
import edu.ucla.astro.irlab.util.PropertyList;

public class LogGraphTestFrame extends JFrame {
	LogGraphPanel graphPanel;
	
	public LogGraphTestFrame() {
		this(null);
	}
	public LogGraphTestFrame(PropertyList propList) {
		graphPanel = new LogGraphPanel(true);
		graphPanel.setDefaultLogFilePath("/u/mosdev/kroot/kss/mosfire/gui/temp/");
		this.getContentPane().add(graphPanel, BorderLayout.CENTER);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LogGraphTestFrame f = new LogGraphTestFrame();
		f.pack();
		f.setVisible(true);
	}

}
