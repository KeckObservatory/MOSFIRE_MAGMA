package edu.ucla.astro.irlab.mosfire.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.ucla.astro.irlab.util.NumberFormatters;


public class MascgenArgumentsNEW {
	private String maskName;
	private String targetList;
	private double xRange;       //. arcmin
	private double xCenter;      //. arcmin
	private double slitWidth;    //. arcsec
	private double ditherSpace;  //. arcsec
	private RaDec centerPosition;
	private boolean useCenterOfPriority;
	private int xSteps;
	private double xStepSize;    //. arcsec
	private int ySteps;  
	private double yStepSize;    //. arcsec
	private double centerPA;     //. degrees
	private int paSteps;
	private double paStepSize;   //. degrees
	private int minimumAlignmentStars;
	private double alignmentStarEdgeBuffer;  //. arcsec
	
	private String outputDirectory;
	private String outputSubdirectory;
	private boolean outputSubdirectoryMaskName=true;
	private boolean autonameOutputFiles=true;
	private String outputMascgenParams;
	private String outputAllTargets;
	private String outputMaskTargets;
	private String outputMSC;
	private String outputMaskScript;
	private String outputAlignMaskScript;
	private String outputSlitList;
	private String outputDS9Regions;
	private String outputStarList;
	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
	private String outputExcessTargets;

	public static final String XML_ROOT = "mascgenArguments";
	public static final String XML_ELEMENT_MASK_NAME = "maskName";
	public static final String XML_ELEMENT_INPUTS = "inputs";
	public static final String XML_INPUT_ATTRIBUTE_TARGET_LIST = "targetList";
	public static final String XML_INPUT_ATTRIBUTE_XRANGE = "xrange";
	public static final String XML_INPUT_ATTRIBUTE_XCENTER = "xcenter";
	public static final String XML_INPUT_ATTRIBUTE_SLIT_WIDTH = "slitWidthArcsec";
	public static final String XML_INPUT_ATTRIBUTE_DITHER_SPACE = "ditherSpace";
	public static final String XML_ELEMENT_CENTER = "center";
	public static final String XML_CENTER_ATTRIBUTE_RAH = "raHours";
	public static final String XML_CENTER_ATTRIBUTE_RAM = "raMinutes";
	public static final String XML_CENTER_ATTRIBUTE_RAS = "raSeconds";
	public static final String XML_CENTER_ATTRIBUTE_DECD = "decDegrees";
	public static final String XML_CENTER_ATTRIBUTE_DECM = "decMinutes";
	public static final String XML_CENTER_ATTRIBUTE_DECS = "decSeconds";
	public static final String XML_CENTER_ATTRIBUTE_PA = "positionAngle";
	public static final String XML_CENTER_ATTRIBUTE_USE_COP = "useCenterOfPriority";
	public static final String XML_ELEMENT_STEPS = "steps";
  public static final String XML_STEPS_ATTRIBUTE_XSTEPS = "xSteps";
  public static final String XML_STEPS_ATTRIBUTE_XSTEP_SIZE = "xStepsSize";
  public static final String XML_STEPS_ATTRIBUTE_YSTEPS = "ySteps";
  public static final String XML_STEPS_ATTRIBUTE_YSTEP_SIZE = "yStepsSize";
  public static final String XML_STEPS_ATTRIBUTE_PA_STEPS = "paSteps";
  public static final String XML_STEPS_ATTRIBUTE_PA_STEP_SIZE = "paStepsSize";
	public static final String XML_INPUT_ATTRIBUTE_MINIMUM_ALIGNMENT_STARS = "minAlignStars";
	public static final String XML_INPUT_ATTRIBUTE_ALIGN_STAR_EDGE = "starDitherSpace";
	public static final String XML_ELEMENT_OUTPUTS = "outputs";
	public static final String XML_OUTPUT_ATTRIBUTE_AUTONAME_OUTPUT = "autonameOutput";
	public static final String XML_ELEMENT_OUTPUT_DIRECTORY = "directory";
	public static final String XML_OUTPUT_ATTRIBUTE_ROOT_DIRECTORY = "root";
	public static final String XML_OUTPUT_ATTRIBUTE_MASK_SUBDIRECTORY = "subdirectory";
	public static final String XML_OUTPUT_ATTRIBUTE_USE_MASK_NAME_SUBDIRECTORY = "useMaskNameSubdir";
	public static final String XML_OUTPUT_ATTRIBUTE_MSC = "mscFile";
	public static final String XML_OUTPUT_ATTRIBUTE_SCIENCE = "science";
	public static final String XML_OUTPUT_ATTRIBUTE_ALIGN = "align";
	public static final String XML_ELEMENT_OUTPUT_MASK_SCRIPT = "maskScript";
	public static final String XML_OUTPUT_ATTRIBUTE_PARAMS = "mascgenParams";
	public static final String XML_OUTPUT_ATTRIBUTE_ALL_TARGETS = "allTargets";
	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
	public static final String XML_OUTPUT_ATTRIBUTE_EXCESS_TARGETS = "unusedTargets";
	public static final String XML_OUTPUT_ATTRIBUTE_MASK_TARGETS = "targetsInMask";
	public static final String XML_OUTPUT_ATTRIBUTE_SLIT_LIST = "slitList";
	public static final String XML_OUTPUT_ATTRIBUTE_STAR_LIST = "starList";
	public static final String XML_OUTPUT_ATTRIBUTE_DS9_REGIONS = "ds9Regions";
	

