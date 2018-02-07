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

import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_FP_RADIUS;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_HEIGHT;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_WIDTH;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.STAR_EDGE_DISTANCE;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.xml.transform.TransformerException;

import nom.tam.fits.FitsException;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import edu.hawaii.keck.kjava.KJavaException;
import edu.ucla.astro.irlab.mosfire.util.AstroObj;
import edu.ucla.astro.irlab.mosfire.util.MascgenArgumentException;
import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;
import edu.ucla.astro.irlab.mosfire.util.MascgenResult;
import edu.ucla.astro.irlab.mosfire.util.MascgenTransforms;
import edu.ucla.astro.irlab.mosfire.util.MechanicalSlit;
import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.mosfire.util.RaDec;
import edu.ucla.astro.irlab.mosfire.util.SlitConfiguration;
import edu.ucla.astro.irlab.mosfire.util.SlitPosition;
import edu.ucla.astro.irlab.mosfire.util.TargetListFormatException;
import edu.ucla.astro.irlab.mosfire.util.TargetListParser;

import edu.ucla.astro.irlab.util.DoubleProperty;
import edu.ucla.astro.irlab.util.FileUtilities;
import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.KJavaPropertyManager;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;
import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.StringProperty;
import edu.ucla.astro.irlab.util.gui.GenericModel;
import edu.ucla.astro.irlab.util.process.ProcessControl;
import edu.ucla.astro.irlab.util.process.ProcessInfo;
import edu.ucla.astro.irlab.util.process.ProcessListener;


/**
 * Contains model logic for MAGMA (MSCGUI).
 * Responsible for handling state of program, including
 * current configuration, all loaded configurations, interfacing
 * with MASCGEN, and executing masks.
 * <p>
 * Logic for performing MASCGEN is in the <code>MascgenCore</code> class, and
 * logic for manipulating a single configuration, including writing
 * of data products, is in the <code>SlitConfiguration</code> class.
 * 
 * @author Jason L. Weiss, UCLA Infrared Laboratory
 * @see    MascgenCore
 * @see    edu.ucla.astro.irlab.mosfire.util.SlitConfiguration
 */
public class MSCGUIModel extends GenericModel implements PropertyChangeListener {
	private static final Logger logger = Logger.getLogger(MSCGUIModel.class);
	
	private SlitConfiguration currentSlitConfiguration = new SlitConfiguration();
	private ArrayList<SlitConfiguration> openedSlitConfigurations  = new ArrayList<SlitConfiguration>();
	private ArrayList<AstroObj> targetList = new ArrayList<AstroObj>();
	private int activeRow = -1;
	private MascgenCore mascgen = new MascgenCore();
	ProcessControl myProcessControl;
	private boolean scriptRunning;
	private long currentCommandProcessID;
	private MascgenResult currentMascgenResult;
	private int currentSlitConfigurationIndex;
	private File scriptDirectory = new File("");
	private String loadedMaskSetup="none";
	private String currentMaskName="unknown";
	private boolean lastMaskSetupIsAlign=false;
	private boolean online = false;
	private SimpleDateFormat scriptDatFormatter = new SimpleDateFormat("yyMMdd_HHmmss");
	private MascgenChangeListener mascgenListener = new MascgenChangeListener();
	MascgenRunThread mascgenThread;
	private int csuReady = 0;
	private String csuStatus = "";
	private StringProperty extFilenameProp;
	private PropertyList propertyList;
	private KJavaPropertyManager propertyManager;
	private double minimumCloseOffSlitWidth;
	private double closedOffSlitWidth;
	private int closeOffType;
	private boolean mascgenReassignUnusedSlits;
	private ArrayList<String> processOutputMessages = new ArrayList<String>();
	public static final int CLOSE_OFF_TYPE_DO_NOTHING = 1;
	public static final int CLOSE_OFF_TYPE_REDUCE_IN_PLACE = 2;
	public static final int CLOSE_OFF_TYPE_CLOSE_OFF = 3;
	
	/**
	 * Sole Constructor. 
	 * 
	 * @param  propList    PropertyList object containing list of properties used by MAGMA
	 * @param  online      flag for whether running in online mode
	 * @throws Exception   on all fatal errors, particularly with connecting with properties
	 */
	public MSCGUIModel(PropertyList propList, boolean online) throws Exception {
		propertyList = propList;
		currentSlitConfiguration.getMascgenArgs().setOutputDirectory(MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY.getCanonicalPath());
		currentMascgenResult = new MascgenResult();
		scriptRunning=false;
		myProcessControl = new ProcessControl();
		myProcessControl.addProcessListener(new MSCGUIProcessListener());
		currentSlitConfigurationIndex = -1;
		scriptDirectory = MSCGUIParameters.DEFAULT_EXECUTED_MASK_CONFIGURATION_DIRECTORY;
		minimumCloseOffSlitWidth = MSCGUIParameters.DEFAULT_MINIMUM_CLOSE_OFF_SLIT_WIDTH;
		closedOffSlitWidth = MSCGUIParameters.DEFAULT_CLOSED_OFF_SLIT_WIDTH;
		setCloseOffType(CLOSE_OFF_TYPE_REDUCE_IN_PLACE);
		mascgenReassignUnusedSlits = MSCGUIParameters.REASSIGN_UNUSED_SLITS;
		this.setOnline(online);
		if (online) {
			registerWithProperties();
			initializeValuesFromProperties();
			extFilenameProp = (StringProperty)propertyList.getProperty("CSUFitsExtensionFilename");
			if (extFilenameProp == null) {
				throw new NoSuchPropertyException("CSUFitsExtensionFilename property is not found and is required.");
			}
			
		}
	}
	
	/**
	 * Accessor method to return MOSFIRE Global Server last alive property from property list.
	 * 
	 * @return  StringProperty for MOSFIRE server last alive
	 */
	public StringProperty getMosfireLastAliveProperty() {
		StringProperty prop = (StringProperty)propertyList.getProperty("MGSAlive");
		return prop;
	}
	/**
	 * Accessor method to return MOSFIRE Detector Server last alive property from property list.
	 * 
	 * @return  StringProperty for MDS server last alive
	 */
	public StringProperty getMDSLastAliveProperty() {
		StringProperty prop = (StringProperty)propertyList.getProperty("MDSAlive");
		return prop;
	}
	/**
	 * Accessor method to return MOSFIRE CSU Server last alive property from property list.
	 * 
	 * @return  StringProperty for MCSUS server last alive
	 */
	public StringProperty getMCSUSLastAliveProperty() {
		StringProperty prop = (StringProperty)propertyList.getProperty("MCSUSAlive");
		return prop;
	}
	
	/**
	 * Abort MASCGEN if running.
	 */
	public void abortMascgen() {
		//. If mascgen is running, signal to abort.
		if (mascgenThread != null) {
			if (mascgenThread.isAlive()) {
				mascgen.abort();
  		}
  	}
		//. notify user via exception property
  	setMascgenArgumentException(new MascgenArgumentException("MASCGEN was aborted by user."));
  }
	
