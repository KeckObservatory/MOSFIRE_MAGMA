package edu.ucla.astro.irlab.mosfire.mscgui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;
import edu.ucla.astro.irlab.mosfire.util.Timer;
import edu.ucla.astro.irlab.util.KJavaPropertyManager;
import edu.ucla.astro.irlab.util.Property;
import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.StringProperty;

/**
 * <p>Title: MSCGUIApplication</p>
 * <p>Description: Instantiates model and view, and displays view.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class MSCGUIApplication {
  boolean packFrame = false;
  MSCGUIModel myModel;
  PropertyList list = new PropertyList();
  KJavaPropertyManager kjavaPropertyManager;

  public MSCGUIApplication(String slitConfigurationFilename, boolean suppressDialog) throws Exception {
  	this(false);
  	File mscFile = new File(slitConfigurationFilename);
  	ArrayList<String> outWarningList = new ArrayList<String>();
  	myModel.openSlitConfiguration(mscFile, outWarningList);

  	int answer=JOptionPane.YES_OPTION;
  	if (!outWarningList.isEmpty()) {
  		if (suppressDialog) {
  			throw new Exception("Errors found in slit configuration file <"+slitConfigurationFilename+">");
  		} else {
	  		outWarningList.add(0, "The following problems were found parsing the Slit Configuration File:");
	  		outWarningList.add(1, slitConfigurationFilename);
	  		outWarningList.add(2, " ");
	  		outWarningList.add(" ");
	  		outWarningList.add("Execute mask anyway?");
	  		
	  		answer = JOptionPane.showConfirmDialog(null, outWarningList.toArray(new String[0]), "Slit Configuration Parsing Warnings", JOptionPane.YES_NO_OPTION); 
  		}
  	}
  	if (answer == JOptionPane.YES_OPTION) {
  		//. check CSU status
  		if (myModel.getCsuReady() != MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE) {
  			throw new Exception("CSU is not ready to move");
  		}
  		
  		myModel.executeMaskSetup(false);
  		//. wait for mask script to finish
  		int timeoutCounter=0;
  		int timeoutMax=50;  //. 5 seconds, should only take fractions
  		while (myModel.isScriptRunning()) {
  			Thread.currentThread().sleep(100);
  			timeoutCounter++;
  			if (timeoutCounter > timeoutMax) {
  				throw new Exception("Timeout setting up mask.");
  			}
  		}
  		timeoutCounter = 0;
  		timeoutMax = 60;  //. 30 seconds
  		//. wait for mask configuration to complete
  		while (myModel.getCsuReady() != MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE) {
  			Thread.currentThread().sleep(1000);
  			timeoutCounter++;
  			if (timeoutCounter > timeoutMax) {
  				throw new Exception("Timeout setting up mask.");
  			}  			
  		}
  		
  		//. now execute mask
  		myModel.executeMask();
  	}
  }
  //Construct the application
  public MSCGUIApplication(boolean showGUI) throws Exception {
  	if (MSCGUIParameters.ONLINE_MODE) {
      list.readXML(MSCGUIParameters.MOSFIRE_PROPERTIES_FILENAME);
  	}
    //. Instantiate model and view
    myModel = new MSCGUIModel(list, MSCGUIParameters.ONLINE_MODE);
    if (showGUI) {
    	MSCGUIView myView = new MSCGUIView(myModel);

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
  	if (MSCGUIParameters.ONLINE_MODE) {
    	kjavaPropertyManager = new KJavaPropertyManager(list, MSCGUIParameters.SERVER_NAME);
    	myModel.setKJavaPropertyManager(kjavaPropertyManager);
      list.registerExternalSetter(kjavaPropertyManager);
    	kjavaPropertyManager.start();
    	if (showGUI) {
    		ArrayList<Property> aliveProps = new ArrayList<Property>();
    		aliveProps.add(list.getProperty("MGSAlive"));
    		aliveProps.add(list.getProperty("MCSUSAlive"));
    		aliveProps.add(list.getProperty("MDSAlive"));
    		kjavaPropertyManager.startServerMonitor(aliveProps, MSCGUIParameters.PAUSE_MS_BETWEEN_INDIVIDUAL_SERVER_STATUS_POLLS, 10);
    	}
  	}

  }
  public void executeMask(String slitConfigurationFile) {
  	
  }
  
  public int runMascgen(String mascgenArgumentsFilename) throws Exception {
  	ArrayList<String> outWarningList = new ArrayList<String>();
  	MascgenArguments args = MascgenArguments.readMascgenParamFile(new File(mascgenArgumentsFilename), outWarningList);
  	int answer=JOptionPane.YES_OPTION;
  	if (!outWarningList.isEmpty()) {
  		outWarningList.add(0, "The following problems were found parsing the MASCGEN Arguments File:");
  		outWarningList.add(1, mascgenArgumentsFilename);
  		outWarningList.add(2, " ");
  		outWarningList.add(" ");
  		outWarningList.add("Run MASCGEN on this file anyway (Yes) or Skip (No)?");
  		
  		answer = JOptionPane.showConfirmDialog(null, outWarningList.toArray(new String[0]), "MascgenArgs Parsing Warnings", JOptionPane.YES_NO_OPTION); 
  	}
  	if (answer == JOptionPane.YES_OPTION) {
  		Timer timer = new Timer();
  		timer.start();
  		myModel.runMascgen(args);
  		myModel.writeCurrentSlitConfigurationOutputs(true);
  		timer.end();
  		timer.printTime();
  		return 0;
  	}
  	return -1;
  }
}
