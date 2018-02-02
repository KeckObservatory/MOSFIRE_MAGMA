package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;

//. NOTE This class is unfinished, and not in Makefile

public class GUIConstructor extends JPanel {
	JTree guiTree = new JTree();
	JPanel componentPanel = new JPanel();
	JPanel previewPanel = new JPanel();
	JSplitPane treeSplitPane = new JSplitPane();
	JSplitPane splitPane = new JSplitPane();
	
	public GUIConstructor() {
		init();
	}
	
	public void init() {
		
		treeSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		treeSplitPane.setDividerLocation(0.3);
		treeSplitPane.setTopComponent(guiTree);
		treeSplitPane.setBottomComponent(splitPane);
		
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(0.3);
		splitPane.setTopComponent(componentPanel);
		splitPane.setBottomComponent(previewPanel);
		
//		this.setLayout(new GridBagLayout());
//		this.add(guiTree, new GridBagConstraints(0,0,1,1,4.0, 10.0, GridBagConstraints.WEST,
//				GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
//		this.add(splitPane, new GridBagConstraints(1,0,1,1,6.0, 10.0, GridBagConstraints.EAST,
//				GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
	
		this.setLayout(new BorderLayout());
		this.add(treeSplitPane, BorderLayout.CENTER);

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.setSize(600,600);
		
		frame.setContentPane(new GUIConstructor());
		
		frame.setVisible(true);
	}

	private class GUILayoutTreeModel {
		public GUILayoutTreeModel() {
			
		}
		
	}
	
}