  /**
   * Start MASCGEN based on passed in arguments.
   * 
   * @param  args                  MascgenArguments object specifying parameters for MASCGEN
   * @throws FileNotFoundException if target list in <code>args</code> cannot be opened
   */
  public void startMascgen(MascgenArguments args) throws FileNotFoundException {
  	//. validate target list
  	new RandomAccessFile(new File(args.getTargetList()),"r");
  	//. start mascgen thread
  	mascgenThread = new MascgenRunThread(args);
  	mascgenThread.start();
  }
  /**
   * Start MASCGEN with specified arguments.
   * Normally, this function is called by the MascgenRunThread inner class.
   * However, it is called directly in command-line mode.
   * <p>
   * If the target list specified in the arguments starts with a object named
   * "CENTER" with a priority of 9999, it is removed prior to executing MASCGEN.
   * This is for compatibility with older Keck instrument slit mask creation
   * software.
   * <p> 
   * When MASCGEN completes, if it found a solution, a SlitConfiguration object is 
   * created from the solution, and set as the current configuration.  If it did
   * not find a solution, an exception is raised that explains why.
   * 
   * @param  args                      MascgenArguments object that specifies parameters for MASCGEN 
   * @throws MascgenArgumentException  if MASCGEN cannot be run using <code>args</code>
   */
  	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	// * This method is heavily modified to keep set number of top  //
	//	 configurations. please refer to comment starts with JMS    //
	//////////////////////////////////////////////////////////////////
  public void runMascgen(MascgenArguments args) throws MascgenArgumentException {
		try {
			//. read in target list
			mascgen.setMascgenStatus("Reading target list", mascgenListener);
			ArrayList<AstroObj> astroObjArrayList = TargetListParser.parseFile(args.getTargetList());
			
			// remove a center line
			// must be first line in the file, name = CENTER, Priority=9999
			if (astroObjArrayList.get(0).getObjName() == "CENTER" && 
					astroObjArrayList.get(0).getObjPriority() == 9999){
				astroObjArrayList.remove(0);
			}
			
		//. run mascgen
	  	List<MascgenResult> resultList = mascgen.run(astroObjArrayList, args, mascgenListener);
	  	int index=1;
	  	for(MascgenResult result : resultList){
	  		//. if valid result found, construct SlitConfiguration from result
		  	//. add configuration to list of opened configs, and set as current
		  	if (result.getAstroObjects().length > 0) {
		  		//JMS : Copy the arguments to avoid future alteration
		  		SlitConfiguration newConfig = SlitConfiguration.generateSlitConfiguration(args, result, mascgenReassignUnusedSlits);
		  		newConfig.setOriginalTargetSet(astroObjArrayList);
		  		newConfig.updateOriginalAstroObjects();
		  		//TEST PURPOSE : JMS
		  		int count = 1;
		  		for (AstroObj obj : astroObjArrayList){
		  			System.out.printf("%3d\t%10s\t%+5f\t%+3d\t%+3d\t%+5f\t%+5f\n", count, obj.getObjName(), obj.getObjPriority(),obj.getMinRow(), obj.getMaxRow(),obj.getObjX(),obj.getObjY());
		  			count++;
		  		}
		  		newConfig.verifyObjectAvoidance();
		  		//TEST PURPOSE : JMS
		  		System.out.println("\n\nAvoid\n\n");
		  		count = 1;
		  		for (AstroObj obj : newConfig.getAvoidTargetList()){
		  			System.out.printf("%3d\t%10s\t%+5f\t%+3d\t%+3d\t%+5f\t%+5f\n", count, obj.getObjName(), obj.getObjPriority(),obj.getMinRow(), obj.getMaxRow(),obj.getObjX(),obj.getObjY());
		  			count++;
		  		}
		  		newConfig.setMaskName(args.getMaskName()+index);
		  		addSlitConfiguration(newConfig);
		  		setCurrentSlitConfiguration(newConfig);
		  		ArrayList<AstroObj> tempList = new ArrayList<AstroObj>();
		  		for(AstroObj obj : astroObjArrayList){
		  			tempList.add(obj.getCleanAstroObj());
				}
		  		astroObjArrayList = tempList;
		  		
		  	} else {
		  		//. otherwise, throw exception with reason why result wasn't found (if not enough alignment stars)
		  		if (result.getLegalAlignmentStars().length < args.getMinimumAlignmentStars()) {
		  			if (result.getLegalAlignmentStars().length == 0) {
			  			throw new MascgenArgumentException("Cannot find solution with "+args.getMinimumAlignmentStars()+" alignment stars. No valid alignment stars found.");
		  			} else {
		  				throw new MascgenArgumentException("Cannot find solution with "+args.getMinimumAlignmentStars()+" alignment stars.  Max of "+result.getLegalAlignmentStars().length+" stars found at "+result.getCenter().toStringWithUnits()+", pa="+result.getPositionAngle()+".");
		  			}
		  		} else {
		  			throw new MascgenArgumentException("No valid configuration found.  Alignment stars were found, but no objects.");
		  		}
		  	}
		  	index++;
	  	}
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			throw new MascgenArgumentException("Error parsing target list: " + ex.getMessage());
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			throw new MascgenArgumentException("Error opening target list: " + ex.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new MascgenArgumentException("Error reading target list: " + ex.getMessage());
		} catch (TargetListFormatException ex) {
			ex.printStackTrace();
			throw new MascgenArgumentException("Error parsing target list: " + ex.getMessage());
		}
		setCurrentSlitConfigurationIndex(getSlitConfigurationIndex(args.getMaskName()+1));
  }
  /**
   * Construct a new Long Slit configuration, add it to list of opened configurations, and set as current.
   * 
   * @param slitLengthArcsec double value specifying length of slit in arcsec
   * @param slitWidth        double value specifying width of slit in arcsec
   */
  public void openLongSlitConfiguration(double slitLengthArcsec, double slitWidth) {
  	//. convert slit length to number of rows
  	int length = (int)Math.ceil(((slitLengthArcsec - MosfireParameters.SINGLE_SLIT_HEIGHT) / MosfireParameters.CSU_ROW_HEIGHT));
  	//. call overloaded method
  	openLongSlitConfiguration(length, slitWidth);
  	
  }
  
  /**
   * Construct a new Long Slit configuration, add it to list of opened configurations, and set as current.
   * 
   * @param numberOfBars  length of the slit in number of rows
   * @param slitWidth     double value specifying width of the slit in arcsec
   */
  public void openLongSlitConfiguration(int numberOfBars, double slitWidth) {
  	//. construct new config
  	SlitConfiguration config = SlitConfiguration.createLongSlitConfiguration(numberOfBars, slitWidth);
  	//. add it to list of opened configs
  	addSlitConfiguration(config);
  	//. set as current
  	setCurrentSlitConfiguration(config);
  }

  /**
   * Construct a new Open Mask configuration, add it to the list of opened configurations, and set as current
   */
  public void openOpenMaskSlitConfiguration() {
  	//. construct new config
  	SlitConfiguration config = SlitConfiguration.createOpenMaskSlitConfiguration();
  	//. add it to the list of opened configs
  	addSlitConfiguration(config);
  	//. set as current
  	setCurrentSlitConfiguration(config);
  }
  
  /**
   * Open a slit configuration from an MSC file.
   * Warnings found parsing MSC file are added to the <code>outWarningList</code> 
   * argument, which must be instantiated before passing in.  The configuration
   * is added to the list of opened configurations and set to the current configuration.
   * <p>
   * If the MSC is successfully read, this method will attempt to open the 
   * original target list in order to display targets.  In online mode, 
   * the MSC and associated files may not be in the same directory as they
   * were when created (for example, when copying the configuration to the 
   * observatory machine for on-the-sky use), so in this case, this method
   * attempts to set the parent directory based on its new location, thus
   * helping it to find the original target list that is a normal output
   * product of MASCGEN.
   * 
   * @param  mscFile         File object specifying MSC file to open
   * @param  outWarningList  String ArrayList to append warnings found when parsing MSC file
   * @throws JDOMException   if fatal error parsing MSC file
   * @throws IOException     if error opening or reading MSC file
   */
  public void openSlitConfiguration(File mscFile, ArrayList<String> outWarningList) throws JDOMException, IOException  {
  	SlitConfiguration newConfig = new SlitConfiguration();
  	//. read msc file
  	newConfig.readSlitConfiguration(mscFile, outWarningList);
  	
  	//. add warning if version mismatch, if warning for this is enabled
  	if (!MSCGUIParameters.SUPPRESS_VERSION_MISMATCH_WARNINGS) {
  		if (!newConfig.getVersion().equals(MSCGUIParameters.MSC_VERSION)) {
  			outWarningList.add("Version of MSC read <"+newConfig.getVersion()+"> does not match current version <"+MSCGUIParameters.MSC_VERSION+">.");
  		}
  	}

  	//. in online mode, the msc file might not be in the same directory it was created
  	//. overwrite output subdirectory 
  	if (MSCGUIParameters.ONLINE_MODE) {
  		//. replace output directory with parent of mscFile
  		String newOutdir;
  		File mscParent = mscFile.getParentFile();
  		//. check the highest level directory to see if it is mask name
  		String[] parentDirs = FileUtilities.getDirectoryTree(mscParent);
  		//. if so, make sure that option is set properly
  		newConfig.getMascgenArgs().setOutputSubdirectoryMaskName(parentDirs[parentDirs.length-1].equals(newConfig.getMaskName()));

  		//. if using mask name for subdir, make the new root output dir its parent
  		//. otherwise, us it, and set subdir to empty string
  		if (newConfig.getMascgenArgs().isOutputSubdirectoryMaskName()) {
  			newOutdir = mscParent.getParent();
  		} else {
  			newOutdir = mscParent.toString();
  			newConfig.getMascgenArgs().setOutputSubdirectory("");
  		}
  		logger.debug("New configuration output directory is "+newOutdir);
  		newConfig.getMascgenArgs().setOutputDirectory(newOutdir);
  	}
  	
  	//. any errors below are just warnings, since we can still work okay without original target list
  	//. add warning messages based on exception messages to the out arraylist
		try {
			//. open original target list
			ArrayList<AstroObj> astroObjArrayList = TargetListParser.parseFile(newConfig.getMascgenArgs().getFullPathOutputAllTargets());
			
			// remove a center line
			// must be first line in the file, name = CENTER, Priority=9999
			if (astroObjArrayList.get(0).getObjName() == "CENTER" && 
					astroObjArrayList.get(0).getObjPriority() == 9999){
				astroObjArrayList.remove(0);
			}
			
			//. set targets in config, and update them with proper configuration values
			newConfig.setOriginalTargetSet(astroObjArrayList);
			newConfig.updateOriginalAstroObjects();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			outWarningList.add("Error parsing original target list: " + ex.getMessage());
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			outWarningList.add("Error opening original target list: " + ex.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
			outWarningList.add("Error reading original target list: " + ex.getMessage());
		} catch (TargetListFormatException ex) {
			ex.printStackTrace();
			outWarningList.add("Error parsing original target list: " + ex.getMessage());
		}
		
		//. add to opened config list
  	addSlitConfiguration(newConfig);
  	//. set as current
		setCurrentSlitConfiguration(newConfig);  	
  }
  
  /**
   * Remove a configuration from the list of opened configurations.
   * Current configuration is set as item above the one removed, unless
   * it is the first item, in which it is set as the one below.
   * 
   * @param index  index in list of opened configurations of configuration to remove
   */
  public void closeSlitConfiguration(int index) {
  	openedSlitConfigurations.remove(index);
		propertyChangeListeners.firePropertyChange("openedSlitConfigurations", null, openedSlitConfigurations);	

		//. set new current
		if (index == openedSlitConfigurations.size()) {
			setCurrentSlitConfigurationIndex(index-1);
		} else {
			setCurrentSlitConfigurationIndex(index);			
		}
  }
  
  /**
   * Make a copy of the current configuration and add to list of opened configurations.
   * Configuration name of copy will be the original name concatenated with "_" and
   * the lowest positive integer such that the name is unique among opened configurations.
   * 
   * @param index  index in the list of opened configurations of configuration to copy
   */
  public void copySlitConfiguration(int index) {
  	//. copy configuration
  	SlitConfiguration newConfiguration = currentSlitConfiguration.clone();

  	//. new name is oldName+_+ii, but don't allow duplicates.  increment ii 
  	//. until unique name is found.
  	int ii=1;
  	//. getSlitConfigurationIndex returns -1 if no configuration can be found 
  	//. within opened configs
  	while (getSlitConfigurationIndex(newConfiguration.getMaskName()) != -1) {
  		newConfiguration.setMaskName(currentSlitConfiguration.getMaskName()+"_"+ii);
  		ii++;
  	}
  	//. add to list of opened configs
  	addSlitConfiguration(newConfiguration);
  }
  
  /**
   * Add slit configuration to list of opened configurations.
   * 
   * @param config  SlitConfiguration to add
   */
  private void addSlitConfiguration(SlitConfiguration config) {
  	//. check to see if configuration is already added
  	int index = getSlitConfigurationIndex(config.getMaskName());

  	//. if not, add it, and set index to it (at the end)
  	if (index < 0) {
  		openedSlitConfigurations.add(config);
  		index = openedSlitConfigurations.size()-1;
  	} else {
  		//. if config with same name is already opened, replace it
  		openedSlitConfigurations.set(index, config);
  	}
		propertyChangeListeners.firePropertyChange("openedSlitConfigurations", null, openedSlitConfigurations);	

		//. set as current
		setCurrentSlitConfigurationIndex(index);
  }
  
  /**
   * Get index of configuration in list of opened configurations based on mask name.
   * 
   * @param  maskName  String name of configuration to search for
   * @return           index of configuration matching <code>maskName</code> 
   */
  public int getSlitConfigurationIndex(String maskName) {
  	int index=-1;
  	int currentCounter = 0;
  	for (SlitConfiguration config : openedSlitConfigurations) {
  		if (config.getMaskName().equals(maskName)) {
  			index = currentCounter;
  			break;
  		}
  		currentCounter++;
  	}
  	return index;
  }
  
  /**
   * Sets the current configuration to the one matching indexed position in opened configuration list.
   * If the index is negative (when list is empty), the current configuration is set to a default configuration.
   * 
   * @param currentSlitConfigurationIndex  Index of configuration in opened configuration list to make current
   */
  public void setCurrentSlitConfigurationIndex(int currentSlitConfigurationIndex) {
  	int oldValue = this.currentSlitConfigurationIndex;
		this.currentSlitConfigurationIndex = currentSlitConfigurationIndex;
		propertyChangeListeners.firePropertyChange("currentSlitConfigurationIndex",
				oldValue, currentSlitConfigurationIndex);

		if (currentSlitConfigurationIndex < 0) {
			setCurrentSlitConfiguration(new SlitConfiguration());
		} else {
			setCurrentSlitConfiguration(openedSlitConfigurations.get(currentSlitConfigurationIndex));
		}
  }
  public int getCurrentSlitConfigurationIndex() {
		return currentSlitConfigurationIndex;
	}

	public ArrayList<SlitConfiguration> getOpenedSlitConfigurations() {
  	return openedSlitConfigurations;
  }
	
	
	/**
	 * Returns whether any of the opened configurations are unsaved.
	 * This includes new and modified configurations.  Unsaveable configurations
	 * (long slits and open masks) are ignored.
	 * 
	 * @return  boolean specifying if any configurations are unsaved
	 */
	public boolean hasUnsavedSlitConfigurationsOpened() {
		//. loop through opened configurations and
		//. return true is any of them are not saved or unsaveable.
		for (SlitConfiguration config : openedSlitConfigurations) {
			if (!config.getStatus().equals(SlitConfiguration.STATUS_SAVED) && !config.getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				return true;
			}
		}
		//. no?  then no unsaved configs
		return false;
	}
	
	
  /**
   * Write all data products associated with current configuration.
   * This includes:
   * <ul>
   *   <li>MASCGEN parameters file
   *   <li>Target list for targets in mask
   *   <li>Target list for all original targets
   *   <li>MOSFIRE Slit Configuration (MSC) file
   *   <li>Description of slits
   *   <li>Script to execute science mask
   *   <li>Script to execute alignment mask (if there are alignment stars in configuration)
   *   <li>DS9 regions file
   *   <li>Keck formatted star list for mask pointing
   *   <li>(Optional) HTML version of configuration
   * </ul>
   * Data products are written to files according to values specified in current SlitConfiguration object.
   * 
   * @param  writeHTML            boolean flag for whether to write the HTML version of configuration
   * @throws JDOMException        if error constructing XML for MSC or MASCGEN parameters file
   * @throws IOException          if error writing file
   * @throws TransformerException if error constructing HTML from MSC file 
   */
  public void writeCurrentSlitConfigurationOutputs(boolean writeHTML) throws JDOMException, IOException, TransformerException {
  	
  	currentSlitConfiguration.writeMascgenParams();
  	currentSlitConfiguration.writeCoordsFile();
  	currentSlitConfiguration.writeOrigCoordsFile();
  	currentSlitConfiguration.writeSlitConfiguration(MSCGUIParameters.MSC_VERSION);
  	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
	//////////////////////////////////////////////////////////////////
  	currentSlitConfiguration.writeExcessCoordsFile();
  	
  	if (writeHTML) {
  		currentSlitConfiguration.writeSlitConfigurationHTML();
  	}
  	currentSlitConfiguration.writeOutSlitList();
  	currentSlitConfiguration.writeScienceCSUScript(false);
  	currentSlitConfiguration.writeOutStarList();
  	currentSlitConfiguration.writeDS9Regions();

  	//. only write alignment script if there are alignment stars
  	if (currentSlitConfiguration.getAlignmentStarCount() > 0) {
  		currentSlitConfiguration.writeAlignmentCSUScript(false);
  	}
  }
  
  /**
   * Open target list file as set as current targets.
   * 
   * @param  targetListFile            File object for target list
   * @throws FileNotFoundException     if file not found
   * @throws IOException               on other error opening file
   * @throws NumberFormatException     if target list cannot be parsed correctly
   * @throws TargetListFormatException if target list cannot be parsed correctly
   */
  public void openTargetList(File targetListFile) throws FileNotFoundException, IOException, NumberFormatException, TargetListFormatException {
  	targetList = TargetListParser.parseFile(targetListFile);
  }  
	public ArrayList<AstroObj> getTargetList() {
  	return targetList;
  }

	/**
	 * Remove all targets from current target list.
	 */
	public void clearTargetList() {
  	targetList = new ArrayList<AstroObj>();
  }
  
  /**
   * Construct filename string based on current mask name.
   * Path for script is the executed mask directory.
   * Name is modified if it is an alignment mask, or if it is a script
   * to move unused bars.
   * 
   * @param isAlign    boolean flag for if this is an alignment mask
   * @param isExtra    boolean flag for if this is a mask to move unused bars
   * @param extension  String giving file extension of script
   * @return           String giving filename of script
   */
  private String constructMaskScriptFilename(boolean isAlign, boolean isExtra, String extension) {
  	//. format is dir/YYMMDD_HHMMSS_maskName(_align)(_extra).csh
  	StringBuffer buffer = new StringBuffer(scriptDirectory.getAbsolutePath()+File.separator);
  	buffer.append(scriptDatFormatter.format(Calendar.getInstance().getTime()));
  	buffer.append("_");
  	//. remove /, [, ], (, and ) from filename.
  	//. cfitsio cannot open files with these chars
  	String modifiedMaskName = currentSlitConfiguration.getMaskName().replaceAll("[\\(\\)\\[\\]/]", "_");
  	buffer.append(modifiedMaskName);

  	//. append _align if it is an alignment mask
  	if (isAlign) {
  		buffer.append("_align");
  	}
  	//. append _extra if it is for unused bars
  	if (isExtra) {
  		buffer.append("_extra");
  	}
  	buffer.append(".");
  	buffer.append(extension);
  	return buffer.toString();
  }
  
  public void writeLongSlitConfigurationScript(SlitConfiguration config) throws FileNotFoundException {
  	String scriptFilename = constructMaskScriptFilename(false, false, "csh");
  	config.writeScienceCSUScript(scriptFilename, false);
		File scriptFile = new File(scriptFilename);
		//. make file world executable
		scriptFile.setExecutable(true, false);
  }
  
	/**
	 * Initiate a mask setup.
	 * This method writes a script to the executed masks directory
	 * that specifies target positions for the current mask, and starts
	 * a CSU setup, which downloads bar positions to the CSU controller.
	 * The setup script is then executed.  After completion, if the 
	 * <code>doMira</code> is set, the MIRA setup script is then run.
	 * 
	 * @param  doAlign               boolean flag for whether or not to execute alignment version of mask
	 * @param  doMira                boolean flag for whether the MIRA setup script should be run after mask setup
	 * @throws IOException           on errors writing script
	 * @throws InterruptedException  if script execution monitoring is interrupted.
	 */
	public void executeMaskSetup(boolean doAlign, boolean doMira) throws IOException, InterruptedException {
		setScriptRunning(true);
		//. make sure a mask is configured
		try {
			//. check directory existence
			if (scriptDirectory.exists()) {
				if (scriptDirectory.canWrite()) {
					String scriptFilename;
					File scriptFile;
					
					//. check if something needs to be done with bars not used by mask
					if (closeOffType != CLOSE_OFF_TYPE_DO_NOTHING) {
						//. move unused bars if necessary
						SlitConfiguration unusedBarsConfig = constructExtraBarsConfiguration(doAlign);
						if (!unusedBarsConfig.getMechanicalSlitList().isEmpty()) {
							scriptFilename = constructMaskScriptFilename(doAlign, true, "csh");
							if (doAlign) {
								unusedBarsConfig.writeAlignmentCSUScript(scriptFilename, true);
							} else {
								unusedBarsConfig.writeScienceCSUScript(scriptFilename, true);
							}
							scriptFile = new File(scriptFilename);
							//. make file executable?
							scriptFile.setExecutable(true, false);
							runMaskScript(scriptFilename);
							//. sleep a little to give time to finish
							//. note: unused bar script does not do setupinit
							//. it only sets target position keywords, which is quick
							Thread.sleep(1000);
						}
					}

					//. now do mask setup.
					scriptFilename = constructMaskScriptFilename(doAlign, false, "csh");
					if (doAlign) {
						currentSlitConfiguration.writeAlignmentCSUScript(scriptFilename, doMira);
					} else {
						currentSlitConfiguration.writeScienceCSUScript(scriptFilename, doMira);
					}
					scriptFile = new File(scriptFilename);
					//. make file world executable
					scriptFile.setExecutable(true, false);
					runMaskScript(scriptFilename);
					setLastMaskSetupIsAlign(doAlign);
					
					//. if mira flag is set, do mira script after mask script
					//. mask script does setupinit which downloads bar positions to controller
					//. this can take several seconds.
					//. wait for setup to complete by monitoring CSU Ready state
					if (doMira) {
						//. wait a couple of seconds to make sure setup starts
						Thread.sleep(2000);

						//. wait for setup to finish
						int timeoutCounter = 0;
						while (getCsuReady() != MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE) {
							Thread.sleep(1000);
							timeoutCounter++;
							if (timeoutCounter > MSCGUIParameters.SETUP_TIMEOUT_SECONDS) {
								break;
							}
						}
						
						//. if no timeout, execute MIRA script
						if (timeoutCounter > MSCGUIParameters.SETUP_TIMEOUT_SECONDS) {
							throw new IOException("Timeout waiting for mask setup to finish.  Mira setup not performed.");
						} else {
							runMaskScript(MSCGUIParameters.SCRIPT_MIRA.getAbsolutePath());						
						}
					}
				} else {
					throw new IOException("Directory "+scriptDirectory+" is not writeable.");
				}
			} else {
				throw new IOException("Directory "+scriptDirectory+" does not exist.  Please create or specify another directory.");
			} 
		} catch (IOException ex) {
			setScriptRunning(false);
			throw ex;
		} catch (InterruptedException ex) {
			setScriptRunning(false);
			throw ex;
		}
	}
	
	/**
	 * Execute mask by running execute mask script.
	 * If mask is run successfully, a FITS file with FITS extensions describing the current
	 * configuration will be written to the executed masks directory.
	 * The CSUFitsExtensionFilename property is then set to the path of the
	 * FITS extensions file.
	 * 
	 * @throws IOException              on errors executing mask or writing FITS extensions
	 * @throws InterruptedException     if script execution is interrupted
	 * @throws FitsException            if error constructing FITS extensions
	 * @throws NoSuchPropertyException  if property specifying extensions path cannot be found
	 * @throws InvalidValueException    if error setting fits extensions path property
	 */
	public void executeMask() throws IOException, InterruptedException, FitsException, NoSuchPropertyException, InvalidValueException {
		setScriptRunning(true);
			try {
				runMaskScript(MSCGUIParameters.SCRIPT_EXECUTE_MASK.getAbsolutePath());
			} catch (IOException ex) {
				setScriptRunning(false);
				throw ex;
			} catch (InterruptedException ex) {
				setScriptRunning(false);
				throw ex;
			}
		String fitsExtensionFilename = constructMaskScriptFilename(lastMaskSetupIsAlign, false, "fits");
		currentSlitConfiguration.writeFITSExtension(fitsExtensionFilename, lastMaskSetupIsAlign);
		propertyList.setNewPropertyValue("CSUFitsExtensionFilename", fitsExtensionFilename);
	}
	
	/**
	 * Execute script using process control.
	 * 
	 * @param  maskSetupScriptFilename  String to path of script to run
	 * @throws IOException              on errors running script
	 * @throws InterruptedException     if script is interrupted
	 */
	private void runMaskScript(String maskSetupScriptFilename) throws IOException, InterruptedException {
		//. run script
		long id = System.currentTimeMillis();
		String[] command = {maskSetupScriptFilename};

		ProcessInfo pi = new ProcessInfo(command, id);
		currentCommandProcessID = pi.getIdNumber();
		logger.info("executing <"+pi.getCommandString()+">.");
		
		//. in engineering mode, don't do anything, but simulate script finished
		//. otherwise, clear output messages and run process
		if (!MSCGUIParameters.ENGINEERING_MODE) {
			processOutputMessages.clear();
			myProcessControl.execute(pi);
		} else {
			setScriptRunning(false);
		}
	}
	
	/**
	 * Determine which bars are to be moved based on current mask setup.
	 * Typically, there are only unused bars when doing a long slit mask.
	 * 
	 * @param  doAlign  boolean specifying whether or not to check the alignment mask setup
	 * @return          92-element boolean array specifying if a bar is used.  Value at index n specifies if bar n+1 is used.
	 */
	public boolean[] getSlitUsageArray(boolean doAlign) {
		ArrayList<MechanicalSlit> currentSlitList = currentSlitConfiguration.getMechanicalSlitList();
		
		//. determine which rows are not assigned		
		//. default value is false
		boolean[] usedSlits = new boolean[MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS];
		
		for (MechanicalSlit slit : currentSlitList) {
			usedSlits[slit.getSlitNumber()-1] = true;
		}
		if (doAlign) {
			ArrayList<MechanicalSlit> currentAlignSlitList = currentSlitConfiguration.getAlignSlitList();
			for (MechanicalSlit slit : currentAlignSlitList) {
				usedSlits[slit.getSlitNumber()-1] = true;
			}
		}
		
		return usedSlits;
	}
	
	/**
	 * Create a slit configuration that gives target positions for bars not used in current configuration.
	 * This is typically only used for long slit configurations.  What to do with the bars is determined
	 * by the <code>closeOffType</code>.
	 * <p>
	 * The configuration created by this method only has its mechanical slit list defined, and
	 * has the same name as the current mask.
	 *  
	 * @param  doAlign  boolean flag for whether to use alignment mask
	 * @return          SlitConfiguration for ununsed bars
	 * @see #setCloseOffType(int)
	 */
	private SlitConfiguration constructExtraBarsConfiguration(boolean doAlign) {
		ArrayList<MechanicalSlit> slitList = new ArrayList<MechanicalSlit>();
		SlitConfiguration config = new SlitConfiguration(currentSlitConfiguration.getMaskName());
		
		boolean[] usedSlits = getSlitUsageArray(doAlign);
		
		for (int ii=0; ii<MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS; ii++) {
			if (!usedSlits[ii]) {
				//. slit number is 1-indexed
				int slitNumber = ii+1;
				//. get current bar positions
				double rightBarPosition = getRightBarPosition(slitNumber);
				double leftBarPosition = getLeftBarPosition(slitNumber);
				if (rightBarPosition < 0) continue;
				if (leftBarPosition < 0) continue;
				
				if ((leftBarPosition - rightBarPosition) > (minimumCloseOffSlitWidth / MosfireParameters.CSU_ARCSEC_PER_MM)) {
					MechanicalSlit slit = new MechanicalSlit(slitNumber);
					slit.setSlitWidth(closedOffSlitWidth);
					if (closeOffType == CLOSE_OFF_TYPE_REDUCE_IN_PLACE) {
						//. leave right bar where it is, but move left toward it
						slit.setCenterPosition((rightBarPosition - MosfireParameters.CSU_ZERO_PT) * MosfireParameters.CSU_ARCSEC_PER_MM + closedOffSlitWidth/2.0);
					} else if (closeOffType == CLOSE_OFF_TYPE_CLOSE_OFF) {
						//. move bars off of field of view to closest side
						//.  closest side it the side where the bar away from it moves the least
						if ((leftBarPosition - MosfireParameters.CSU_MINIMUM_BAR_POSITION_MM) < (MosfireParameters.CSU_MAXIMUM_BAR_POSITION_MM - rightBarPosition)) {
							//. move to left (toward zero)
							slit.setCenterPosition((MosfireParameters.CSU_MINIMUM_BAR_POSITION_MM - MosfireParameters.CSU_ZERO_PT) * MosfireParameters.CSU_ARCSEC_PER_MM + closedOffSlitWidth/2.0);
						} else {
							//. move to right (toward max)
							slit.setCenterPosition((MosfireParameters.CSU_MAXIMUM_BAR_POSITION_MM - MosfireParameters.CSU_ZERO_PT) * MosfireParameters.CSU_ARCSEC_PER_MM - closedOffSlitWidth/2.0);
						}
					} else {
						break;
					}
					//. add to new config
					slitList.add(slit);
				}
			}
		}
		config.setMechanicalSlitList(slitList);
		return config;
	}

	private double getLeftBarPosition(int slitNumber) {
		//. even bars
		return getBarPosition(2*slitNumber);
	}
	private double getRightBarPosition(int slitNumber) {
		//. odd bars
		return getBarPosition(2*slitNumber-1);
	}
	
	/**
	 * Get bar position from property.
	 * 
	 * @param  barNumber number of bar to get position
	 * @return           position of bar in mm.  Returns -1 if bar position property cannot be found.
	 */
	private double getBarPosition(int barNumber) {
		DecimalFormat format = new DecimalFormat("00");
		String propName = "CSUBarPosition"+format.format(barNumber);
		DoubleProperty prop = (DoubleProperty)propertyList.get(propName);
		if (prop != null) {
			return prop.getPrimitiveDoubleValue();
		} else {
			return -1.0;
		}
	}
	
  /**
   * Write MSC file to disk for current slit configuration.
   * 
   * @param  file           File object pointing to file to write to
   * @throws IOException    on errors writing the file
   * @throws JDOMException on errors constructing the XML
   */
  public void writeMSCFile(File file) throws IOException, JDOMException {
  	currentSlitConfiguration.writeSlitConfiguration(MSCGUIParameters.MSC_VERSION);
  }
  
  /**
   * Write HTML version of MSC to disk for current slit configuration.
   * 
   * @param  file                   File object pointing to file to write to
   * @throws TransformerException   on error constructing HTML
   * @throws MalformedURLException  on error getting XSLT transformation URL
   */
  public void writeMSCHtmlFile(File file) throws TransformerException, MalformedURLException {
   	currentSlitConfiguration.writeSlitConfigurationHTML(file);
  }
  
  /**
   * Write list of opened configurations to disk.
   * List is written in YAML format.
   * 
   * @param file                    File to save list of opened configurations.
   * @throws FileNotFoundException  if <code>file</code> is not a valid path.
   */
  public void writeMSCList(File file) throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(file);
		PrintStream p = new PrintStream(out);

		//. write in YAML format, although i won't use YAML
		//. library to parse, since this is pretty simple
		//. and i don't want to add the additional dependency
		p.println(MSCGUIParameters.MSC_LIST_CONFIGS_TAG);
		for (SlitConfiguration config : openedSlitConfigurations) {
			if (config.getStatus().equals(SlitConfiguration.STATUS_SAVED)) { 
				p.println(MSCGUIParameters.MSC_LIST_NAME_TAG+config.getMaskName());
				p.println(MSCGUIParameters.MSC_LIST_MSC_TAG+config.getOriginalFilename());
				p.println("");
			} else if (config.getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				p.println(MSCGUIParameters.MSC_LIST_NAME_TAG+config.getMaskName());
				p.println("");
			}
		}
  }
  
  /**
   * Open a saved list of MSCs and set opened configurations to list.
   * Non-fatal errors found parsing MSCs from list are appened to <code>outWarningList</code>,
   * which must be instantiated before passing in.
   * 
   * @param  file            File to load list of MSCs
   * @param  outWarningList  String ArrayList giving warnings found when opening MSCs
   * @throws IOException     on error opening <code>file</code>
   * @throws JDOMException   on error parsing MSC from list
   */
  public void openMSCList(File file, ArrayList<String> outWarningList) throws IOException, JDOMException {
  	//. list will be in ~YAML format. we'll parse manually though, 
  	//. so we don't need the dependency
  	
  	//. we'll assume the format above in terms of whitespace
  	//. ignore everything that isn't an tag we are expecting
  	RandomAccessFile currentRandomAccessFile = new RandomAccessFile(file, "r");

		String currentLine = currentRandomAccessFile.readLine();
		boolean inConfig = false;
		String name = "";
	  String msc = "";
	  while (currentLine != null) {
	  	logger.trace(currentLine);
	  	//. list begins with configs:
	  	if (currentLine.equals(MSCGUIParameters.MSC_LIST_CONFIGS_TAG)) {
	  		inConfig=true;
	  	}
	  	if (inConfig) {
	  		if (currentLine.startsWith(MSCGUIParameters.MSC_LIST_NAME_TAG)) {
	  			if (!name.isEmpty()) {
	  				try {
	  					ArrayList<String> tempWarningList = new ArrayList<String>();
	  					processMSCListEntry(name, msc, tempWarningList);
	  					if (!tempWarningList.isEmpty()) {
	  						outWarningList.add("Errors found for "+msc+":");
	  						outWarningList.addAll(tempWarningList);
	  						outWarningList.add("----------------------");
	  					}
	  				} catch (JDOMException e) {
	  					throw new JDOMException("Error parsing "+msc+": "+e.getMessage());
	  				} catch (IOException e) {
	  					throw new IOException("Error opening "+msc+": "+e.getMessage());
	  				}
	  			}
	  			name = currentLine.substring(12);
	  			msc="";
	  		} else if (currentLine.startsWith(MSCGUIParameters.MSC_LIST_MSC_TAG)) {
	  			msc =  currentLine.substring(12);
	  		}
	  	}
  		currentLine = currentRandomAccessFile.readLine();
	  }
	  if (!name.isEmpty()) {
	  	try {
	  		ArrayList<String> tempWarningList = new ArrayList<String>();
	  		processMSCListEntry(name, msc, tempWarningList);
	  		if (!tempWarningList.isEmpty()) {
	  			outWarningList.add("Errors found for "+msc+":");
	  			outWarningList.addAll(tempWarningList);
	  			outWarningList.add("----------------------");
	  		}
	  	} catch (JDOMException e) {
	  		throw new JDOMException("Error parsing "+msc+": "+e.getMessage());
	  	} catch (IOException e) {
	  		throw new IOException("Error opening "+msc+": "+e.getMessage());
	  	}
	  }
  }
  
  /**
   * Process entry in MSC list.  Entry is interpreted, and a Slit Configuration is
   * generated for each entry and added to list of opened configurations.
   * 
   * @param  name            String name of mask from entry
   * @param  msc             String path to MSC file for entry
   * @param  outWarningList  String ArrayList to list warnings when opening MSC
   * @throws IOException     on error opening MSC file
   * @throws JDOMException   on error parsing MSC file
   */
  private void processMSCListEntry(String name, String msc, ArrayList<String> outWarningList) throws IOException, JDOMException {
  	logger.debug("opening mask: <"+name+">, file=<"+msc+">.");
  	//. normal MSC files will have a non-empty msc value
  	if (!msc.isEmpty()) {
			openSlitConfiguration(new File(msc), outWarningList);
		} else {
			//. if msc is empty, it must be an OPEN or long slit mask
			if (name.equals("OPEN")) {
				addSlitConfiguration(SlitConfiguration.createOpenMaskSlitConfiguration());
			} else if (name.startsWith("LONGSLIT-")) {
				//. parse size of long slit from name
				String sizeString = name.substring(9);
	  		int xpos = sizeString.indexOf('x');
	  		//. longslit name must be LONGSLIT-<rows>x<width>
	  		//. be sure to validate rows and width
	  		if (xpos > 0) {
		  		int rows;
		  		double width;	  			
	  			rows = Integer.parseInt(sizeString.substring(0,xpos));
	  			if ((rows <= 0) || (rows > MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS)) {
	  				throw new IOException("LONSSLIT name has invalid number of rows");
	  			}
	  			width = Double.parseDouble(sizeString.substring(xpos+1));
	  			if ((width < MosfireParameters.MINIMUM_SLIT_WIDTH) || (width > MosfireParameters.CSU_WIDTH)) {
	  				throw new IOException("LONGSLIT has invalid width.");
	  			}
	  			openLongSlitConfiguration(rows, width);
	  		}
			}
		}
  }
  
  /**
   * Get current slit configuration.
   * 
   * @return  SlitConfiguration that is the current configuration
   */
  public SlitConfiguration getCurrentSlitConfiguration() {
  	return currentSlitConfiguration;
  }

  /**
   * Set the current slit configuration.
   * Registered <code>PropertyChangeListeners</code> are notified of change. 
   * 
   * @param currentSlitConfiguration  SlitConfiguration to set as current
   */
  public void setCurrentSlitConfiguration(SlitConfiguration currentSlitConfiguration) {
  	SlitConfiguration oldValue = this.currentSlitConfiguration;
		this.currentSlitConfiguration = currentSlitConfiguration;
		propertyChangeListeners.firePropertyChange("currentSlitConfiguration",
				oldValue, currentSlitConfiguration);
	}

	/**
	 * Get <code>SlitPosition</code> object in current configuration for specified row.
	 * 
	 * @param  row                             row index (0-based) for which to get SlitPosition
	 * @return                                 SlitPosition for specified row
	 * @throws ArrayIndexOutOfBoundsException  if row is outside of range of current configuration rows
	 */
	public SlitPosition getSlitPosition(int row) throws ArrayIndexOutOfBoundsException {
  	return (currentSlitConfiguration.getSlitPosition(row));
  }
	
	/**
	 * Get the index of the active row (0-based).
	 * 
	 * @return index of active row
	 */
	public int getActiveRow() {
		return activeRow;
	}
	
	/**
	 * Set the active row.
	 * Registered <code>PropertyChangeListeners</code> are notified.
	 * 
	 * @param activeRow index of new active row
	 */
	public void setActiveRow(int activeRow) {
		int oldActiveRow = this.activeRow;
		this.activeRow = activeRow;
		propertyChangeListeners.firePropertyChange("activeRow", new Integer(oldActiveRow), new Integer(activeRow));	
	}

	/**
	 * Set the active row based on mechanical slit number.
	 * Active row is set to -1 if row is not used in mechanical slit list.
	 * 
	 * @param row  mechanical slit number to set active.
	 */
	public void setActiveSlitRow(int row) {
		int ii=0;
		for (MechanicalSlit slit : currentSlitConfiguration.getMechanicalSlitList()) {
			if (slit.getSlitNumber() == row) {
				setActiveRow(ii);
				return;
			}
			ii++;
		}		
		setActiveRow(-1);
	}

	/**
	 * Set the active row based on specified object.

	 * @param obj  AstroObj object to set as active.
	 */
	public void setActiveObject(AstroObj obj) {
		//. for stars, get index from alignment list.
		if (obj.getObjPriority() < 0) {
			for (MechanicalSlit slit : currentSlitConfiguration.getAlignSlitList()) {
				if (slit.getTarget().getObjName().equals(obj.getObjName())) {
					setActiveSlitRow(slit.getSlitNumber());
					return;
				}
			}
		} else {
			//. otherwise, get index from mechnanical list
			int ii=0;
			///////////////////////////////////////////////////////////////////
			// Part of MAGMA UPGRADE item m3 by Ji Man Sohn, UCLA 2016-2017  //
			// * if active row already has the obj to be set active, return  // 
			if(currentSlitConfiguration.getMechanicalSlitList().get(activeRow).getTarget().equals(obj)){
				return;
			}else {
				for (MechanicalSlit slit : currentSlitConfiguration.getMechanicalSlitList()) {
					if (slit.getTarget().getObjName().equals(obj.getObjName())) {
						setActiveRow(ii);
						return;
					}
					ii++;
				}
			}
			//			for (MechanicalSlit slit : currentSlitConfiguration.getMechanicalSlitList()) {
			//				if (slit.getTarget().getObjName().equals(obj.getObjName())) {
			//					setActiveRow(ii);
			//					return;
			//				}
			//				ii++;
			//			}
			///////////////////////////////////////////////////////////////////
		}
	}
	
  /**
   * Align the active slit with the slit above it.
   * Registered <code>PropertyChangeListeners</code> are notified of the change to the current slit configuration.
   * @see edu.ucla.astro.irlab.mosfire.util.SlitConfiguration#alignSlitWithNeighbor(int, boolean)
   */
  public void alignActiveSlitWithAbove() {
  	try {
  		currentSlitConfiguration.alignSlitWithNeighbor(activeRow, true);
			propertyChangeListeners.firePropertyChange("currentSlitConfiguration",
					null, currentSlitConfiguration);
  	} catch (ArrayIndexOutOfBoundsException aioobEx) {
  		//. shouldn't happen
  		aioobEx.printStackTrace();
  	}
  }

  /**
   * Align the active slit with the slit below it.
   * Registered <code>PropertyChangeListeners</code> are notified of the change to the current slit configuration.
   * @see edu.ucla.astro.irlab.mosfire.util.SlitConfiguration#alignSlitWithNeighbor(int, boolean)
   */
  public void alignActiveSlitWithBelow() {
  	try {
  		currentSlitConfiguration.alignSlitWithNeighbor(activeRow, false);
			propertyChangeListeners.firePropertyChange("currentSlitConfiguration",
					null, currentSlitConfiguration);
  	} catch (ArrayIndexOutOfBoundsException aioobEx) {
  		//. shouldn't happen
  		aioobEx.printStackTrace();
  	}
  }

  /**
   * Move slit onto specified target.
   * Registered <code>PropertyChangeListeners</code> are notified of the change to the current slit configuration.
   * 
   * @param  target  AstroObj object to move slit onto
   * @return         boolean is false if valid slit cannot be put on target because object is too close to edge of field
   * @see            edu.ucla.astro.irlab.mosfire.util.SlitConfiguration#moveSlitOntoTarget(int, AstroObj)
   */  
  public boolean moveSlitOntoTarget(AstroObj target) {
  	boolean status = false;
  	try {
  		status = currentSlitConfiguration.moveSlitOntoTarget(activeRow, target);
  		if (status) {
			propertyChangeListeners.firePropertyChange("currentSlitConfiguration",
					null, currentSlitConfiguration);
  		}
  	} catch (ArrayIndexOutOfBoundsException aioobEx) {
  		//. shouldn't happen
  		aioobEx.printStackTrace();
  	}
		return status;
  }

  /**
   * Returns whether current slit configuration has any invalid slits
   * 
   * @return false if configuration have any invalid slits, true otherwise
   * @see SlitConfiguration#hasInvalidSlits()
   */
  public boolean currentSlitConfigurationHasInvalidSlits() {
  	return currentSlitConfiguration.hasInvalidSlits();
  }

  /**
   * Get <code>MascgenResult</code> for last run of MASCGEN.
   * 
   * @return  MascgenResult for last run of MASCGEN.
   */
  public MascgenResult getCurrentMascgenResult() {
  	return currentMascgenResult;
  }
  
	/**
	 * Method run when script completes.
	 * Registered <code>PropertyChangeListeners</code> are notified that processErrorOuput is set to <code>processErrorMessages</code> if exit code is non-zero.
	 * 
	 * @param process  ProcessInfo for process that completed
	 * @param code     exit code for process
	 */
	protected void processExited(ProcessInfo process, int code) {
		if (process.getIdNumber() == currentCommandProcessID) {
			if (code != 0) {
				propertyChangeListeners.firePropertyChange("processErrorOutput", null, processOutputMessages);
			}
		}
	}

	/**
	 * Get whether script is running.
	 * 
	 * @return true is script is running, false if not.
	 */
	public boolean isScriptRunning() {
		return scriptRunning;
	}
	
	/**
	 * Set whether script is running.
	 * Registered <code>PropertyChangeListeners</code> are notified.
	 * 
	 * @param scriptRunning  boolean if script is running
	 */
	private void setScriptRunning(Boolean scriptRunning) {
		logger.trace("new ScriptRunning="+scriptRunning);
		Boolean oldValue = this.scriptRunning;
		this.scriptRunning = scriptRunning;
		propertyChangeListeners.firePropertyChange("scriptRunning", oldValue, scriptRunning);
	}
 
	/**
	 * Sets name of mask currently in place in CSU.
	 * Not to be confused with mask name of current configuration.
	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
	 * 
	 * @param currentMaskName  String name of current mask.
	 */
	private void setCurrentMaskName(String currentMaskName) {
		String oldValue = this.currentMaskName;
		this.currentMaskName = currentMaskName;
		propertyChangeListeners.firePropertyChange("currentMaskName",	oldValue, currentMaskName);
	}

	/**
	 * Gets the name of mask currently in place in CSU.
	 * Not to be confused with mask name of current configuration.
	 * 
	 * @return  String name of current mask
	 */
	public String getCurrentMaskName() {
		return currentMaskName;
	}
	
	/**
	 * Sets name of mask currently setup in CSU.
	 * Not to be confused with mask name of current configuration.
	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
	 * 
	 * @param loadedMaskSetup  String name of current setup mask.
	 */
	public void setLoadedMaskSetup(String loadedMaskSetup) {
		String oldValue = this.loadedMaskSetup;
		this.loadedMaskSetup = loadedMaskSetup;
		propertyChangeListeners.firePropertyChange("loadedMaskSetup",	oldValue, loadedMaskSetup);
	}
	
	/**
	 * Gets the name of mask currently setup in CSU.
	 * Not to be confused with mask name of current configuration.
	 * 
	 * @return  String name of current setup mask
	 */
	public String getLoadedMaskSetup() {
		return loadedMaskSetup;
	}

	/**
	 * Set the executed masks directory.
	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
	 * 
	 * @param scriptDirectory  File object pointing to new directory
	 */
	public void setScriptDirectory(File scriptDirectory) {
		File oldValue = this.scriptDirectory;
		this.scriptDirectory = scriptDirectory;
		propertyChangeListeners.firePropertyChange("scriptDirectory", oldValue, scriptDirectory);
	}

	/**
	 * Get the executed masks directory.
	 * 
	 * @return  File pointing to directory.
	 */
	public File getScriptDirectory() {
		return scriptDirectory;
	}

	/**
	 * Set whether program is in online mode.
	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
	 *
	 * @param online  boolean flag for mode.
	 */
	public void setOnline(boolean online) {
		boolean oldValue = this.online;
		this.online = online;
		propertyChangeListeners.firePropertyChange("online", oldValue, online);
	}

	/**
	 * Get the online mode.
	 * 
	 * @return  true is in online mode, false if not.
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * Set whether last mask setup was an alignment mask.
	 * 
	 * @param lastMaskSetupIsAlign  flag for whether last setup was an alignment mask
	 */
	public void setLastMaskSetupIsAlign(boolean lastMaskSetupIsAlign) {
		this.lastMaskSetupIsAlign = lastMaskSetupIsAlign;
	}

	/**
	 * Return whether last mask setup was an alignment mask.
	 * 
	 * @return true is last mask setup was an alignment mask, false if not
	 */
	public boolean isLastMaskSetupIsAlign() {
		return lastMaskSetupIsAlign;
	}

	/**
	 * Notify registered <code>PropertyChangeListeners</code> of new MASCGEN exception.
	 * This is used for non-MascgenArgument parsing/validating exceptions as well.
	 * 
	 * @param ex  MascgenArgumentException to broadcast
	 */
	public void setMascgenArgumentException(MascgenArgumentException ex) {
		propertyChangeListeners.firePropertyChange("mascgenArgumentException", null, ex);
	}

	/**
	 * Get CSU readiness state.
	 * 
	 * @return  CSU Readiness state
	 * @see MSCGUIParameters#CSU_READINESS_STATES
	 */
	public int getCsuReady() {
		return csuReady;
	}
	
	/**
	 * Set the CSU readiness state.
 	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
	 *
	 * @param csuReady new readiness state
	 */
	public void setCsuReady(int csuReady) {
		int oldValue = this.csuReady;
		this.csuReady = csuReady;
		propertyChangeListeners.firePropertyChange("csuReady", new Integer(oldValue), new Integer (csuReady));
	}

	/**
	 * Get the CSU status string.
	 * 
	 * @return String giving CSU status
	 */
	public String getCsuStatus() {
		return csuStatus;
	}
	
	/**
	 * Set the CSU status string.
	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
	 * 
	 * @param csuStatus
	 */
	public void setCsuStatus(String csuStatus) {
		String oldValue = this.csuStatus;
		this.csuStatus = csuStatus;
		propertyChangeListeners.firePropertyChange("csuStatus",
				oldValue, csuStatus);
	}
	
	/**
	 * Get the minimum slit width for unused slits that will be closed off.
	 * 
	 * @return  double value for minimum slit width in arcsec
	 */
	public double getMinimumCloseOffSlitWidth() {
		return minimumCloseOffSlitWidth;
	}

	/**
	 * Set the minimum slit width for unused slit that will be closed off.
	 * 
	 * @param minimumCloseOffSlitWidth  double value for minimum slit width in arcsec
	 */
	public void setMinimumCloseOffSlitWidth(double minimumCloseOffSlitWidth) {
		this.minimumCloseOffSlitWidth = minimumCloseOffSlitWidth;
	}
	
	/**
	 * Get the width to which unused slits will be closed to if they are to be closed off.
	 * 
	 * @return slit width in arcsec
	 */
	public double getClosedOffSlitWidth() {
		return closedOffSlitWidth;
	}

	/**
	 * Set the width to which unused slits to be closed off will be closed to.
	 * 
	 * @param closedOffSlitWidth  double value for slit width in arcsec
	 */
	public void setClosedOffSlitWidth(double closedOffSlitWidth) {
		this.closedOffSlitWidth = closedOffSlitWidth;
	}

	/**
	 * Set the action to perform on unused bars exceeding minimum close off slit width.
	 * Must be one of CLOSE_OFF_TYPE_DO_NOTHING, CLOSE_OFF_TYPE_REDUCE_IN_PLACE, or
	 * CLOSE_OFF_TYPE_CLOSE_OFF.
	 * Registered <code>PropertyChangeListeners</code> are notified of change in value.
.  *
	 * @param closeOffType  action to perform on unused slits
	 */
	public void setCloseOffType(int closeOffType) {
		int oldValue = this.closeOffType;
		this.closeOffType = closeOffType;
		propertyChangeListeners.firePropertyChange("closeOffType", oldValue, closeOffType);
	}
	
	/**
	 * Get action to perform for unused bars.
	 * 
	 * @return action to perform for unused bars.  One of CLOSE_OFF_TYPE_DO_NOTHING, CLOSE_OFF_TYPE_REDUCE_IN_PLACE, or
	 * CLOSE_OFF_TYPE_CLOSE_OFF
	 */
	public int getCloseOffType() {
		return closeOffType;
	}

	/**
	 * Set whether unused slits will be reassigned.
	 * 
	 * @param mascgenReassignUnusedSlits  whether or not unused slits should be reassigned
	 */
	public void setMascgenReassignUnusedSlits(boolean mascgenReassignUnusedSlits) {
		this.mascgenReassignUnusedSlits = mascgenReassignUnusedSlits;
	}

	/**
	 * Get whether unused slits will be reassigned.
	 * 
	 * @return  whether or not unused slits will be reassigned.
	 */
	public boolean isMascgenReassignUnusedSlits() {
		return mascgenReassignUnusedSlits;
	}
	
	/**
	 * Add or decrease slit width of all slits by specified amount.
	 * Registered <code>PropertyChangeListeners</code> are notified of change to current slit configuration.
	 * 
	 * @param  offset double value to adjust slit widths in arcsec.
	 * @return false if slit widths cannot be adjusted because they would be too small or go off of the edge of FOV. True otherwise.
   * @see SlitConfiguration#incrementSlitWidth(double)
	 */
	public boolean incrementSlitWidth(double offset) {
		boolean status = currentSlitConfiguration.incrementSlitWidth(offset);
		if (status) {
			propertyChangeListeners.firePropertyChange("currentSlitConfiguration", null, currentSlitConfiguration);
		}
		return status;
	}

	/**
	 * Set the width of slit at specified row in arcsec.
	 * Registered <code>PropertyChangeListeners</code> are notified of change to current slit configuration.
   *
	 * @param row        row to adjust slit width
	 * @param slitWidth  new slit width in arcsec
	 */
	public void setSlitWidth(int row, double slitWidth) {
		currentSlitConfiguration.setSlitWidth(row, slitWidth);
		propertyChangeListeners.firePropertyChange("currentSlitConfiguration", null, currentSlitConfiguration);
	}

	/**
	 * Returns if current filter in instrument is the dark filter, obtained from MechFilterPosition property.
	 * 
	 * @return  true if value of MechFilterPosition property is Dark, false otherwise
	 */
	public boolean isFilterDark() {
		StringProperty propFilter = (StringProperty)propertyList.getProperty("MechFilterPosition");
		if (propFilter != null) {
			return (propFilter.getValue().equals("Dark"));
		}
		return false;
	}

	/**
	 * Initialize local member variables with values from corresponding properties.
	 */
	private void initializeValuesFromProperties() {
		setCsuStatus((String)(propertyList.getProperty("CSUStatus").getValue()));
		setLoadedMaskSetup((String)(propertyList.getProperty("CSUSetupMaskName").getValue()));
		setCurrentMaskName((String)(propertyList.getProperty("CSUMaskName").getValue()));
		setCsuReady(((Integer)(propertyList.getProperty("CSUMoveReadiness").getValue())).intValue());
	}

	/**
	 * Register as a listener to property changes in property list.
	 * 
	 * @throws NoSuchPropertyException  if property is not in property list.
	 */
	private void registerWithProperties() throws NoSuchPropertyException {
		propertyList.registerListener("CSUStatus", this);
		propertyList.registerListener("CSUMoveReadiness", this);		
		propertyList.registerListener("CSUSetupMaskName", this);		
		propertyList.registerListener("CSUMaskName", this);		
		propertyList.registerListener(MosfireParameters.MOSFIRE_PROPERTY_MAGMA_SCRIPT_QUESTION, this);

	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().compareTo("CSUStatus") == 0) {
			setCsuStatus((String)(evt.getNewValue()));
		} else if (evt.getPropertyName().compareTo("CSUMoveReadiness") == 0) {
			setCsuReady(((Integer)(evt.getNewValue())).intValue());
		} else if (evt.getPropertyName().compareTo("CSUSetupMaskName") == 0) {
			setLoadedMaskSetup((String)(evt.getNewValue()));
		} else if (evt.getPropertyName().compareTo("CSUMaskName") == 0) {
			setCurrentMaskName((String)(evt.getNewValue()));
		} else {
			propertyChangeListeners.firePropertyChange(evt);
		}
	}
	public void answerScriptQuestion(boolean answerIsYes) throws NoSuchPropertyException, InvalidValueException {
		//. note: kjava errors come back casted as InvalidValueException
		propertyList.setNewPropertyValue(MosfireParameters.MOSFIRE_PROPERTY_MAGMA_SCRIPT_REPLY, (answerIsYes ? "y" : "n"));
	}
	
