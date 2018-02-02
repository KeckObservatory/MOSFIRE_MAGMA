package edu.ucla.astro.irlab.mosfire.mscgui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.transform.TransformerException;

import nom.tam.fits.FitsException;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import edu.hawaii.keck.kjava.KJavaException;
import edu.ucla.astro.irlab.mosfire.util.AstroObj;
import edu.ucla.astro.irlab.mosfire.util.MascgenArgumentException;
import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;
import edu.ucla.astro.irlab.mosfire.util.MascgenResult;
import edu.ucla.astro.irlab.mosfire.util.MechanicalSlit;
import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.mosfire.util.SlitConfiguration;
import edu.ucla.astro.irlab.mosfire.util.SlitPosition;
import edu.ucla.astro.irlab.mosfire.util.TargetListFormatException;
import edu.ucla.astro.irlab.mosfire.util.TargetListParser;

import edu.ucla.astro.irlab.util.DoubleProperty;
import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.KJavaPropertyManager;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;
import edu.ucla.astro.irlab.util.NumberFormatters;
import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.StringProperty;
import edu.ucla.astro.irlab.util.gui.GenericModel;
import edu.ucla.astro.irlab.util.process.ProcessControl;
import edu.ucla.astro.irlab.util.process.ProcessInfo;
import edu.ucla.astro.irlab.util.process.ProcessListener;