	public MascgenArgumentsNEW() {
		this("default");
	}

	public MascgenArgumentsNEW(String maskName) {
		this(maskName, "", MosfireParameters.DEFAULT_XRANGE, MosfireParameters.DEFAULT_XCENTER, 
				MosfireParameters.DEFAULT_SLIT_WIDTH, MosfireParameters.DEFAULT_DITHER_SPACE, new RaDec(), true, 
				MosfireParameters.DEFAULT_X_STEPS, MosfireParameters.DEFAULT_X_STEPSIZE,
				MosfireParameters.DEFAULT_Y_STEPS, MosfireParameters.DEFAULT_Y_STEPSIZE,
				0.0, MosfireParameters.DEFAULT_PA_STEPS, MosfireParameters.DEFAULT_PA_STEPSIZE,
				MosfireParameters.DEFAULT_ALIGNMENT_STARS, MosfireParameters.DEFAULT_STAR_EDGE_BUFFER);
		constructOutputFilenames();
	}
	public MascgenArgumentsNEW(String maskName, String targetList, double xRange, double xCenter,
			double slitWidth, double ditherSpace, RaDec centerPosition, boolean useCenterOfPriority, int xSteps,
			double xStepSize, int ySteps, double yStepSize, double centerPA,
			int paSteps, double paStepSize, int minimumAlignmentStars,
			double alignmentStarEdgeBuffer) {
		this.maskName = maskName;	
		this.targetList = targetList;
		this.xRange = xRange;
		this.xCenter = xCenter;
		this.slitWidth = slitWidth;
		this.ditherSpace = ditherSpace;
		this.centerPosition = centerPosition;
		this.xSteps = xSteps;
		this.xStepSize = xStepSize;
		this.ySteps = ySteps;
		this.yStepSize = yStepSize;
		this.centerPA = centerPA;
		this.paSteps = paSteps;
		this.paStepSize = paStepSize;
		this.minimumAlignmentStars = minimumAlignmentStars;
		this.alignmentStarEdgeBuffer = alignmentStarEdgeBuffer;
		this.useCenterOfPriority = useCenterOfPriority;
		constructOutputFilenames();
	}
	private void constructOutputFilenames() {
		outputDirectory="";
		outputSubdirectory = maskName;
		outputMascgenParams = maskName+".param";
		outputAllTargets = maskName+"_orig.coords";
		outputMaskTargets = maskName+".coords";
		// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
		outputExcessTargets = maskName+"_excess.coords";
		outputMSC = maskName+".xml";
		outputMaskScript = maskName+"_BarPositions.csh";
		outputAlignMaskScript = maskName+"_AlignmentBarPositions.csh";
		outputSlitList = maskName+"_SlitList.txt";
		outputDS9Regions = maskName+"_SlitRegions.reg";
		outputStarList = maskName+"_StarList.txt";			
	}

