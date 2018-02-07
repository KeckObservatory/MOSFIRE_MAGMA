/* Copyright (c) 2012, Regents of the University of California
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import edu.hawaii.keck.kjava.KJavaException;
import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;
import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.mosfire.util.Timer;
import edu.ucla.astro.irlab.util.KJavaPropertyManager;
import edu.ucla.astro.irlab.util.Property;
import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.StringProperty;
import edu.ucla.astro.irlab.util.gui.NonBlockingMessageDialog;

/**
 * Sets up and runs MAGMA in command-line mode or in
 * GUI-based mode, depending on constructor used. 
 * If run in online mode, this class is responsible
 * for the KJava connection to the KTL server it
 * connects to.
 * 
 * @author Jason L. Weiss, UCLA Infrared Laboratory
 */
public class MSCGUIApplication {
  private boolean packFrame = false;
  private MSCGUIModel myModel;
  private PropertyList list = new PropertyList();
  private KJavaPropertyManager kjavaPropertyManager;
  private JDialog disconnectedDialog;
  private MSCGUIView myView;
	
  /**
   * Constructor for executing a mask in command-line mode.
   * 
   * @param  slitConfigurationFilename  string to path of MSC file.  Can be in the form LONGSLIT-&lt;row&gt;x&lt;width&gt; for long slits.
   * @param  suppressDialog             If <code>suppressDialog</code> is set to 0, user is prompted with a dialog to handle warnings that come up during mask execution.
   * @throws Exception                  on any fatal errors that occur during mask execution
   */
  public MSCGUIApplication(String slitConfigurationFilename, boolean suppressDialog) throws Exception {
  	//. run in command-line mode
  	this(false);
	  try {
	  	//. check to see if slit config is a longslit
	  	//. must have no extension, and be of the form:
	  	//. LONGSLIT-<rows>x<width>
	  	//. where rows is number of rows in slit
	  	//. and width is slit width in arcsec
	  	boolean doLongslit = false;
  		ArrayList<String> outWarningList = new ArrayList<String>();
  		if (!slitConfigurationFilename.endsWith(".xml")) {
		  	if (slitConfigurationFilename.startsWith("LONGSLIT-")) {
		  		String sizeString = slitConfigurationFilename.substring(9);
		  		int xpos = sizeString.indexOf('x');
		  		if (xpos > 0) {

		  			//. parse rows and width and make sure they are in valid ranges
		  			int rows;
			  		double width;
			  		try {
			  			rows = Integer.parseInt(sizeString.substring(0,xpos));
			  			if ((rows <= 0) || (rows > MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS)) {
			  				throw new NumberFormatException("Invalid number of rows");
			  			}
			  			width = Double.parseDouble(sizeString.substring(xpos+1));
			  			if ((width < MosfireParameters.MINIMUM_SLIT_WIDTH) || (width > MosfireParameters.CSU_WIDTH)) {
			  				throw new NumberFormatException("Invalid width.");
			  			}
			  			
			  			//. all good, create and open new long slit config
			  			myModel.openLongSlitConfiguration(rows, width);
			  			doLongslit = true;
			  		} catch (NumberFormatException ex) {
			  			System.err.println("Error parsing longslit <"+slitConfigurationFilename+">.  Trying to open as MSC file.");
			  		}
		  		}
		  	}
  		}
  		
  		//. if not doing a long slit, try to open MSC file
	  	if (!doLongslit) {
	  		File mscFile = new File(slitConfigurationFilename);
	  		myModel.openSlitConfiguration(mscFile, outWarningList);
	  	}	
	  	
	  	//. default answer is to execute, but check for errors
	  	int answer=JOptionPane.YES_OPTION;

	  	//. if there are errors, ask user if suppress dialog is not set.  if it is, just log error to stderr
	  	if (!outWarningList.isEmpty()) {
	  		if (suppressDialog) {
	  			System.err.println("Errors found in slit configuration file <"+slitConfigurationFilename+">:\n");
	  			for (String s : outWarningList) {
	  				System.err.println(s);
	  			}
	  		} else {
		  		outWarningList.add(0, "The following problems were found parsing the Slit Configuration File:");
		  		outWarningList.add(1, slitConfigurationFilename);
		  		outWarningList.add(2, " ");
		  		outWarningList.add(" ");
		  		outWarningList.add("Execute mask anyway?");
		  		
		  		answer = JOptionPane.showConfirmDialog(null, outWarningList.toArray(new String[0]), "Slit Configuration Parsing Warnings", JOptionPane.YES_NO_OPTION); 
	  		}
	  	}
	  	
	  	//. if ok to execute
	  	if (answer == JOptionPane.YES_OPTION) {
	  		//. check CSU status
	  		if (myModel.getCsuReady() != MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE) {
	  			throw new Exception("CSU is not ready to move");
	  		}
	  		
	  		//. run mask setup script
	  		myModel.executeMaskSetup(false, false);
	  		
	  		//. wait for mask script to finish
	  		int timeoutCounter=0;
	  		int timeoutMax=50;  //. 5 seconds, should only take fractions
	  		while (myModel.isScriptRunning()) {
	  			Thread.sleep(100);
	  			timeoutCounter++;
	  			if (timeoutCounter > timeoutMax) {
	  				throw new Exception("Timeout setting up mask.");
	  			}
	  		}
	  		//. wait a second for setup to get started
	  		Thread.sleep(1000);
	  		
	  		timeoutCounter = 0;
	  		timeoutMax = 60;  //. 30 seconds
	  		//. wait for mask configuration to complete
	  		while (myModel.getCsuReady() != MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE) {
	  			Thread.sleep(1000);
	  			timeoutCounter++;
	  			if (timeoutCounter > timeoutMax) {
	  				throw new Exception("Timeout setting up mask.");
	  			}  			
	  		}
	  		
	  		//. now execute mask
	  		myModel.executeMask();
	  	}
  	} catch (Exception ex) {
  		//. forward any exceptions
  		throw ex;
  	} finally {
  		//. stop kjava on conclusion
  		if (kjavaPropertyManager != null) {
  			kjavaPropertyManager.stop();
  		}
  	}
  }

