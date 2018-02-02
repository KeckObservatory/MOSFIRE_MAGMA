package edu.ucla.astro.irlab.mosfire.mscgui;

import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.util.XmlToParams;

/**
 * <p>Title: MSCGUI</p>
 * <p>Description: Entry Point (Main Class) for MSCGUI</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class MSCGUI {
	private static final Logger logger = Logger.getLogger(MSCGUI.class);
	
	public MSCGUI() throws Exception {
		new MSCGUIApplication(true);
	}
	public MSCGUI(ArrayList<String> mascgenArgumentsFilenames) throws Exception {
		MSCGUIApplication app = new MSCGUIApplication(false);
		ArrayList<String> completedFiles = new ArrayList<String>();
		ArrayList<String> errorFiles = new ArrayList<String>();

		for (String mascgenArgumentsFilename : mascgenArgumentsFilenames) {
			try {
				System.out.println("Running MASCGEN on file <"+mascgenArgumentsFilename+">.");
				if (app.runMascgen(mascgenArgumentsFilename) == 0) {
					completedFiles.add(mascgenArgumentsFilename);
					System.out.println("MASCGEN for file <"+mascgenArgumentsFilename+"> complete.");
				} else {
					errorFiles.add(mascgenArgumentsFilename);
					System.out.println("MASCGEN skipped for file <"+mascgenArgumentsFilename+">.");
				}
			} catch (Exception e) {
				errorFiles.add(mascgenArgumentsFilename);
				String[] message = {"Error running mascgen on file: ", mascgenArgumentsFilename, "", e.getMessage()};
				//. TODO: mode with no dialogs
				JOptionPane.showMessageDialog(null, message, "Mascgen Error", JOptionPane.ERROR_MESSAGE);
				for (int ii=0; ii<message.length; ii++) {
					System.err.println(message[ii]);
				}
				e.printStackTrace();
			}
		}
		System.out.println("---------- MSCGUI batch job complete. ----------");
		System.out.println("Completed Files: ");
		for (String file : completedFiles) {
			System.out.println(" * " + file);
		}
		System.out.println("Files with errors: ");
		for (String file : errorFiles) {
			System.out.println(" - " + file);
		}
		System.exit(0);
	}
	public MSCGUI(String maskName, boolean suppressWarnings) throws Exception {
		MSCGUIApplication app = new MSCGUIApplication(maskName, suppressWarnings);
		System.exit(0);
	}

	//Main method
	public static void main(String[] args) {
		String cfgFilename="";
		boolean engineeringMode=false;
		boolean onlineMode=false;
		boolean classicMode=false;
		boolean onlineModePassedIn=false;
		boolean engineeringModePassedIn=false;
		ArrayList<String> mascgenArgumentsFilenames = new ArrayList<String>();
		String maskName = "";
		boolean executeMask = false;
		boolean ignoreWarnings = false;
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			//. arg[0] should be cfg=Config_Filename
			for (int ii=0; ii<args.length; ii++) {
				if (args[ii].startsWith("cfg=")) {
					cfgFilename=args[ii].substring(4, (args[ii].length()));
				} else if (args[ii].compareToIgnoreCase("-eng") == 0) {
					engineeringMode=true;
					engineeringModePassedIn = true;
				} else if (args[ii].compareToIgnoreCase("-online") == 0) {
					onlineMode=true;
					onlineModePassedIn = true;
				} else if (args[ii].compareToIgnoreCase("-executeMask") == 0) {
					executeMask = true;
					if (ii == (args.length-1)) {
						throw new IllegalArgumentException("excuteMask tag must be followed by a valid MSC filename and flag for whether or not to ignore MSC warnings.");
					}
					ii++;
					maskName = args[ii];
					if (ii == (args.length-1)) {
						throw new IllegalArgumentException("excuteMask tag must be followed by a valid MSC filename and flag for whether or not to ignore MSC warnings.");
					}
					ii++;
					if (args[ii].equals("1")) {
						ignoreWarnings = true;
					} else if (args[ii].equals("0")) {
						ignoreWarnings = false;
					} else {
						throw new IllegalArgumentException("Second arguement after -executeMask must be a 1 or 0 specifying whether or not to ignore warnings.");
					}
					//. must be in online mode
					onlineMode=true;
					onlineModePassedIn = true;
				} else {
					mascgenArgumentsFilenames.add(args[ii]);
				}
			}
			if (cfgFilename.length() > 0) {
				//. get config file
				java.io.File cfgFile = new java.io.File(cfgFilename);
				//. extract parameters from config file
				XmlToParams.extractParams(cfgFile, MSCGUIParameters.getInstance());
			}
			PropertyConfigurator.configure(MSCGUIParameters.LOG4J_CONFIG_FILENAME.getAbsolutePath());

			if (MSCGUIParameters.MOSFIRE_PARAMETERS_FILE.exists()) {
				XmlToParams.extractParams(MSCGUIParameters.MOSFIRE_PARAMETERS_FILE, MosfireParameters.getInstance());				
			}
			
			if (engineeringModePassedIn) {
				MSCGUIParameters.ENGINEERING_MODE = engineeringMode;
			}
			if (onlineModePassedIn) {
				MSCGUIParameters.ONLINE_MODE = onlineMode;
			}
			
			//. define menu fonts here in this way, so that setFont doesn't have
			//. to be called for each item.
			UIManager.put("Menu.font", MSCGUIParameters.FONT_MENU);
			UIManager.put("MenuItem.font", MSCGUIParameters.FONT_MENUITEM);

			//. launch application
			if (executeMask) {
				//. take precedence over mascgen
				new MSCGUI(maskName, ignoreWarnings);
			} else {
				if (mascgenArgumentsFilenames.isEmpty()) {
					new MSCGUI();
				} else {
					new MSCGUI(mascgenArgumentsFilenames);
				}
			}

		} catch (java.io.IOException ioE) {
			if (!ignoreWarnings) {
				JOptionPane.showMessageDialog(null, ioE.getMessage(), "MSCGUI Critical Error", JOptionPane.ERROR_MESSAGE);
			}
			ioE.printStackTrace();
			System.exit(-1);
		} catch (org.jdom.JDOMException jdE) {
			if (!ignoreWarnings) {
				JOptionPane.showMessageDialog(null, jdE.getMessage(), "MSCGUI Critical Error", JOptionPane.ERROR_MESSAGE);
			}
			jdE.printStackTrace();
			System.exit(-1);
		} catch(Exception e) {
			if (!ignoreWarnings) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "MSCGUI Critical Error", JOptionPane.ERROR_MESSAGE);
			}
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
