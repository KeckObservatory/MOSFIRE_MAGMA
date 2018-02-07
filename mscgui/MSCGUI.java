/*
 * Copyright (c) 2012, Regents of the University of California
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for educational, research and non-profit purposes, without 
 * fee, and without a written agreement is hereby granted, provided that the 
 * above copyright notice, this paragraph and the following three paragraphs 
 * appear in all copies.
 * 
 * Permission to incorporate this software into commercial products may be 
 * obtained by contacting the University of California.
 * 
 *  Thomas J. Trappler, ASM
 *  Director, UCLA Software Licensing
 *  UCLA Office of Information Technology
 *  5611 Math Sciences
 *  Los Angeles, CA 90095-1557
 *  (310) 825-7516
 *  trappler@ats.ucla.edu
 *  
 *  This software program and documentation are copyrighted by The Regents of 
 *  the University of California. The software program and documentation are 
 *  supplied "as is", without any accompanying services from The Regents. The 
 *  Regents does not warrant that the operation of the program will be 
 *  uninterrupted or error-free. The end-user understands that the program was 
 *  developed for research purposes and is advised not to rely exclusively on 
 *  the program for any reason.
 *  
 *  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 *  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 *  LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS 
 *  DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE 
 *  POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY 
 *  DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE 
 *  SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF 
 *  CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 *  ENHANCEMENTS, OR MODIFICATIONS.
 */

package edu.ucla.astro.irlab.mosfire.mscgui;

import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.util.XmlToParams;

/**
 * This class is the entry point (main class for MAGMA (formerly MSCGUI).
 * It is responsible for parsing command line arguments, and then instantiating
 * a <Code>MSCGUIApplication</code> class for the appropriate mode, which can 
 * be two command line modes (execute a mask, or run MASCGEN) or a GUI-based mode
 * with online and engineering mode options.
 * 
 * @author Jason L. Weiss, UCLA Infrared Laboratory
 * @see    MSCGUIApplication
 */
public class MSCGUI {
	private static final Logger logger = Logger.getLogger(MSCGUI.class);
	
	/**
	 * Default constructor.  Instantiates GUI-based version of MAGMA.
	 * 
	 * @throws Exception  on all fatal errors from creating GUI
	 */
	public MSCGUI() throws Exception {
		new MSCGUIApplication(true);
	}
	