	public MascgenArgumentsNEW(String maskName, String targetList, double xRange, double xCenter,
			double slitWidth, double ditherSpace, RaDec centerPosition, boolean useCenterOfPriority, int xSteps,
			double xStepSize, int ySteps, double yStepSize, double centerPA,
			int paSteps, double paStepSize, int minimumAlignmentStars,
			double alignmentStarEdgeBuffer, String outputDirectory,
			String outputSubdirectory, boolean outputSubdirectoryMaskName,
			boolean autonameOutputFiles, String outputMascgenParams,
			String outputMaskTargets, String outputMSC, String outputAlignMSC,
			String outputMaskScript, String outputAlignMaskScript,
			String outputBarPositions, String outputAlignBarPositions,
			String outputSlitList, String outputDS9Regions, String outputStarList) {
		
		this(maskName, targetList, xRange, xCenter, slitWidth, ditherSpace, centerPosition, useCenterOfPriority, xSteps, xStepSize,
				ySteps, yStepSize, centerPA, paSteps, paStepSize, minimumAlignmentStars, alignmentStarEdgeBuffer);

		this.outputDirectory = outputDirectory;
		this.outputSubdirectory = outputSubdirectory;
		this.outputSubdirectoryMaskName = outputSubdirectoryMaskName;
		this.autonameOutputFiles = autonameOutputFiles;
		this.outputMascgenParams = outputMascgenParams;
		this.outputMaskTargets = outputMaskTargets;
		this.outputMSC = outputMSC;
		this.outputMaskScript = outputMaskScript;
		this.outputAlignMaskScript = outputAlignMaskScript;
		this.outputSlitList = outputSlitList;
		this.outputDS9Regions = outputDS9Regions;
		this.outputStarList = outputStarList;
	}