  /**
   * Main constructor for launching MAGMA.
   * Instantiates model and view (if desired) and
   * creates KJava connection in online mode.
   * 
   * @param  showGUI    flag for whether or not to display a GUI
   * @throws Exception  on all fatal errors constructing program
   */
  public MSCGUIApplication(boolean showGUI) throws Exception {
  	if (MSCGUIParameters.ONLINE_MODE) {
      list.readXML(MSCGUIParameters.MOSFIRE_PROPERTIES_FILENAME);
  	}
    //. Instantiate model and view
    myModel = new MSCGUIModel(list, MSCGUIParameters.ONLINE_MODE);
    if (showGUI) {
    	myView = new MSCGUIView(myModel);

    	//Validate frames that have preset sizes
    	//Pack frames that have useful preferred size info, e.g. from their layout
    	if (packFrame) {
    		myView.pack();
    	} else {
    		myView.validate();
    	}
    
    	//. set location
    	myView.setLocation(MSCGUIParameters.POINT_MAINFRAME_LOCATION);
    	//. show gui
    	myView.setVisible(true);
    }


    //. if online mode, set up KJava connection
  	if (MSCGUIParameters.ONLINE_MODE) {
  		//. last alive properties are used for monitoring connectionn to KJava
      StringProperty lastAliveProperty = (StringProperty)list.getProperty("MGSAlive");
    	lastAliveProperty.addPropertyChangeListener(new PropertyChangeListener() {
  			@Override
  			public void propertyChange(PropertyChangeEvent evt) {
  				lastAlivePropertyChange(evt.getNewValue().toString());
  			}
  		});
    	//. instantiate kjava
    	kjavaPropertyManager = new KJavaPropertyManager(list, MSCGUIParameters.SERVER_NAME);

    	//. register property mananger in model
    	myModel.setKJavaPropertyManager(kjavaPropertyManager);
    	
    	//. register kjava as external property setter in property list
    	//. used for modifying keywords
      list.registerExternalSetter(kjavaPropertyManager);

      //. connect to server
      try {
      	connectToServer();
      } catch (KJavaException kjEx) {
      	kjEx.printStackTrace();
      }
      
      //. if showing a GUI, set up server monitors
    	if (showGUI) {
    		ArrayList<Property> aliveProps = new ArrayList<Property>();
    		aliveProps.add(list.getProperty("MGSAlive"));
    		aliveProps.add(list.getProperty("MCSUSAlive"));
    		aliveProps.add(list.getProperty("MDSAlive"));
    		kjavaPropertyManager.startServerMonitor(lastAliveProperty, aliveProps, MSCGUIParameters.PAUSE_MS_BETWEEN_INDIVIDUAL_SERVER_STATUS_POLLS);
    	}
  	}

  }