/**
 * <p>Title: MSCGUIModel</p>
 * <p>Description: Model class for MOSFIRE CSU Control GUI</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
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
	private boolean lastMaskSetupIsAlign=false;
	private boolean online = false;
	private SimpleDateFormat scriptDatFormatter = new SimpleDateFormat("yyMMdd_HHmmss");
	private MascgenChangeListener mascgenListener = new MascgenChangeListener();
	MascgenRunThread mascgenThread;
	private double currentSlitWidth;
	private boolean sameSlitWidthForAllSlits = true;
	private int csuReady = 0;
	private String csuStatus = "";
	private StringProperty extFilenameProp;
	private PropertyList propertyList;
	private KJavaPropertyManager propertyManager;
	private double minimumCloseOffSlitWidth;
	private double closedOffSlitWidth;
	private int maximumSlitLength;
	private int closeOffType;
	private boolean mascgenReassignUnusedSlits;
	private ArrayList<String> processOutputMessages = new ArrayList<String>();
	public static final int CLOSE_OFF_TYPE_DO_NOTHING = 1;
	public static final int CLOSE_OFF_TYPE_REDUCE_IN_PLACE = 2;
	public static final int CLOSE_OFF_TYPE_CLOSE_OFF = 3;
	public MSCGUIModel(PropertyList propList, boolean online) throws Exception {
		propertyList = propList;
		currentSlitConfiguration.getMascgenArgs().setOutputDirectory(MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY.getCanonicalPath());
		currentMascgenResult = new MascgenResult();
		scriptRunning=false;
		myProcessControl = new ProcessControl();
		myProcessControl.addProcessListener(new MSCGUIProcessListener());
		currentSlitConfigurationIndex = -1;
		scriptDirectory = MSCGUIParameters.DEFAULT_EXECUTED_MASK_CONFIGURATION_DIRECTORY;
		currentSlitWidth = MosfireParameters.DEFAULT_SLIT_WIDTH;
		minimumCloseOffSlitWidth = MSCGUIParameters.DEFAULT_MINIMUM_CLOSE_OFF_SLIT_WIDTH;
		closedOffSlitWidth = MSCGUIParameters.DEFAULT_CLOSED_OFF_SLIT_WIDTH;
		maximumSlitLength = MSCGUIParameters.DEFAULT_MAXIMUM_SLIT_LENGTH;
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
	public StringProperty getMosfireLastAliveProperty() {
		StringProperty prop = (StringProperty)propertyList.getProperty("MGSAlive");
		return prop;
	}
	public StringProperty getMDSLastAliveProperty() {
		StringProperty prop = (StringProperty)propertyList.getProperty("MDSAlive");
		return prop;
	}
	public StringProperty getMCSUSLastAliveProperty() {
		StringProperty prop = (StringProperty)propertyList.getProperty("MCSUSAlive");
		return prop;
	}
	public void abortMascgen() {
		if (mascgenThread != null) {
			if (mascgenThread.isAlive()) {
				mascgen.abort();
  		}
  	}
  	setMascgenArgumentException(new MascgenArgumentException("MASCGEN was aborted by user."));
  }
  public void startMascgen(MascgenArguments args) {
  	mascgenThread = new MascgenRunThread(args);
  	mascgenThread.start();
  }
  public void runMascgen(MascgenArguments args) throws MascgenArgumentException {
		try {
			mascgen.setMascgenStatus("Reading target list", mascgenListener);
			ArrayList<AstroObj> astroObjArrayList = TargetListParser.parseFile(args.getTargetList());
			
			// remove a center line
			// must be first line in the file, name = CENTER, Priority=9999
			if (astroObjArrayList.get(0).getObjName() == "CENTER" && 
					astroObjArrayList.get(0).getObjPriority() == 9999){
				astroObjArrayList.remove(0);
			}
	  	MascgenResult result = mascgen.run(astroObjArrayList, args, mascgenListener);
	  	if (result.getAstroObjects().length > 0) {
	  		SlitConfiguration newConfig = SlitConfiguration.generateSlitConfiguration(args, result, mascgenReassignUnusedSlits);
	  		newConfig.setOriginalTargetSet(astroObjArrayList);
	  		addSlitConfiguration(newConfig);
	  		setCurrentSlitConfiguration(newConfig);
	  	} else {
	  		throw new MascgenArgumentException("No valid configuration found.");
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
  }
  public void openLongSlitConfiguration(double slitWidth) {
  	SlitConfiguration config = SlitConfiguration.createLongSlitConfiguration(maximumSlitLength, slitWidth);
  	addSlitConfiguration(config);
  	setCurrentSlitConfiguration(config);
  }
  public void openOpenMaskSlitConfiguration() {
  	SlitConfiguration config = SlitConfiguration.createOpenMaskSlitConfiguration();
  	addSlitConfiguration(config);
  	setCurrentSlitConfiguration(config);
  }
  public void openSlitConfiguration(File mscFile, ArrayList<String> outWarningList) throws JDOMException, IOException  {
  	SlitConfiguration newConfig = new SlitConfiguration();
  	newConfig.readSlitConfiguration(mscFile, outWarningList);
		try {
			ArrayList<AstroObj> astroObjArrayList = TargetListParser.parseFile(newConfig.getMascgenArgs().getTargetList());
			
			// remove a center line
			// must be first line in the file, name = CENTER, Priority=9999
			if (astroObjArrayList.get(0).getObjName() == "CENTER" && 
					astroObjArrayList.get(0).getObjPriority() == 9999){
				astroObjArrayList.remove(0);
			}
			newConfig.setOriginalTargetSet(astroObjArrayList);
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
  	addSlitConfiguration(newConfig);
		setCurrentSlitConfiguration(newConfig);  	
  }
  public void closeSlitConfiguration(int index) {
  	openedSlitConfigurations.remove(index);
		propertyChangeListeners.firePropertyChange("openedSlitConfigurations", null, openedSlitConfigurations);	

		if (index == openedSlitConfigurations.size()) {
			setCurrentSlitConfigurationIndex(index-1);
		} else {
			setCurrentSlitConfigurationIndex(index);			
		}
  }
  public void copySlitConfiguration(int index) {
  	SlitConfiguration newConfiguration = currentSlitConfiguration.clone();
  	int ii=1;
  	while (getSlitConfigurationIndex(newConfiguration.getMaskName()) != -1) {
  		newConfiguration.setMaskName(currentSlitConfiguration.getMaskName()+" ("+ii+")");
  		ii++;
  	}
  	addSlitConfiguration(newConfiguration);
  }
  private void addSlitConfiguration(SlitConfiguration config) {
  	int index = getSlitConfigurationIndex(config.getMaskName());
  	if (index < 0) {
  		openedSlitConfigurations.add(config);
  		index = openedSlitConfigurations.size()-1;
  	} else {
  		openedSlitConfigurations.set(index, config);
  	}
		propertyChangeListeners.firePropertyChange("openedSlitConfigurations", null, openedSlitConfigurations);	

		setCurrentSlitConfigurationIndex(index);
  }
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
	public boolean hasUnsavedSlitConfigurationsOpened() {
		for (SlitConfiguration config : openedSlitConfigurations) {
			if (!config.getStatus().equals(SlitConfiguration.STATUS_SAVED) && !config.getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				return true;
			}
		}
		return false;
	}
  public void writeCurrentSlitConfigurationOutputs(boolean writeHTML) throws JDOMException, IOException, TransformerException {
  	//. create output directory
  	currentSlitConfiguration.writeMascgenParams();
  	currentSlitConfiguration.writeCoordsFile();
  	currentSlitConfiguration.writeSlitConfiguration();
  	if (writeHTML) {
  		currentSlitConfiguration.writeSlitConfigurationHTML();
  	}
  	currentSlitConfiguration.writeOutSlitList();
  	currentSlitConfiguration.writeScienceCSUScript(false);
  	currentSlitConfiguration.writeOutStarList();
  	currentSlitConfiguration.writeDS9Regions();

  	if (currentSlitConfiguration.getAlignmentStarCount() > 0) {
  		currentSlitConfiguration.writeAlignmentCSUScript(false);
  	}
  }
  
  public void openTargetList(File targetListFile) throws FileNotFoundException, IOException, NumberFormatException, TargetListFormatException {
  	targetList = TargetListParser.parseFile(targetListFile);
  }  
	public ArrayList<AstroObj> getTargetList() {
  	return targetList;
  }
  public void clearTargetList() {
  	targetList = new ArrayList<AstroObj>();
  }

  
  private String constructMaskScriptFilename(boolean isAlign, boolean isExtra, String extension) {
  	//. format is dir/YYMMDD_HHMMSS_maskName(_align).csh
  	StringBuffer buffer = new StringBuffer(scriptDirectory.getAbsolutePath()+File.separator);
  	buffer.append(scriptDatFormatter.format(Calendar.getInstance().getTime()));
  	buffer.append("_");
  	buffer.append(currentSlitConfiguration.getMaskName());
  	if (isAlign) {
  		buffer.append("_align");
  	}
  	if (isExtra) {
  		buffer.append("_extra");
  	}
  	buffer.append(".");
  	buffer.append(extension);
  	return buffer.toString();
  }
	public void executeMaskSetup(boolean doAlign) throws IOException, InterruptedException {
		setScriptRunning(true);
		//. make sure a mask is configured

		//. make sure alignment mask has been run already?
		try {
			//. check directory existence
			if (scriptDirectory.exists()) {
				if (scriptDirectory.canWrite()) {
					String scriptFilename;
					File scriptFile;
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
							scriptFile.setExecutable(true);
							runMaskScript(scriptFilename);
							//. sleep a little to give time to finish
							Thread.currentThread().sleep(1000);
						}
					}

					scriptFilename = constructMaskScriptFilename(doAlign, false, "csh");
					if (doAlign) {
						currentSlitConfiguration.writeAlignmentCSUScript(scriptFilename, false);
					} else {
						currentSlitConfiguration.writeScienceCSUScript(scriptFilename, false);
					}
					scriptFile = new File(scriptFilename);
					//. make file executable?
					scriptFile.setExecutable(true);
					runMaskScript(scriptFilename);
					setLastMaskSetupIsAlign(doAlign);
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
	private void runMaskScript(String maskSetupScriptFilename) throws IOException, InterruptedException {
		//. run script
		long id = System.currentTimeMillis();
		String[] command = {maskSetupScriptFilename};

		ProcessInfo pi = new ProcessInfo(command, id);
		execute(pi, MSCGUIParameters.SCRIPT_EXECUTE_SHOW_DIALOG);
		currentCommandProcessID = pi.getIdNumber();
	}
	public void execute(ProcessInfo pi) throws IOException, InterruptedException {
		execute(pi, true);
	}
	public void execute(ProcessInfo pi, boolean showDialog) throws IOException, InterruptedException {
		System.out.println("executing <"+pi.getCommandString()+">.");
		if (!MSCGUIParameters.ENGINEERING_MODE) {
			processOutputMessages.clear();
			myProcessControl.execute(pi);
		} else {
			setScriptRunning(false);
		}
	}
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
	
  public void writeMSCFile(File file) throws IOException, JDOMException {
  	currentSlitConfiguration.writeSlitConfiguration(file);
  }
  public void writeMSCHtmlFile(File file) throws TransformerException, MalformedURLException {
   	currentSlitConfiguration.writeSlitConfigurationHTML(file);
  }
  public void readMSCFile(File file) throws IOException, JDOMException {
//  	slitList.readSlitConfiguration(file);
  }
  public SlitConfiguration getCurrentSlitConfiguration() {
  	return currentSlitConfiguration;
  }
  public void setCurrentSlitConfiguration(SlitConfiguration currentSlitConfiguration) {
  	SlitConfiguration oldValue = this.currentSlitConfiguration;
		this.currentSlitConfiguration = currentSlitConfiguration;
		propertyChangeListeners.firePropertyChange("currentSlitConfiguration",
				oldValue, currentSlitConfiguration);
	}

	public SlitPosition getSlitPosition(int row) throws ArrayIndexOutOfBoundsException {
  	return (currentSlitConfiguration.getSlitPosition(row));
  }
	public int getActiveRow() {
		return activeRow;
	}
	public void setActiveRow(int activeRow) {
		int oldActiveRow = this.activeRow;
		this.activeRow = activeRow;
		propertyChangeListeners.firePropertyChange("activeRow", new Integer(oldActiveRow), new Integer(activeRow));	
	}
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
	public void setActiveObject(AstroObj obj) {
		if (obj.getObjPriority() < 0) {
			for (MechanicalSlit slit : currentSlitConfiguration.getAlignSlitList()) {
				if (slit.getTarget().getObjName().equals(obj.getObjName())) {
					setActiveSlitRow(slit.getSlitNumber());
					return;
				}
			}
		} else {
			int ii=0;
			for (MechanicalSlit slit : currentSlitConfiguration.getMechanicalSlitList()) {
				if (slit.getTarget().getObjName().equals(obj.getObjName())) {
					setActiveRow(ii);
					return;
				}
				ii++;
			}
		}
	}
  public void moveActiveSlit(double offset) {
  	try {
  		SlitPosition pos = currentSlitConfiguration.getSlitPosition(activeRow);
  		pos.setCenterPosition(pos.getCenterPosition()+offset);
  	} catch (ArrayIndexOutOfBoundsException aioobEx) {
  		//. shouldn't happen
  		aioobEx.printStackTrace();
  	}
  }
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
  
  public MascgenResult getCurrentMascgenResult() {
  	return currentMascgenResult;
  }
  public void setCurrentMascgenResult(MascgenResult currentMascgenResult) {
  	MascgenResult oldValue = this.currentMascgenResult;
		this.currentMascgenResult = currentMascgenResult;
		propertyChangeListeners.firePropertyChange("currentMascgenResult",
				oldValue, currentMascgenResult);
  }
  
	protected void processExited(ProcessInfo process, int code) {
		if (process.getIdNumber() == currentCommandProcessID) {
			if (code != 0) {
				propertyChangeListeners.firePropertyChange("processErrorOutput", null, processOutputMessages);
			}
		}
	}
	public boolean isScriptRunning() {
		return scriptRunning;
	}
	private void setScriptRunning(Boolean scriptRunning) {
		Boolean oldValue = this.scriptRunning;
		this.scriptRunning = scriptRunning;
		propertyChangeListeners.firePropertyChange(MosfireParameters.MOSFIRE_PROPERTY_SCRIPT_RUNNING, oldValue, scriptRunning);
	}
 
	public void setLoadedMaskSetup(String loadedMaskSetup) {
		String oldValue = this.loadedMaskSetup;
		this.loadedMaskSetup = loadedMaskSetup;
		propertyChangeListeners.firePropertyChange("loadedMaskSetup",	oldValue, loadedMaskSetup);
	}
	
	public String getLoadedMaskSetup() {
		return loadedMaskSetup;
	}

	public void setScriptDirectory(File scriptDirectory) {
		File oldValue = this.scriptDirectory;
		this.scriptDirectory = scriptDirectory;
		propertyChangeListeners.firePropertyChange("scriptDirectory", oldValue, scriptDirectory);
	}

	public File getScriptDirectory() {
		return scriptDirectory;
	}

	public void setOnline(boolean online) {
		boolean oldValue = this.online;
		this.online = online;
		propertyChangeListeners.firePropertyChange("online", oldValue, online);
	}

	public boolean isOnline() {
		return online;
	}

	public void setLastMaskSetupIsAlign(boolean lastMaskSetupIsAlign) {
		this.lastMaskSetupIsAlign = lastMaskSetupIsAlign;
	}

	public boolean isLastMaskSetupIsAlign() {
		return lastMaskSetupIsAlign;
	}

	public void setMascgenArgumentException(MascgenArgumentException ex) {
		propertyChangeListeners.firePropertyChange("mascgenArgumentException", null, ex);
	}

	public void setCurrentSlitWidth(double currentSlitWidth) {
		if (!currentSlitConfiguration.getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
			double oldValue = this.currentSlitWidth;
			this.currentSlitWidth = currentSlitWidth;
			propertyChangeListeners.firePropertyChange("currentSlitWidth",
					oldValue, currentSlitWidth);
			if (currentSlitWidth != currentSlitConfiguration.getMascgenArgs().getSlitWidth()) {
				if (sameSlitWidthForAllSlits) {
					currentSlitConfiguration.setSlitWidth(currentSlitWidth);
					propertyChangeListeners.firePropertyChange("currentSlitConfiguration",
							null, currentSlitConfiguration);
				}
			}
		}
	}
	public double getCurrentSlitWidth() {
		return currentSlitWidth;
	}

	public void setSameSlitWidthForAllSlits(boolean sameSlitWidthForAllSlits) {
		boolean oldValue = this.sameSlitWidthForAllSlits;
		this.sameSlitWidthForAllSlits = sameSlitWidthForAllSlits;
		propertyChangeListeners.firePropertyChange("sameSlitWidthForAllSlits",
				oldValue, sameSlitWidthForAllSlits);
	}

	public boolean isSameSlitWidthForAllSlits() {
		return sameSlitWidthForAllSlits;
	}
	public int getCsuReady() {
		return csuReady;
	}
	public void setCsuReady(int csuReady) {
		int oldValue = this.csuReady;
		this.csuReady = csuReady;
		propertyChangeListeners.firePropertyChange("csuReady", new Integer(oldValue), new Integer (csuReady));
	}
	public String getCsuStatus() {
		return csuStatus;
	}
	public void setCsuStatus(String csuStatus) {
		String oldValue = this.csuStatus;
		this.csuStatus = csuStatus;
		propertyChangeListeners.firePropertyChange("csuStatus",
				oldValue, csuStatus);
	}
	public double getMinimumCloseOffSlitWidth() {
		return minimumCloseOffSlitWidth;
	}
	public void setMinimumCloseOffSlitWidth(double minimumCloseOffSlitWidth) {
		this.minimumCloseOffSlitWidth = minimumCloseOffSlitWidth;
	}
	public double getClosedOffSlitWidth() {
		return closedOffSlitWidth;
	}
	public void setClosedOffSlitWidth(double closedOffSlitWidth) {
		this.closedOffSlitWidth = closedOffSlitWidth;
	}
	public int getMaximumSlitLength() {
		return maximumSlitLength;
	}
	public void setMaximumSlitLength(int maximumSlitLength) {
		this.maximumSlitLength = maximumSlitLength;
	}
	public void setCloseOffType(int closeOffType) {
		int oldValue = this.closeOffType;
		this.closeOffType = closeOffType;
		propertyChangeListeners.firePropertyChange("closeOffType",
				oldValue, closeOffType);
	}
	public int getCloseOffType() {
		return closeOffType;
	}
	public void setMascgenReassignUnusedSlits(boolean mascgenReassignUnusedSlits) {
		this.mascgenReassignUnusedSlits = mascgenReassignUnusedSlits;
	}
	public boolean isMascgenReassignUnusedSlits() {
		return mascgenReassignUnusedSlits;
	}
	private void initializeValuesFromProperties() {
		setCsuStatus((String)(propertyList.getProperty("CSUStatus").getValue()));
		setLoadedMaskSetup((String)(propertyList.getProperty("CSUSetupMaskName").getValue()));
		setCsuReady(((Integer)(propertyList.getProperty("CSUMoveReadiness").getValue())).intValue());
	}
	private void registerWithProperties() throws NoSuchPropertyException {
		propertyList.registerListener("CSUStatus", this);
		propertyList.registerListener("CSUMoveReadiness", this);		
		propertyList.registerListener("CSUSetupMaskName", this);		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().compareTo("CSUStatus") == 0) {
			setCsuStatus((String)(evt.getNewValue()));
		} else if (evt.getPropertyName().compareTo("CSUMoveReadiness") == 0) {
			setCsuReady(((Integer)(evt.getNewValue())).intValue());
		} else if (evt.getPropertyName().compareTo("CSUSetupMaskName") == 0) {
				setLoadedMaskSetup((String)(evt.getNewValue()));
		}
	}
	public void setKJavaPropertyManager(KJavaPropertyManager manager) {
		propertyManager = manager;
	}
	public void stopCShow() throws KJavaException {
		if (propertyManager != null) {
			propertyManager.stop();
		}
	}
	private class MSCGUIProcessListener implements ProcessListener {
		//. all we care about here are exit codes
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

	private class MascgenChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			propertyChangeListeners.firePropertyChange(evt);
		}		
	}
  
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
}
