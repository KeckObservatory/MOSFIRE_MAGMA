package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.hawaii.keck.kjava.KJavaException;
import edu.ucla.astro.irlab.util.KJavaPropertyManager;
import edu.ucla.astro.irlab.util.PropertyList;

/**
 * <p>Title: GUILauncher</p>
 * <p>Description: Entry Point (Main Class) for launching GUI constructed by GUIBuilder</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class GUILauncher {
	static String USAGE = "GUILauncher layoutFilename propertyListFilename [server=serverName] [x=x] [y=y] [xs=xs] [ys=ys]";
	boolean packFrame = false;
	PropertyList list = new PropertyList();
	KJavaPropertyManager kjavaPropManager;
	boolean connectedToServer=false;
	JFrame myView;
	
	public GUILauncher(File layoutFile, File propertiesFile, String serverName, Point location, int xs, int ys) throws Exception {

		list.readXML(propertiesFile);
		GUILayout layout = new GUILayout(layoutFile);


		layout.registerComponentsWithProperties(list);
		layout.registerComponentsWithSetter(list);

		myView = GUIBuilder.createGUI(layout);
		myView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frameClosed();
			}
		});
		
		//Validate frames that have preset sizes
		//Pack frames that have useful preferred size info, e.g. from their layout
		if (packFrame) {
			myView.pack();
		}
		else {
			myView.validate();
		}

		//. set location
		myView.setLocation(location);

		Dimension size = myView.getSize();
		
		if (xs > 0) {
			size.width = xs;
		}
		
		if (ys > 0) {
			size.height = ys;
		}

		//. set size
		myView.setSize(size);
		
		//. show gui
		myView.setVisible(true);

		//. get server name
		if (!serverName.isEmpty()) {
			kjavaPropManager = new KJavaPropertyManager(list, serverName);
			list.registerExternalSetter(kjavaPropManager);
			kjavaPropManager.start();
			connectedToServer = true;
		}
	}

	public void frameClosed() {
		int err=0;
		if (connectedToServer) {
			try {
				System.out.println("Stopping cshow.");
				kjavaPropManager.stop();
				System.out.println("Cshow stopped successfully.");
			} catch (KJavaException e) {
				JOptionPane.showMessageDialog(myView, "Error stopping KJava cshow.", "Error stopping CShow", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				err=-1;	
			}
		}
		System.out.println("Exiting.");
		System.exit(err);
	}
	
	//Main method
	public static void main(String[] args) {
		String cfgFilename="";
		String propListFilename="";
		String server = "";
		int xLoc = 0;
		int yLoc = 0;
		int xs=-1;
		int ys=-1;
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			for (int ii=0; ii<args.length; ii++) {
				if (args[ii].startsWith("server=")) {
					server=args[ii].substring(7, (args[ii].length()));
				} else if (args[ii].startsWith("x=")) {
					xLoc=Integer.parseInt(args[ii].substring(2, (args[ii].length())));
				} else if (args[ii].startsWith("y=")) {
					yLoc=Integer.parseInt(args[ii].substring(2, (args[ii].length())));
				} else if (args[ii].startsWith("xs=")) {
					xs=Integer.parseInt(args[ii].substring(3, (args[ii].length())));
				} else if (args[ii].startsWith("ys=")) {
					ys=Integer.parseInt(args[ii].substring(3, (args[ii].length())));
				} else {
					if (cfgFilename.isEmpty()) {
						cfgFilename = args[ii];
					} else {
						if (propListFilename.isEmpty()) {
							propListFilename = args[ii];
						} else {
							throw new Exception(USAGE);
						}
					}
				}
			}
			if (cfgFilename.isEmpty() || propListFilename.isEmpty()) {
				throw new Exception(USAGE);
			}
			new GUILauncher(new File(cfgFilename), new File(propListFilename), server, new Point(xLoc, yLoc), xs, ys);
			
		} catch (java.io.IOException ioE) {
			JOptionPane.showMessageDialog(null, ioE.getMessage(), "GUILauncher Critical Error", JOptionPane.ERROR_MESSAGE);
			ioE.printStackTrace();
			System.exit(-1);
		} catch (org.jdom.JDOMException jdE) {
			JOptionPane.showMessageDialog(null, jdE.getMessage(), "GUILauncher Critical Error", JOptionPane.ERROR_MESSAGE);
			jdE.printStackTrace();
			System.exit(-1);
		} catch (NumberFormatException nfEx) {
			JOptionPane.showMessageDialog(null, nfEx.getMessage(), "GUILauncher Critical Error", JOptionPane.ERROR_MESSAGE);
			nfEx.printStackTrace();
			System.exit(-1);
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "GUILauncher Critical Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