	/**
	 * Set the KJava property manager.
	 * 
	 * @param manager  KJavaPropertyManager object to set as KJava manager
	 */
	public void setKJavaPropertyManager(KJavaPropertyManager manager) {
		propertyManager = manager;
	}
	
	/**
	 * Request that KJava property manager stop its cshow (keyword monitor).
	 * 
	 * @throws KJavaException  on errors stopping cshow
	 */
	public void stopCShow() throws KJavaException {
		if (propertyManager != null) {
			propertyManager.stop();
		}
	}
	
	/**
	 * This inner class is used to monitor processes started by <code>ProcessControl</code>.
	 * 
	 * @author Jason L. Weiss, UCLA Infrared Laboratory
	 * @ProcessControl
	 */
	private class MSCGUIProcessListener implements ProcessListener {
		//. all we care about here are exit codes
		//. append messages to processOutputMessages array.  
		//. prepend with process command if first message.
		public void processErrMessage(ProcessInfo process, String message) {
			logger.error(message);
			if (processOutputMessages.isEmpty()) {
				processOutputMessages.add(process.getCommandString()+":");
			}
			processOutputMessages.add(message);
		}
		public void processOutMessage(ProcessInfo process, String message) {
			logger.debug(message);
			if (processOutputMessages.isEmpty()) {
				processOutputMessages.add(process.getCommandString()+":");
			}
			processOutputMessages.add(message);
		}
		public void processExitCode(ProcessInfo process, int code) {
			logger.debug("process "+process.getIdNumber()+" ("+process.getCommandString()+") exited with code "+code);
			processExited(process, code);
			setScriptRunning(false);
		}

	}	