	/**
	 * Constructor for running MAGMA in command-line MASCGEN mode.
	 * 
	 * @param  mascgenArgumentsFilenames  ArrayList of MASCGEN parameter files to execute
	 * @throws Exception                  on all fatal errors running MASCGEN
	 */
	public MSCGUI(ArrayList<String> mascgenArgumentsFilenames) throws Exception {
		//. create application in command-line mode
		MSCGUIApplication app = new MSCGUIApplication(false);
		ArrayList<String> completedFiles = new ArrayList<String>();
		ArrayList<String> errorFiles = new ArrayList<String>();

		//. go through list of param files
		for (String mascgenArgumentsFilename : mascgenArgumentsFilenames) {
			try {
				System.out.println("Running MASCGEN on file <"+mascgenArgumentsFilename+">.");
				//. run mascgen on current configuration
				//. keep track of which ones were successful and which had errors or no solutions
				if (app.runMascgen(mascgenArgumentsFilename) == 0) {
					completedFiles.add(mascgenArgumentsFilename);
					System.out.println("MASCGEN for file <"+mascgenArgumentsFilename+"> complete.");
				} else {
					errorFiles.add(mascgenArgumentsFilename);
					System.out.println("MASCGEN skipped for file <"+mascgenArgumentsFilename+">.");
				}
			} catch (Exception e) {
				//. on errors, add to error list, and display dialog to user.
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
		
		//. display results
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
	
  /**
   * Launches MAGMA application in command-line mask execution mode.
   * 
	 * @param  maskName          filename to MSC file which is to be executed 
	 * @param  suppressWarnings  set to 0 to suppress warning dialogs.  
	 * @throws Exception         fatal error has occurred during command-line execution of mask.
	 */
	public MSCGUI(String maskName, boolean suppressWarnings) throws Exception {
		MSCGUIApplication app = new MSCGUIApplication(maskName, suppressWarnings);
		System.exit(0);
	}

	/**
	 * Main entry method for MAGMA.
	 * 
	 * Allowable arguments are:
   * <ul>
	 *   <li><code>cfg=&lt;config_file&gt;</code>: configuration file for program
	 *   <li><code>-eng</code>: run in engineering mode (script calls are simulated)
	 *   <li><code>-online</code>: run in online mode, where scripts can be run to execute mask
	 *   <li><code>-executeMask &lt;mscFile&gt; &lt;ignoreWarnings&gt;</code>: execute mask 
	 *              defined by MSC <code>mscFile</code>. If <code>ignoreWarnings</code> is set to 0, 
	 *              user is prompted with a dialog to handle warnings that come up during mask execution.
	 *   <li><code>mascgenParams1 [mascgenParams2 ...]</code>: A list of MASCGEN parameter files to 
	 *              run MASCGEN from command line.
	 *  
	 * @param args arguments passed in from command line
	 */
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
			//. Parse args
			for (int ii=0; ii<args.length; ii++) {
				if (args[ii].startsWith("cfg=")) {
					//. cfg=<config_file>.  next arg will be configuration file to configure MAGMA
					cfgFilename=args[ii].substring(4, (args[ii].length()));
				} else if (args[ii].compareToIgnoreCase("-eng") == 0) {
					//. engineering mode
					engineeringMode=true;
					engineeringModePassedIn = true;
				} else if (args[ii].compareToIgnoreCase("-online") == 0) {
					//. online mode
					onlineMode=true;
					onlineModePassedIn = true;
				} else if (args[ii].compareToIgnoreCase("-executeMask") == 0) {
					//. execute mask mode.  must be followed by MSC filename and flag if to ignore warnings.
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
					//. mascgen command line mode
					mascgenArgumentsFilenames.add(args[ii]);
				}
			}
			if (cfgFilename.length() > 0) {
				//. get config file
				java.io.File cfgFile = new java.io.File(cfgFilename);
				//. extract parameters from config file
				XmlToParams.extractParams(cfgFile, MSCGUIParameters.getInstance());
			}
			
			//. configure log4j
			PropertyConfigurator.configure(MSCGUIParameters.LOG4J_CONFIG_FILENAME.getAbsolutePath());

			//. extract parameteres from MOSFIRE parameters file
			if (MSCGUIParameters.MOSFIRE_PARAMETERS_FILE.exists()) {
				XmlToParams.extractParams(MSCGUIParameters.MOSFIRE_PARAMETERS_FILE, MosfireParameters.getInstance());				
			}
			
			//. set engineering mode, overriding config file if passed in
			if (engineeringModePassedIn) {
				MSCGUIParameters.ENGINEERING_MODE = engineeringMode;
			}

			//. set online mode, overriding config file if passed in
			if (onlineModePassedIn) {
				MSCGUIParameters.ONLINE_MODE = onlineMode;
			}
			
			//. define menu fonts here in this way, so that setFont doesn't have
			//. to be called for each item.
			UIManager.put("Menu.font", MSCGUIParameters.FONT_MENU);
			UIManager.put("MenuItem.font", MSCGUIParameters.FONT_MENUITEM);

			//. launch application
			if (executeMask) {
				//. execute mask takes precedence over mascgen
				new MSCGUI(maskName, ignoreWarnings);
			} else {
				//. if mascgen param files are passed in, run that version
				//. otherwise, run normal GUI based mode
				if (mascgenArgumentsFilenames.isEmpty()) {
					new MSCGUI();
				} else {
					new MSCGUI(mascgenArgumentsFilenames);
				}
			}

			//. catch errors and display dialog.
			//. these are all fatal errors.  exit after displaying error message.
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