  /**
   * Run MASCGEN, usually from command-line.
   * 
   * @param  mascgenArgumentsFilename  path to MASCGEN parameters file
   * @return                           0 if run successfully, -1 on error or abort in case of warnings parsing MASCGEN parameters file
   * @throws Exception                 on all fatal errors
   */
  public int runMascgen(String mascgenArgumentsFilename) throws Exception {
  	ArrayList<String> outWarningList = new ArrayList<String>();

  	//. parse parameters files
  	MascgenArguments args = MascgenArguments.readMascgenParamFile(new File(mascgenArgumentsFilename), outWarningList);
  	int answer=JOptionPane.YES_OPTION;

  	//. display warnings to user to confirm whether or not to proceed
  	if (!outWarningList.isEmpty()) {
  		outWarningList.add(0, "The following problems were found parsing the MASCGEN Arguments File:");
  		outWarningList.add(1, mascgenArgumentsFilename);
  		outWarningList.add(2, " ");
  		outWarningList.add(" ");
  		outWarningList.add("Run MASCGEN on this file anyway (Yes) or Skip (No)?");
  		
  		answer = JOptionPane.showConfirmDialog(null, outWarningList.toArray(new String[0]), "MascgenArgs Parsing Warnings", JOptionPane.YES_NO_OPTION); 
  	}
  	
  	//. if proceeding with errors, or no errors found, run mascgen
  	if (answer == JOptionPane.YES_OPTION) {
    	//. benchmark
  		Timer timer = new Timer();
  		timer.start();
  		myModel.runMascgen(args);
  		//. write out products based on parameters file
  		myModel.writeCurrentSlitConfigurationOutputs(true);
  		timer.end();
  		timer.printTime();
  		return 0;
  	}
  	return -1;
  }

  /**
   * Handle changed to the lastalive property, primarily to 
   * detect and handle disconnects from server.
   * 
   * @param value  new value for lastalive property
   */
	protected void lastAlivePropertyChange(String value) {
		//. if value contains ERROR, server is down.
		if (value.contains("ERROR")) {
			//. display message dialog.  instantiate if necessary
			if (disconnectedDialog == null ) {
				String[] message = {"Not connected to server.", 
					"Will automatically reconnect to server when it is available.",
					"This dialog will close when reconnected."};
			
					disconnectedDialog = NonBlockingMessageDialog.showMessageDialog(null, message, "KJava Error", JOptionPane.ERROR_MESSAGE);
			} else {
				if (!disconnectedDialog.isVisible()) {
					disconnectedDialog.setVisible(true);
				}
			}
		} else {
			//. hide disconnected dialog if instantiated and visible
			if (disconnectedDialog != null) {
				if (disconnectedDialog.isVisible()) {
					disconnectedDialog.setVisible(false);
				}
			}
		}
	}

	/**
	 * Attempt to connect to server by starting KJava connection.
	 * 
	 * @throws KJavaException  If error starting KJava connection.
	 */
	private void connectToServer() throws KJavaException {
		System.out.println("Connecting to server");
		//. note: start() does too things.
		//.   1) gets keywords from server
		//.   2) starts a cshow with these keywords
		//. when the cshow is started, if it doesn't connect, 
		//.  it will keep trying every three seconds.
		//. a stop() will kill the cshow and it's attempts 
		//. to reconnect
		kjavaPropertyManager.start();
	}
}
