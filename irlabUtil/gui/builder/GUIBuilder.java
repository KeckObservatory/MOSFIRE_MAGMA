package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.jdom.JDOMException;

import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.PropertySetterFrame;

public class GUIBuilder {
	//. constructorless -- all methods are static
	
	/**
	 * Creates GUI from specification with specified filename.
	 * 
	 * @param cfgFilename
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 * @throws  FileNotFoundException
	 */
	public static JFrame createGUI(String cfgFilename) throws FileNotFoundException, IOException, JDOMException , InvalidGUILayoutException{
		return createGUI(new File(cfgFilename));
	}
	/**
	 * Creates GUI from specification from specified file.
	 * 
	 * @param cfgFile
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	public static JFrame createGUI(File cfgFile) throws IOException, JDOMException, InvalidGUILayoutException {
		return createGUI(new GUILayout(cfgFile));
	}
	/**
	 * 
	 * @param layout
	 * @return
	 */
	public static JFrame createGUI(GUILayout layout) {
		JFrame frame = new JFrame();
		
		//. set location of frame
		frame.setLocation(layout.getScreenLocation());
		//. set default size of frame
		frame.setSize(layout.getScreenSize());
		
		//. set title of frame
		frame.setTitle(layout.getTitle());

		//. set the pane created by layout as the content pane of this new frame
		frame.setContentPane(createRootPane(layout));
		
		return frame;
	}
	public static JInternalFrame createInternalGUI(String cfgFilename) throws FileNotFoundException, IOException, JDOMException, InvalidGUILayoutException {
		return createInternalGUI(new File(cfgFilename));
	}
	public static JInternalFrame createInternalGUI(File cfgFile) throws IOException, JDOMException, InvalidGUILayoutException {
		return createInternalGUI(new GUILayout(cfgFile));
	}
	public static JInternalFrame createInternalGUI(GUILayout layout) {
		//. create new internal frame
		JInternalFrame frame = new JInternalFrame();
		
		//. set location of frame
		frame.setLocation(layout.getScreenLocation());
		//. set default size of frame
		frame.setSize(layout.getScreenSize());
		
		//. set title of frame
		frame.setTitle(layout.getTitle());

		//. set the pane created by layout as content pane of this new frame
		frame.setContentPane(createRootPane(layout));
		
		return frame;
	}
	
	
	private static Container createRootPane(GUILayout layout) {
		
		//. call recursive populatePanel method, putting contents into main container
		populatePanel(layout.getMainContainer());

		//. return container
		return layout.getMainContainer().getComponent();
		
	}
		
	private static void populatePanel(GUIContainer panel) {

		//. get components
		ArrayList<GUIComponent> componentList = panel.getComponents();

		//. for grid bag layout
		if (panel.getLayoutManager().equals(GUIContainer.LAYOUT_GRID_BAG)) {
			if (panel.getComponent() instanceof JPanel) {
				panel.getComponent().setLayout(new GridBagLayout());
			}
			Insets componentInsets = new Insets(panel.getPadY()/2, panel.getPadX()/2, panel.getPadY()/2, panel.getPadX()/2);
			for (GUIComponent component : componentList) {
				//. get GridBagConstraint for alignment
				int align=GridBagConstraints.CENTER;
				switch (component.getAlignment()) {
				case GUIComponent.ALIGNMENT_NORTHWEST: align=GridBagConstraints.NORTHWEST; break;
				case GUIComponent.ALIGNMENT_NORTH: align=GridBagConstraints.NORTH; break;
				case GUIComponent.ALIGNMENT_NORTHEAST: align=GridBagConstraints.NORTHEAST; break;
				case GUIComponent.ALIGNMENT_WEST: align=GridBagConstraints.WEST; break;
				case GUIComponent.ALIGNMENT_CENTER: align=GridBagConstraints.CENTER; break;
				case GUIComponent.ALIGNMENT_EAST: align=GridBagConstraints.EAST; break;
				case GUIComponent.ALIGNMENT_SOUTHWEST: align=GridBagConstraints.SOUTHWEST; break;
				case GUIComponent.ALIGNMENT_SOUTH: align=GridBagConstraints.SOUTH; break;
				case GUIComponent.ALIGNMENT_SOUTHEAST: align=GridBagConstraints.SOUTHEAST; break;
				}
				if (component instanceof SetterGUIWidget) {
					JPanel compPanel = new JPanel(new BorderLayout());
					compPanel.add(component.getComponent(), BorderLayout.CENTER);
					compPanel.add(((SetterGUIWidget)component).getSetButton(), BorderLayout.EAST);
					((Container)(panel.getComponent())).add(compPanel, new GridBagConstraints(component.getXGrid(), component.getYGrid(),
							component.getWidth(), component.getHeight(),1.0,0.0, align, GridBagConstraints.HORIZONTAL,componentInsets,0,0 ));
				} else {
					((Container)(panel.getComponent())).add(component.getComponent(), new GridBagConstraints(component.getXGrid(), component.getYGrid(),
						component.getWidth(), component.getHeight(),1.0,0.0, align, GridBagConstraints.HORIZONTAL,componentInsets,0,0 ));
					if (component instanceof GUIContainer) {
						populatePanel((GUIContainer)component);
					}
				}
			}
		} else if (panel.getLayoutManager().equals(GUIContainer.LAYOUT_GRID)) {
			//. for grid layout

			//. 
			if (panel.getComponent() instanceof JPanel)
				panel.getComponent().setLayout(new GridLayout(panel.getGridYSize(), panel.getGridXSize(), panel.getPadX(), panel.getPadY()));
			for (GUIComponent component : componentList) {
				if (component instanceof SetterGUIWidget) {
					JPanel compPanel = new JPanel(new BorderLayout());
					compPanel.add(component.getComponent(), BorderLayout.CENTER);
					compPanel.add(((SetterGUIWidget)component).getSetButton(), BorderLayout.EAST);
					((Container)(panel.getComponent())).add(compPanel);
				} else {
					((Container)(panel.getComponent())).add(component.getComponent());
					if (component instanceof GUIContainer) {
						populatePanel((GUIContainer)component);
					}
				}
			}
		}
	}

	public static void main(String args[]) {
		//. test entry point
		try {
			GUILayout layout = new GUILayout(new File(args[0]));
			JFrame frame = GUIBuilder.createGUI(layout);				
			frame.validate();
			frame.setVisible(true);
			
			PropertyList list = new PropertyList();
			list.readXML(new File(args[1]));
			
			PropertySetterFrame setFrame = new PropertySetterFrame(list);
			setFrame.setSize(new Dimension(300,80));
			setFrame.validate();
			setFrame.setVisible(true);
			
			layout.registerComponentsWithProperties(list);
			
			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