	/**
	 * Listener to <code>MascgenCore</code>.
	 * 
	 * @author Jason L. Weiss, UCLA Infrared Laboratory
	 * @see MascgenCore
	 */
	private class MascgenChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			propertyChangeListeners.firePropertyChange(evt);
		}		
	}
  
	
	/**
	 * Thread for running MASCGEN.
	 * 
	 * @author Jason L. Weiss, UCLA Infrared Laboratory
	 * @see MSCGUIModel#runMascgen(MascgenArguments)
	 */
	private class MascgenRunThread extends Thread {
		MascgenArguments args;
		public MascgenRunThread(MascgenArguments mascgenArgs) {
			args=mascgenArgs;
		}
		public void run() {
			try {
				runMascgen(args);
			} catch (MascgenArgumentException ex) {
				setMascgenArgumentException(ex);
			}
		}
		
	}
	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	public void setNumTopConfigs(int num){
		mascgen.setNumTopConfigs(num);
	}
	//////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M3 by Ji Man Sohn, UCLA 2016-2017 //
	public void wholeFieldOffsetInX(double offset, boolean realign) throws OffsetException {
		MascgenArguments args = currentSlitConfiguration.getMascgenArgs();
		
		//Clone the result while setting newCenter to the result
		MascgenResult result = currentSlitConfiguration.getMascgenResult();
		MascgenResult newResult = new MascgenResult();
		
		RaDec newCenter = getOffsetCenter(result, offset);
		MascgenTransforms.raDecToXY(newCenter);
		
		newResult.setCenter(newCenter);
		newResult.setPositionAngle(result.getPositionAngle());
		
		//Check the astro bjects against the offset to see if they fall within boundary;
		double theta = Math.toRadians(newResult.getPositionAngle());
		double xOld, yOld;
		double objX, objY;
		double circleOriginX = CSU_WIDTH / 2;
		double circleOriginY = CSU_HEIGHT / 2;
		RaDec objectRaDec;
		Point2D.Double objWcs;
		
		// Checking alignment stars
		
		ArrayList<AstroObj> starsStaying = new ArrayList<AstroObj>();
		ArrayList<AstroObj> starsLeaving = new ArrayList<AstroObj>();
		
		int row;
		for  (MechanicalSlit slit : currentSlitConfiguration.getAlignSlitList()) {
			AstroObj obj = slit.getTarget().getCleanAstroObj();
			// Transform the entire astroObjArray into the CSU plane by subtracting
			// the center coordinate from each AstroObj's xCoordinate and
			// yCoordinate and putting these into the ObjX and ObjY.
			objectRaDec = new RaDec((int)Math.floor(obj.getRaHour()), (int)Math.floor(obj.getRaMin()), obj.getRaSec(), obj.getDecDeg(), obj.getDecMin(), obj.getDecSec());
			objWcs = MascgenTransforms.getWcsFromRaDec(objectRaDec, newCenter.getYCoordinate());
			
			xOld = objWcs.x - newCenter.getXCoordinate();
			yOld = objWcs.y - newCenter.getYCoordinate();
			
			// Rotate the objects in the CSU plane by the Position Angle.
			/** Objects were read in with coordinate system origin at center of
			 *  CSU field. The optimize method runs with the coordinate system
			 *  origin in the lower left. So, simply add CSU_WIDTH / 2 to the x
			 *  position and CSU_HEIGHT / 2 to the y position of each object. **/
			objX = xOld * Math.cos(theta) - yOld * Math.sin(theta) + CSU_WIDTH / 2;
			objY = xOld * Math.sin(theta) + yOld * Math.cos(theta) + CSU_HEIGHT / 2;
			
			/** Crop out all AstroObjs in the astroObjArray that lie outside the
			 * focal plane circle, defined by CSU_FP_RADIUS centered at the origin
			 * (CSU_WDITH / 2, CSU_HEIGHT / 2). **/
			/** Crop out all AstroObjs in the astroObjArray that have x coordinate
			 * positions outside of the CSU Plane. **/
			if (Point.distance(objX, objY, circleOriginX, circleOriginY) < CSU_FP_RADIUS) {

				if ((objX > STAR_EDGE_DISTANCE) && (objX < CSU_WIDTH-STAR_EDGE_DISTANCE)) {

					if ((objY > STAR_EDGE_DISTANCE) && (objY < CSU_HEIGHT-STAR_EDGE_DISTANCE)){ 

						
						/** Assign each Star AstroObj to its correct StarRowRegion.
						 * Then crop out the objects that do not fall into a RowRegion. **/
						row=AstroObj.getRow(objY, args.getAlignmentStarEdgeBuffer());

						if (row != -1) {
							obj.setObjRR(row);
							starsStaying.add(obj);
						} else {
							starsLeaving.add(obj);
						}
					}else {
						starsLeaving.add(obj);
					}
				}else {
					starsLeaving.add(obj);
				}
			}else {
				starsLeaving.add(obj);
			}                      
		}
		
		if(starsLeaving.size() > 0 && starsStaying.size()<args.getMinimumAlignmentStars()){
			if(realign){	
				
				List<AstroObj> ogAllList = currentSlitConfiguration.getOriginalTargetList();
				HashSet<AstroObj> ogStarSet = new HashSet<AstroObj>();
				for(AstroObj obj : ogAllList) {
					if(obj.getObjPriority() < 0) {
						ogStarSet.add(obj.getCleanAstroObj());
					}
				}
				
				ArrayList<AstroObj> legalStars = new ArrayList<AstroObj>();
				for (AstroObj obj : mascgen.findLegalStars(ogStarSet, newCenter, result.getPositionAngle())){
					legalStars.add(obj);
				}
				System.out.println("staying : " +legalStars);
				if(legalStars.size()<args.getMinimumAlignmentStars()){
					throw new OffsetException("Re-assigning alignment stars failed.\n" +
							"Try adding additional alignment stars.");
				} else {
					starsStaying = legalStars;
				}
			}else {
				String message = "Offset Aborted. Following align star(s) will be lost.\n";
				for(AstroObj obj: starsLeaving) {
					message += obj.getObjName() + "\n";
				}
				message += "Number of valid alignment stars (" +starsStaying.size() 
				+ ") will be less than minimum required (" + args.getMinimumAlignmentStars() +")\n" +
				"If you wish Mascgen to find a new set of alignment stars, select Ok.\n";
				throw new OffsetException(message);
			}
		}
		
		// Checking slit targets
		
		ArrayList<AstroObj> targetsStaying = new ArrayList<AstroObj>();
		ArrayList<AstroObj> targetsLeaving = new ArrayList<AstroObj>();
		double tempPriority = 0;
		
		double ditherSpace = args.getDitherSpace();
		double minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2);
		double maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2);
		
		for (MechanicalSlit slit : currentSlitConfiguration.getMechanicalSlitList()) {
			
			AstroObj obj = slit.getTarget().getCleanAstroObj();
			
			objectRaDec = new RaDec((int)Math.floor(obj.getRaHour()), (int)Math.floor(obj.getRaMin()), obj.getRaSec(), obj.getDecDeg(), obj.getDecMin(), obj.getDecSec());
			objWcs = MascgenTransforms.getWcsFromRaDec(objectRaDec, newCenter.getYCoordinate());
			
			// Transform the entire astroObjArray into the CSU plane by subtracting
			// the center coordinate from each AstroObj's xCoordinate and 
			// yCoordinate and putting these into the ObjX and ObjY.
			xOld = objWcs.x - newCenter.getXCoordinate();
			yOld = objWcs.y - newCenter.getYCoordinate();

			// Rotate the objects in the CSU plane by the Position Angle.
			/* Objects were read in with coordinate system origin at center of 
			 *  CSU field. The optimize method runs with the coordinate system 
			 *  origin in the lower left. So, simply add CSU_WIDTH / 2 to the x  
			 *  position and CSU_HEIGHT / 2 to the y position of each object. 
			 */
			objX = (xOld * Math.cos(theta) - yOld * Math.sin(theta));
			objY = (xOld * Math.sin(theta) + yOld * Math.cos(theta));

			/* Crop out all AstroObjs in the astroObjArray that 
			 * 
			 * a) lie outside the  focal plane circle, defined by CSU_FP_RADIUS 
			 *    centered at the origin (CSU_WDITH / 2, CSU_HEIGHT / 2). 
			 * b) have x coordinate positions outside of the legal range. 
			 * c) have x coordinate positions outside of the CSU Plane. 
			 */
			//. need to check object stays in slit during dither
			//.
			//. coordinate system origin is at center, and goes positive to the left, and up
			//. with slits tilted 4 degrees counter-clockwise
			double maxY = objY + ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double maxX = objX + ditherSpace * Math.sin(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double minY = objY - ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double minX = objX - ditherSpace * Math.sin(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			if ((Point.distance(minX, minY, 0, 0) < CSU_FP_RADIUS) && 
					(Point.distance(maxX, maxY, 0, 0) < CSU_FP_RADIUS) && 
					(minX >= minLegalX) && (maxX <= maxLegalX) && (minX > -CSU_WIDTH/2.) && (maxX < CSU_WIDTH/2.) &&
					(minY > -CSU_HEIGHT/2.) && (maxY < CSU_HEIGHT/2.)) {

				obj.setObjX(objX);
				obj.setObjY(objY);

				//. determine what rows the object occupies during full dither
				int minRow = (int)Math.floor((minY + CSU_HEIGHT / 2. - MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT);
				int maxRow = (int)Math.floor((maxY + CSU_HEIGHT / 2. + MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT);
				obj.setMinRow(minRow);
				obj.setMaxRow(maxRow);
				
				//. make sure object stays within mask boundaries during dither
				//. this should be ensured by check above, but better to 
				//. include this just be sure, to avoid IndexOutOfBoundsException
				if ((minRow >= 0) &&  (maxRow < CSU_NUMBER_OF_BAR_PAIRS)) {
					targetsStaying.add(obj);
					tempPriority += obj.getObjPriority();
				}else {
					if(!targetsLeaving.contains(obj)){
						targetsLeaving.add(obj);
					}
				}
			}else {
				if(!targetsLeaving.contains(obj)){
					targetsLeaving.add(obj);
				}			
			}
		}
		
		
		
		newResult.setAstroObjects(targetsStaying.toArray(new AstroObj[targetsStaying.size()]));
		newResult.setLegalAlignmentStars(starsStaying.toArray(new AstroObj[starsStaying.size()]));
		newResult.setTotalPriority(tempPriority);
		
		SlitConfiguration newConfig = SlitConfiguration.generateSlitConfiguration(args, newResult, mascgenReassignUnusedSlits);
		if(realign && newConfig.getAlignmentStarCount()<args.getMinimumAlignmentStars()) {
			throw new OffsetException("Re-assigning alignment stars failed.");
		}
		newConfig.setOriginalTargetSet(currentSlitConfiguration.getOriginalTargetList());
  		newConfig.updateOriginalAstroObjects();
  		newConfig.setMaskName(currentSlitConfiguration.getMaskName());
  		addSlitConfiguration(newConfig);
  		setCurrentSlitConfiguration(newConfig);
  		if(targetsLeaving.size()>0){
			String message = "Following objects was lost during the offset.\nPlease select a different target.\n";
			for(AstroObj obj: targetsLeaving) {
				message += obj.getObjName() + "\n";
			}
			throw new OffsetException(message);
		}
	}
	
	private RaDec getOffsetCenter(MascgenResult result, double offset) {
		result = currentSlitConfiguration.getMascgenResult();
		
		RaDec oldCenter = result.getCenter();
		double positionAngleRadian = Math.toRadians(result.getPositionAngle());
		double raOffset = offset * Math.cos(positionAngleRadian) / 15;
		double decOffset = offset * Math.sin(positionAngleRadian);
		double newRaSec = oldCenter.getRaSec()+raOffset;
		int newRaMin = oldCenter.getRaMin();
		int newRaHour = oldCenter.getRaHour();
		double newDecSec = oldCenter.getDecSec() + decOffset;
		double newDecMin = oldCenter.getDecMin();
		double newDecDeg = oldCenter.getDecDeg();
		
		
		if(newRaSec >= 60){
			newRaSec -= 60;
			newRaMin = oldCenter.getRaMin()+1;
			if(newRaMin >= 60){
				newRaMin -= 60;
				newRaHour +=1;
				if(newRaHour >= 24){
					newRaHour -= 24;
				}
			}	
		}
		
		if(newDecSec >= 60){
			newDecSec -= 60;
			newDecMin = oldCenter.getDecMin()+1;
			if(newDecMin >= 60){
				newDecMin -= 60;
				newDecDeg +=1;
				if(newDecDeg >= 24){
					newDecDeg -= 24;
				}
			}	
		}
		
		return new RaDec(newRaHour, newRaMin, newRaSec, newDecDeg, newDecMin, newDecSec);
	}
	//////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M1 by Ji Man Sohn, UCLA 2016-2017 //
	public void slitNudge(double offset) {
		// Grab current configuration along with some current values
		SlitConfiguration config = currentSlitConfiguration;
		int row = activeRow;
		ArrayList<MechanicalSlit> ogSlitList = config.getMechanicalSlitList();
		String targetName = ogSlitList.get(row).getTargetName();
		
		// Prepare arraylist to store all the adjacent slits with same target, and add current slit.
		ArrayList<MechanicalSlit> nudgeSlitList = new ArrayList<MechanicalSlit>();
		nudgeSlitList.add(ogSlitList.get(row));
	
		// Visiting adjacent slits to check if the targets are are same. If they are, add to nudge slit list.
		int tempRow = row - 1;
		while (tempRow >=0 && ogSlitList.get(tempRow).getTargetName().equals(targetName)){
			System.out.println(tempRow);
			nudgeSlitList.add(ogSlitList.get(tempRow));
			tempRow --;
		}
		tempRow = row +1;
		while (tempRow < CSU_NUMBER_OF_BAR_PAIRS && ogSlitList.get(tempRow).getTargetName().equals(targetName)){
			System.out.println(tempRow);
			nudgeSlitList.add(ogSlitList.get(tempRow));
			tempRow ++;
		}
		// Run through nudge slits and nudge.
		for(MechanicalSlit slit : nudgeSlitList) {
			double center = slit.getCenterPosition();
			slit.setCenterPosition(center-offset);
		}
		
		// Update the configuration.
		addSlitConfiguration(config);
  		setCurrentSlitConfiguration(config);	
	}
	//////////////////////////////////////////////////////////////////
}