	public String getTargetList() {
		return targetList;
	}
	public void setTargetList(String targetList) {
		this.targetList = targetList;
	}
	public double getxRange() {
		return xRange;
	}
	public void setxRange(double xRange) {
		this.xRange = xRange;
	}
	public double getxCenter() {
		return xCenter;
	}
	public void setxCenter(double xCenter) {
		this.xCenter = xCenter;
	}
	public double getSlitWidth() {
		return slitWidth;
	}
	public void setSlitWidth(double slitWidth) {
		this.slitWidth = slitWidth;
	}
	public double getDitherSpace() {
		return ditherSpace;
	}
	public void setDitherSpace(double ditherSpace) {
		this.ditherSpace = ditherSpace;
	}
	public RaDec getCenterPosition() {
		return centerPosition;
	}
	public void setCenterPosition(RaDec centerPosition) {
		this.centerPosition = centerPosition;
	}
	public int getxSteps() {
		return xSteps;
	}
	public void setxSteps(int xSteps) {
		this.xSteps = xSteps;
	}
	public double getxStepSize() {
		return xStepSize;
	}
	public void setxStepSize(double xStepSize) {
		this.xStepSize = xStepSize;
	}
	public int getySteps() {
		return ySteps;
	}
	public void setySteps(int ySteps) {
		this.ySteps = ySteps;
	}
	public double getyStepSize() {
		return yStepSize;
	}
	public void setyStepSize(double yStepSize) {
		this.yStepSize = yStepSize;
	}
	public double getCenterPA() {
		return centerPA;
	}
	public void setCenterPA(double centerPA) {
		this.centerPA = centerPA;
	}
	public void setUseCenterOfPriority(boolean useCenterOfPriority) {
		this.useCenterOfPriority = useCenterOfPriority;
	}
	public boolean usesCenterOfPriority() {
		return useCenterOfPriority;
	}
	public int getPaSteps() {
		return paSteps;
	}
	public void setPaSteps(int paSteps) {
		this.paSteps = paSteps;
	}
	public double getPaStepSize() {
		return paStepSize;
	}
	public void setPaStepSize(double paStepSize) {
		this.paStepSize = paStepSize;
	}
	public int getMinimumAlignmentStars() {
		return minimumAlignmentStars;
	}
	public void setMinimumAlignmentStars(int minimumAlignmentStars) {
		this.minimumAlignmentStars = minimumAlignmentStars;
	}
	public double getAlignmentStarEdgeBuffer() {
		return alignmentStarEdgeBuffer;
	}
	public void setAlignmentStarEdgeBuffer(double alignmentStarEdgeBuffer) {
		this.alignmentStarEdgeBuffer = alignmentStarEdgeBuffer;
	}
	public String getMaskName() {
		return maskName;
	}
	public void setMaskName(String maskName) {
		this.maskName = maskName;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getOutputSubdirectory() {
		return outputSubdirectory;
	}

	public String getFullPathOutputSubdirectory() {
		return getFullPath("");
	}
	public void setOutputSubdirectory(String outputSubdirectory) {
		this.outputSubdirectory = outputSubdirectory;
	}

	public boolean isOutputSubdirectoryMaskName() {
		return outputSubdirectoryMaskName;
	}

	public void setOutputSubdirectoryMaskName(boolean outputSubdirectoryMaskName) {
		this.outputSubdirectoryMaskName = outputSubdirectoryMaskName;
	}

	public boolean isAutonameOutputFiles() {
		return autonameOutputFiles;
	}

	public void setAutonameOutputFiles(boolean autonameOutputFiles) {
		this.autonameOutputFiles = autonameOutputFiles;
	}

	public String getOutputMascgenParams() {
		return outputMascgenParams;
	}
	public String getFullPathOutputMascgenParams() {
		return getFullPath(outputMascgenParams);
	}

	public void setOutputMascgenParams(String outputMascgenParams) {
		this.outputMascgenParams = outputMascgenParams;
	}

	public String getOutputMaskTargets() {
		return outputMaskTargets;
	}
	public String getFullPathOutputMaskTargets() {
		return getFullPath(outputMaskTargets);
	}

	public void setOutputMaskTargets(String outputMaskTargets) {
		this.outputMaskTargets = outputMaskTargets;
	}

	public String getOutputAllTargets() {
		return outputAllTargets;
	}
	public String getFullPathOutputAllTargets() {
		return getFullPath(outputAllTargets);
	}

	public void setOutputAllTargets(String outputAllTargets) {
		this.outputAllTargets = outputAllTargets;
	}
	public String getOutputMSC() {
		return outputMSC;
	}
	public String getFullPathOutputMSC() {
		return getFullPath(outputMSC);
	}

	public void setOutputMSC(String outputMSC) {
		this.outputMSC = outputMSC;
	}

	public String getOutputMaskScript() {
		return outputMaskScript;
	}
	public String getFullPathOutputMaskScript() {
		return getFullPath(outputMaskScript);
	}

	public void setOutputMaskScript(String outputMaskScript) {
		this.outputMaskScript = outputMaskScript;
	}

	public String getOutputAlignMaskScript() {
		return outputAlignMaskScript;
	}
	public String getFullPathOutputAlignMaskScript() {
		return getFullPath(outputAlignMaskScript);
	}

	public void setOutputAlignMaskScript(String outputAlignMaskScript) {
		this.outputAlignMaskScript = outputAlignMaskScript;
	}

	public String getOutputSlitList() {
		return outputSlitList;
	}
	public String getFullPathOutputSlitList() {
		return getFullPath(outputSlitList);
	}

	public void setOutputSlitList(String outputSlitList) {
		this.outputSlitList = outputSlitList;
	}

	public String getOutputDS9Regions() {
		return outputDS9Regions;
	}
	public String getFullPathOutputDS9Regions() {
		return getFullPath(outputDS9Regions);
	}

	public void setOutputDS9Regions(String outputDS9Regions) {
		this.outputDS9Regions = outputDS9Regions;
	}

	public String getOutputStarList() {
		return outputStarList;
	}
	public String getFullPathOutputStarList() {
		return getFullPath(outputStarList);
	}

	public void setOutputStarList(String outputStarList) {
		this.outputStarList = outputStarList;
	}
  private String getFullPath(String filename) {
  	String subdir = (outputSubdirectory.isEmpty() ? "" : outputSubdirectory);
  	String file = (filename.isEmpty() ? filename : File.separator+filename);
  	return outputDirectory+File.separator+subdir+file;
  }
	public static MascgenArgumentsNEW readMascgenParamFile(File paramFile, ArrayList<String> outWarningList) throws JDOMException, IOException, DataConversionException {
		
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
		org.jdom.Document myDoc = builder.build(paramFile);

		//. get root element.
		Element root=myDoc.getRootElement();
		return getMascgenArgsFromElement(root, outWarningList);
	}
	public static MascgenArgumentsNEW getMascgenArgsFromElement(Element root, ArrayList<String> outWarningList)  throws JDOMException, DataConversionException {
		Attribute workingAtt;
		//. check that root is a MascgenArguments element
		if (root.getName().compareTo(XML_ROOT) != 0)
			throw new JDOMException("Root element must be "+XML_ROOT);

		MascgenArgumentsNEW args = new MascgenArgumentsNEW();
		
		//. get children elements
		@SuppressWarnings("unchecked")
		//. JDOM doesn't support generics, but getChidren returns a List of Elements
		List<Element> elements=root.getChildren();
    //. loop through them
    for (Element current : elements) {
    	//. check
      if (current.getName().compareTo(XML_ELEMENT_MASK_NAME) == 0) {
      	args.setMaskName(current.getValue());
      } else if (current.getName().compareTo(XML_ELEMENT_INPUTS) == 0) {
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_TARGET_LIST);
      	if (workingAtt != null) {
    			args.setTargetList(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_TARGET_LIST+"> attribute.");
      	}
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_XRANGE);
      	if (workingAtt != null) {
    			args.setxRange(workingAtt.getDoubleValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_XRANGE+"> attribute.");
      	}
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_XCENTER);
      	if (workingAtt != null) {
    			args.setxCenter(workingAtt.getDoubleValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_XCENTER+"> attribute.");
      	}
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_SLIT_WIDTH);
      	if (workingAtt != null) {
    			args.setSlitWidth(workingAtt.getDoubleValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_SLIT_WIDTH+"> attribute.");
      	}
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_DITHER_SPACE);
      	if (workingAtt != null) {
    			args.setDitherSpace(workingAtt.getDoubleValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_DITHER_SPACE+"> attribute.");
      	}
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_MINIMUM_ALIGNMENT_STARS);
      	if (workingAtt != null) {
    			args.setMinimumAlignmentStars(workingAtt.getIntValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_MINIMUM_ALIGNMENT_STARS+"> attribute.");
      	}
      	workingAtt = current.getAttribute(XML_INPUT_ATTRIBUTE_ALIGN_STAR_EDGE);
      	if (workingAtt != null) {
    			args.setAlignmentStarEdgeBuffer(workingAtt.getDoubleValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_INPUTS+" element does not have a <"+XML_INPUT_ATTRIBUTE_ALIGN_STAR_EDGE+"> attribute.");
      	}

    		@SuppressWarnings("unchecked")
    		//. JDOM doesn't support generics, but getChidren returns a List of Elements
      	List<Element> inputElements = current.getChildren();
      	for (Element inputCurrent : inputElements) {
      		if (inputCurrent.getName().compareTo(XML_ELEMENT_CENTER) == 0) {
      			RaDec raDec = args.getCenterPosition();
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_RAH);
          	if (workingAtt != null) {
        			raDec.setRaHour(workingAtt.getIntValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_RAH+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_RAM);
          	if (workingAtt != null) {
        			raDec.setRaMin(workingAtt.getIntValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_RAM+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_RAS);
          	if (workingAtt != null) {
        			raDec.setRaSec(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_RAS+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_DECD);
          	if (workingAtt != null) {
        			raDec.setDecDeg(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_DECD+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_DECM);
          	if (workingAtt != null) {
        			raDec.setDecMin(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_DECM+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_DECS);
          	if (workingAtt != null) {
        			raDec.setDecSec(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_DECS+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_PA);
          	if (workingAtt != null) {
        			args.setCenterPA(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_PA+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_CENTER_ATTRIBUTE_USE_COP);
          	if (workingAtt != null) {
        			args.setUseCenterOfPriority(workingAtt.getBooleanValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_CENTER+" element does not have a <"+XML_CENTER_ATTRIBUTE_USE_COP+"> attribute.");
          	}
      		} else if (inputCurrent.getName().compareTo(XML_ELEMENT_STEPS) == 0) {
          	workingAtt = inputCurrent.getAttribute(XML_STEPS_ATTRIBUTE_XSTEPS);
          	if (workingAtt != null) {
        			args.setxSteps(workingAtt.getIntValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_STEPS+" element does not have a <"+XML_STEPS_ATTRIBUTE_XSTEPS+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_STEPS_ATTRIBUTE_XSTEP_SIZE);
          	if (workingAtt != null) {
        			args.setxStepSize(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_STEPS+" element does not have a <"+XML_STEPS_ATTRIBUTE_XSTEP_SIZE+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_STEPS_ATTRIBUTE_YSTEPS);
          	if (workingAtt != null) {
        			args.setySteps(workingAtt.getIntValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_STEPS+" element does not have a <"+XML_STEPS_ATTRIBUTE_YSTEPS+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_STEPS_ATTRIBUTE_YSTEP_SIZE);
          	if (workingAtt != null) {
        			args.setyStepSize(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_STEPS+" element does not have a <"+XML_STEPS_ATTRIBUTE_YSTEP_SIZE+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_STEPS_ATTRIBUTE_PA_STEPS);
          	if (workingAtt != null) {
        			args.setPaSteps(workingAtt.getIntValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_STEPS+" element does not have a <"+XML_STEPS_ATTRIBUTE_PA_STEPS+"> attribute.");
          	}
          	workingAtt = inputCurrent.getAttribute(XML_STEPS_ATTRIBUTE_PA_STEP_SIZE);
          	if (workingAtt != null) {
        			args.setPaStepSize(workingAtt.getDoubleValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_STEPS+" element does not have a <"+XML_STEPS_ATTRIBUTE_PA_STEP_SIZE+"> attribute.");
          	}

      		} else {
          	outWarningList.add("Unknown element: "+inputCurrent.getName()+". Ignoring.");
      		}
      	}  //. end loop over inputs children
      } else if (current.getName().compareTo(XML_ELEMENT_OUTPUTS) == 0) {
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_AUTONAME_OUTPUT);
      	if (workingAtt != null) {
    			args.setAutonameOutputFiles(workingAtt.getBooleanValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_AUTONAME_OUTPUT+"> attribute.");
      	}
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_PARAMS);
      	if (workingAtt != null) {
    			args.setOutputMascgenParams(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_PARAMS+"> attribute.");
      	}
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_MSC);
      	if (workingAtt != null) {
    			args.setOutputMSC(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_MSC+"> attribute.");
      	}
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_MASK_TARGETS);
      	if (workingAtt != null) {
    			args.setOutputMaskTargets(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_MASK_TARGETS+"> attribute.");
      	}
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_ALL_TARGETS);
      	if (workingAtt != null) {
    			args.setOutputAllTargets(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_ALL_TARGETS+"> attribute.");
      	}
      	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
    	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_EXCESS_TARGETS);
      	if (workingAtt != null) {
    			args.setOutputExcessTargets(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_EXCESS_TARGETS+"> attribute.");
      	}
      	// End of modification
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_SLIT_LIST);
      	if (workingAtt != null) {
    			args.setOutputSlitList(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_SLIT_LIST+"> attribute.");
      	}
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_STAR_LIST);
      	if (workingAtt != null) {
    			args.setOutputStarList(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_STAR_LIST+"> attribute.");
      	}
       	workingAtt = current.getAttribute(XML_OUTPUT_ATTRIBUTE_DS9_REGIONS);
      	if (workingAtt != null) {
    			args.setOutputDS9Regions(workingAtt.getValue());
      	} else {
      		outWarningList.add(XML_ELEMENT_OUTPUTS+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_DS9_REGIONS+"> attribute.");
      	}

    		@SuppressWarnings("unchecked")
    		//. JDOM doesn't support generics, but getChidren returns a List of Elements
      	List<Element> outputElements = current.getChildren();
      	for (Element outputCurrent : outputElements) {
      		
      		if (outputCurrent.getName().compareTo(XML_ELEMENT_OUTPUT_DIRECTORY) == 0) {
           	workingAtt = outputCurrent.getAttribute(XML_OUTPUT_ATTRIBUTE_ROOT_DIRECTORY);
          	if (workingAtt != null) {
        			args.setOutputDirectory(workingAtt.getValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_OUTPUT_DIRECTORY+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_ROOT_DIRECTORY+"> attribute.");
          	}
           	workingAtt = outputCurrent.getAttribute(XML_OUTPUT_ATTRIBUTE_MASK_SUBDIRECTORY);
          	if (workingAtt != null) {
        			args.setOutputSubdirectory(workingAtt.getValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_OUTPUT_DIRECTORY+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_MASK_SUBDIRECTORY+"> attribute.");
          	}
           	workingAtt = outputCurrent.getAttribute(XML_OUTPUT_ATTRIBUTE_USE_MASK_NAME_SUBDIRECTORY);
          	if (workingAtt != null) {
        			args.setOutputSubdirectoryMaskName(workingAtt.getBooleanValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_OUTPUT_DIRECTORY+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_USE_MASK_NAME_SUBDIRECTORY+"> attribute.");
          	}
      		} else if (outputCurrent.getName().compareTo(XML_ELEMENT_OUTPUT_MASK_SCRIPT) == 0) {
           	workingAtt = outputCurrent.getAttribute(XML_OUTPUT_ATTRIBUTE_SCIENCE);
          	if (workingAtt != null) {
        			args.setOutputMaskScript(workingAtt.getValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_OUTPUT_MASK_SCRIPT+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_SCIENCE+"> attribute.");
          	}
           	workingAtt = outputCurrent.getAttribute(XML_OUTPUT_ATTRIBUTE_ALIGN);
          	if (workingAtt != null) {
        			args.setOutputAlignMaskScript(workingAtt.getValue());
          	} else {
          		outWarningList.add(XML_ELEMENT_OUTPUT_MASK_SCRIPT+" element does not have a <"+XML_OUTPUT_ATTRIBUTE_ALIGN+"> attribute.");
          	}
      		} else {
      			outWarningList.add("Unknown element: "+outputCurrent.getName()+". Ignoring.");
      		}
      		
      	}  //. end loop over output children
      } else {
      	outWarningList.add("Unknown element: "+current.getName()+". Ignoring.");
      }      
    }  //. end loop over root children
    return args;
	}
	public  void writeMascgenParamFile(File file) throws JDOMException, IOException {
		writeMascgenParamFile(file, this);
	}
	public static void writeMascgenParamFile(File file, MascgenArgumentsNEW args) throws JDOMException, IOException {
		//. todo: versioning?    
    Document doc = new Document(getMascgenArgumentsRootElement(args));

	  XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	  outputter.output(doc, new java.io.FileOutputStream(file));
	}
	public static Element getMascgenArgumentsRootElement(MascgenArgumentsNEW args) {
		final DecimalFormat wholeNumberFormatter = NumberFormatters.StandardFloatFormatter(0);  
		final DecimalFormat oneDigitFormatter = NumberFormatters.StandardFloatFormatter(1);  
		final DecimalFormat twoDigitFormatter = NumberFormatters.StandardFloatFormatter(2);  
		final DecimalFormat twoDigitWholeNumberFormatter = new DecimalFormat("00");
		final DecimalFormat degreeSecondFormatter = new DecimalFormat("00.00");

		//. root element 
    Element root = new Element(XML_ROOT);

	    Element maskNameElement = new Element(XML_ELEMENT_MASK_NAME);
	    maskNameElement.addContent(args.maskName);
	    
	    root.addContent(maskNameElement);
	    
	    Element inputElement = new Element(XML_ELEMENT_INPUTS);
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_TARGET_LIST, args.getTargetList());
	    inputElement.addContent(new Comment("NOTE: X Range and X Center units are arcmins, Slit width, Dither Space, and Star Dither Space units are arcsecs"));
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_XRANGE, Double.toString(args.getxRange()));
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_XCENTER, Double.toString(args.getxCenter()));
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_SLIT_WIDTH, Double.toString(args.getSlitWidth()));
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_DITHER_SPACE, Double.toString(args.getDitherSpace()));
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_MINIMUM_ALIGNMENT_STARS, Integer.toString(args.getMinimumAlignmentStars()));
	    inputElement.setAttribute(XML_INPUT_ATTRIBUTE_ALIGN_STAR_EDGE, Double.toString(args.getAlignmentStarEdgeBuffer()));
	    
	    Element centerElement = new Element(XML_ELEMENT_CENTER);
	    RaDec raDec = args.getCenterPosition();
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_RAH, twoDigitWholeNumberFormatter.format(raDec.getRaHour()));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_RAM, twoDigitWholeNumberFormatter.format(raDec.getRaMin()));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_RAS, degreeSecondFormatter.format(raDec.getRaSec()));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_DECD, twoDigitWholeNumberFormatter.format(raDec.getDecDeg()));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_DECM, twoDigitWholeNumberFormatter.format(raDec.getDecMin()));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_DECS, degreeSecondFormatter.format(raDec.getDecSec()));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_PA, Double.toString(args.centerPA));
	    centerElement.setAttribute(XML_CENTER_ATTRIBUTE_USE_COP, Boolean.toString(args.usesCenterOfPriority()));

	    inputElement.addContent(centerElement);
	    
	    inputElement.addContent(new Comment("NOTE: Step size units are arcsecs for X and Y and degrees for PA"));
	    Element stepsElement = new Element(XML_ELEMENT_STEPS);
	    stepsElement.setAttribute(XML_STEPS_ATTRIBUTE_XSTEPS, Integer.toString(args.getxSteps()));
	    stepsElement.setAttribute(XML_STEPS_ATTRIBUTE_XSTEP_SIZE, Double.toString(args.getxStepSize()));
	    stepsElement.setAttribute(XML_STEPS_ATTRIBUTE_YSTEPS, Integer.toString(args.getySteps()));
	    stepsElement.setAttribute(XML_STEPS_ATTRIBUTE_YSTEP_SIZE, Double.toString(args.getyStepSize()));
	    stepsElement.setAttribute(XML_STEPS_ATTRIBUTE_PA_STEPS, Integer.toString(args.getPaSteps()));
	    stepsElement.setAttribute(XML_STEPS_ATTRIBUTE_PA_STEP_SIZE, Double.toString(args.getPaStepSize()));
	    
	    inputElement.addContent(stepsElement);
	    
	    root.addContent(inputElement);
	    
	    Element outputElement = new Element(XML_ELEMENT_OUTPUTS);
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_AUTONAME_OUTPUT, Boolean.toString(args.isAutonameOutputFiles()));
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_PARAMS, args.getOutputMascgenParams());
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_MSC, args.getOutputMSC());
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_ALL_TARGETS, args.getOutputAllTargets());
      	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_EXCESS_TARGETS, args.getOutputExcessTargets());
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_MASK_TARGETS, args.getOutputMaskTargets());
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_SLIT_LIST, args.getOutputSlitList());
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_STAR_LIST, args.getOutputStarList());
	    outputElement.setAttribute(XML_OUTPUT_ATTRIBUTE_DS9_REGIONS, args.getOutputDS9Regions());
	    
	    Element directoryElement = new Element(XML_ELEMENT_OUTPUT_DIRECTORY);
	    directoryElement.setAttribute(XML_OUTPUT_ATTRIBUTE_ROOT_DIRECTORY, args.getOutputDirectory());
	    directoryElement.setAttribute(XML_OUTPUT_ATTRIBUTE_MASK_SUBDIRECTORY, args.getOutputSubdirectory());
	    directoryElement.setAttribute(XML_OUTPUT_ATTRIBUTE_USE_MASK_NAME_SUBDIRECTORY, Boolean.toString(args.isOutputSubdirectoryMaskName()));
	    
	    outputElement.addContent(directoryElement);
	    
	    Element maskScriptElement = new Element(XML_ELEMENT_OUTPUT_MASK_SCRIPT);
	    maskScriptElement.setAttribute(XML_OUTPUT_ATTRIBUTE_SCIENCE, args.getOutputMaskScript());
	    maskScriptElement.setAttribute(XML_OUTPUT_ATTRIBUTE_ALIGN, args.getOutputAlignMaskScript());

	    outputElement.addContent(maskScriptElement);

	    root.addContent(new Comment("MASCGEN output directories, filenames and settings"));
	    root.addContent(outputElement);
	    
	    return root;

	}
	
	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
	public String getOutputExcessTargets() {
		return outputExcessTargets;
	}

	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
	public void setOutputExcessTargets(String outputExcessTargets) {
		this.outputExcessTargets = outputExcessTargets;
	}
	
	// Part of MAGMA UPGRADE M4 by Ji Man Sohn, UCLA 2016-2017
	public String getFullPathOutputExcessTargets() {
		return getFullPath(outputExcessTargets);
	}
	// Part of MAGMA UPGRADE M6
	public MascgenArgumentsNEW clone(String newName) {
		return new MascgenArgumentsNEW(newName,this.targetList, this.xRange, 
									this.xCenter, this.slitWidth, this.ditherSpace, 
									this.centerPosition, this.useCenterOfPriority, 
									this.xSteps, this.xStepSize, this.ySteps, this.yStepSize,
									this.centerPA, this.paSteps, this.paStepSize, 
									this.minimumAlignmentStars, this.alignmentStarEdgeBuffer);
	}
}
