package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;

/**
 * <p>Title: MosfireParameters</p>
 * <p>Description: General constants file for the Mosfire Instrument. Values in this class can be overridden
      by setting values in XML config file (filename passed in as argument). </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class MosfireParameters {

	//. this class follows the singleton design pattern (gamma, et al, Design Patterns)
  //. only one instance of this class is allowed.
  private static MosfireParameters singleton = null;

  //. default colors
  public static Color COLOR_MSC_FIELD_BG = new java.awt.Color(189, 206, 230);
  public static Color COLOR_MSC_BAR_OUTLINE = new java.awt.Color(95, 103, 115, 50);
  public static Color COLOR_MSC_BAR_NUMBER = new java.awt.Color(0, 0, 100, 100);
  public static Color COLOR_MSC_BAR_STATUS_OK = new java.awt.Color(0, 0, 100, 100);
  public static Color COLOR_MSC_BAR_STATUS_ERROR = new java.awt.Color(100, 0, 0, 100);
  public static Color COLOR_LIMITED_SPECTRAL_COVERAGE = new Color(255,100,100,50);
  public static Color COLOR_SLIT = new Color(0,155,0,100);
  public static Color COLOR_SELECTED_SLIT = new Color(0,100,0,100);
  
  //. default fonts
  public static Font FONT_MSC_BAR_NUMBER = new Font("monospaced", Font.BOLD, 10);

	public static String DEFAULT_MSC_XSLT_URL = "http://irlab.astro.ucla.edu/mosfire/msc.xsl";
	public static String DEFAULT_MSC_XSLT_HOST = "irlab.astro.ucla.edu";
	public static File DEFAULT_MSC_XSLT_FILE = new File("msc.xsl");
  
	////////////////////////////////////////////////////////
  //  DETECTOR CONSTANTS
  ////////////////////////////////////////////////////////
  
	//Conversion between arcseconds and pixels
	public static double DETECTOR_ARCSEC_PER_PIXEL = 0.1798;
	
	//. total number of row and columns in detector
	public static int    DETECTOR_ROWS = 2048;
  public static int    DETECTOR_COLUMNS = 2048;

  // The usable number of pixels on the detector in each direction
	public static double DETECTOR_USABLE_PIXELS = 2040;

  public static double DETECTOR_WIDTH_ARCSECS = DETECTOR_USABLE_PIXELS * DETECTOR_ARCSEC_PER_PIXEL;
  public static double DETECTOR_HEIGHT_ARCSECS = DETECTOR_USABLE_PIXELS * DETECTOR_ARCSEC_PER_PIXEL;

	//. number of detector outputs
	public static int    DETECTOR_OUTPUTS = 32;

	//. clock time per pixel of detector 
  public static int    DETECTOR_PIXEL_CLOCK_TIME_MICROS = 10;
   
	////////////////////////////////////////////////////////
  //  CSU CONSTANTS
  ////////////////////////////////////////////////////////
  
	//. start older (pre-mascgen) CSU constants
  public static double CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN = 3.0;
  public static double CSU_SLIT_MINIMUM_DRAWN_WIDTH = 0.02;
  //. end older CSU constants
  
	//The number of bars on the CSU
	public static int CSU_NUMBER_OF_BAR_PAIRS = 46;

  // First, define some dimensions of the CSU.
	// We use dimensions as given in mm, but then multiply by the conversion
	// factor.
//	public static double CSU_ARCSEC_PER_MM = 1.37896;
	//. JLW: updated 2012-04-19
	public static double CSU_ARCSEC_PER_MM = 1.38028;
	public static double SINGLE_SLIT_HEIGHT_MM = 5.082;
	public static double SINGLE_SLIT_HEIGHT = SINGLE_SLIT_HEIGHT_MM * CSU_ARCSEC_PER_MM;
	public static double OVERLAP_MM = 0.698;
	public static double OVERLAP = OVERLAP_MM * CSU_ARCSEC_PER_MM;

	// The CSU has 46 rows for slits, spaced by 45 "full" overlap regions, 
	// with one extra "half" overlap region on the very top and another 
	// "half" overlap region on the very bottom. Thus, we compute the total
	// CSU height: 46 * SINGLE_SLIT_HEIGHT + 45 * OVERLAP + OVERLAP / 2 + 
	// OVERLAP / 2 = 46 * (SINGLE_SLIT_HEIGHT + OVERLAP).
	public static double CSU_ROW_HEIGHT = SINGLE_SLIT_HEIGHT + OVERLAP;
	public static double CSU_ROW_HEIGHT_MM = SINGLE_SLIT_HEIGHT_MM + OVERLAP_MM;
	public static double CSU_HEIGHT = CSU_NUMBER_OF_BAR_PAIRS * CSU_ROW_HEIGHT;
	public static double CSU_HEIGHT_MM = CSU_NUMBER_OF_BAR_PAIRS * CSU_ROW_HEIGHT_MM;

	// We assume the CSU to be square, with width = height.
	public static double CSU_WIDTH = CSU_HEIGHT;
	public static double CSU_WIDTH_MM = CSU_HEIGHT_MM;

	// Set the CSU Focal Plane radius.
	public static double CSU_FP_RADIUS = 6.9*30;
	public static double CSU_FP_RADIUS_MM = CSU_FP_RADIUS/CSU_ARCSEC_PER_MM;
	// Set the bar tilt angle in degrees.
	public static double CSU_SLIT_TILT_ANGLE = 4.0;
	// Set the bar tilt angle in radians.
	public static double CSU_SLIT_TILT_ANGLE_RADIANS = CSU_SLIT_TILT_ANGLE * Math.PI / 180.0;
	// Set the CSU zero point for offsets in mm
	public static double CSU_ZERO_PT = 137.40;
	
  //. for an open mask, bar values should be 270.4 and 4
  //. that's a slit width of 266.4 with a center of 133.2
  //. however, nick is still calculating, so we used a center position of 0, and
  //. adjust the slit width to stay within these limits
  public static double CSU_OPEN_MASK_CENTER_POSITION = 0.0;
  public static double CSU_OPEN_MASK_SLIT_WITH_MM = 266;    //. for now.  it might end up being CSU_WIDTH_MM
  public static double CSU_OPEN_MASK_SLIT_WIDTH = CSU_OPEN_MASK_SLIT_WITH_MM * CSU_ARCSEC_PER_MM;
  public static double CSU_MINIMUM_BAR_POSITION_MM = 4.0;
  public static double CSU_MAXIMUM_BAR_POSITION_MM = 270.4;

  public static double[] CSU_OPEN_BAR_TARGETS = {
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4,
  	4.0,
  	270.4    
  };

  //Don't let a star get within 20 arcseconds of the edge of the detector.
	public static double STAR_EDGE_DISTANCE = 20;

	public static double ALIGNMENT_BOX_SLIT_WIDTH = 4.0;
	
  //. MASCGEN defaults and limits
  public static int    SUGGESTED_MINIMUM_ALIGNMENT_STARS = 5;
	public static double MINIMUM_SLIT_WIDTH = 0.1999;
	public static double MAXIMUM_SLIT_WIDTH = CSU_OPEN_MASK_SLIT_WIDTH;

	// Set the default slit width, for use if invalid slit width is entered.
	public static double DEFAULT_SLIT_WIDTH = 0.7;
	public static double DEFAULT_XRANGE = 3.0;
	public static double DEFAULT_XCENTER = 0.0;
	public static double DEFAULT_DITHER_SPACE = 2.5;
	public static int    DEFAULT_X_STEPS = 10;
	public static double DEFAULT_X_STEPSIZE = 5.0;
	public static int    DEFAULT_Y_STEPS = 10;
	public static double DEFAULT_Y_STEPSIZE = 5.0;
	public static int    DEFAULT_PA_STEPS = 4;
	public static double DEFAULT_PA_STEPSIZE = 10.0;
	public static int    DEFAULT_ALIGNMENT_STARS = SUGGESTED_MINIMUM_ALIGNMENT_STARS;
	public static double DEFAULT_STAR_EDGE_BUFFER=2.0;
	public static double DEFAULT_SLIT_NOD = 1.5;
	public static double DEFAULT_ABAB_NOD = 1.5;
	public static double DEFAULT_ABAB_OFFSET = 1.2;

  public static int    CSU_DEFAULT_MAXIMUM_SLIT_LENGTH_IN_ROWS = 15;
  public static int    CSU_CALIBRATION_SLIT_MIDDLE_ROW = 23;
  
  
	//. start mask visualization (spectral format) constants)
  
	// The shift of the central wavelength in angstroms of the spectrum based off
  // a shift of the slit in mm
  public static double SHIFT_K =  12.209;
  public static double SHIFT_KS = 12.209;
  public static double SHIFT_H =   9.157;
  public static double SHIFT_J =   7.460;
  public static double SHIFT_Y =   6.216;

  // The minimum wavelength that fits on the detector with the
  // slit at the center of the field
  public static double MIN_LAM_K =  19524;
  public static double MIN_LAM_KS = 19524;
  public static double MIN_LAM_H =  14669;
  public static double MIN_LAM_J =  11150;
  public static double MIN_LAM_Y =   9256;

  // The maximum wavelength that fits on the detector with the
  // slit at the center of the field
  public static double MAX_LAM_K =  23970;
  public static double MAX_LAM_KS = 23970;
  public static double MAX_LAM_H =  18041;
  public static double MAX_LAM_J =  13810;
  public static double MAX_LAM_Y =  11473;

  //The lower wavelength half power point of the various filters
  public static double MIN_BAND_WIDTH_K =  19210;
  public static double MIN_BAND_WIDTH_KS = 19898;
  public static double MIN_BAND_WIDTH_H =  14645;
  public static double MIN_BAND_WIDTH_J =  11526;
  public static double MIN_BAND_WIDTH_Y =   9716;

  //The upper wavelength half power point of the various filters
  public static double MAX_BAND_WIDTH_K =  24060;
  public static double MAX_BAND_WIDTH_KS = 23040;
  public static double MAX_BAND_WIDTH_H =  18086;
  public static double MAX_BAND_WIDTH_J =  13522;
  public static double MAX_BAND_WIDTH_Y =  11247;

  //The dispersion in Angstroms/pix
  public static double DISPERSION_K_KS = 2.1691;
  public static double DISPERSION_H =    1.6269;
  public static double DISPERSION_J =    1.3028;
  public static double DISPERSION_Y =    1.0855;
  //. end mask visualization constants

  public static String[] FILTER_LIST = {"Open", "Y", "J", "H", "K", "Ks", "J2", "J3", "H1", "H2", "NB1061", "Dark"};
  public static int PUPIL_MECHANISM_TRACKING_RANGE_STEPS = 40200;
  public static int PUPIL_MECHANISM_TRACKING_RANGE_DEGREES = 300;
  public static double PUPIL_MECHANISM_STEPS_PER_DEGREE = PUPIL_MECHANISM_TRACKING_RANGE_STEPS/PUPIL_MECHANISM_TRACKING_RANGE_DEGREES;

  public static int DUST_COVER_MECHANISM_RANGE_STEPS = -14375;
  public static String DUST_COVER_POSITION_NAME_OPEN = "open";
  public static String DUST_COVER_POSITION_NAME_CLOSED = "closed";
  

  public static int EXPOSURE_MINIMUM_ITIME = 0;
  public static int EXPOSURE_MAXIMUM_ITIME_MS = Integer.MAX_VALUE;
  public static int EXPOSURE_MAXIMUM_ITIME = EXPOSURE_MAXIMUM_ITIME_MS/1000;
  public static int EXPOSURE_MINIMUM_COADDS = 1;
  public static int EXPOSURE_MAXIMUM_COADDS = 99999;
  public static int EXPOSURE_MINIMUM_READS = 1;
  public static int EXPOSURE_MAXIMUM_READS = 99999;
  public static int EXPOSURE_MINIMUM_GROUPS = 2;
  public static int EXPOSURE_MAXIMUM_GROUPS = 9999999;

  public static String ABORT_TYPE_IMMEDIATELY = "Abort Immediately";
  public static String ABORT_TYPE_AFTER_READ = "Abort After Read";
  public static String ABORT_TYPE_AFTER_GROUP = "Abort After Group";
  public static String ABORT_TYPE_AFTER_COADD = "Abort After Coadd";
  public static String ABORT_TYPE_AFTER_FRAME = "Abort After Frame";
  public static String ABORT_TYPE_AFTER_REPEAT = "Abort After Repeat";
  
  public static int ABORT_TYPE_IMMEDIATELY_VALUE = 1;
  public static int ABORT_TYPE_AFTER_READ_VALUE = 2;
  public static int ABORT_TYPE_AFTER_GROUP_VALUE = 3;
  public static int ABORT_TYPE_AFTER_COADD_VALUE = 4;
  public static int ABORT_TYPE_AFTER_FRAME_VALUE = 5;
  public static int ABORT_TYPE_AFTER_REPEAT_VALUE = 6;
  
  
  public static String[] ABORT_TYPE_LIST = {ABORT_TYPE_IMMEDIATELY, 
  																					ABORT_TYPE_AFTER_READ, 
  																					ABORT_TYPE_AFTER_GROUP, 
  																					ABORT_TYPE_AFTER_COADD,
  																					ABORT_TYPE_AFTER_FRAME,
  																					ABORT_TYPE_AFTER_REPEAT};
  
  public static String GO_IMMEDIATELY = "GO";
  public static String GO_WFMECHS = "WAIT & GO";
  public static String GO_APPLY = "APPLY";
  
  public static String[] GO_TYPE_LIST = {GO_WFMECHS, GO_IMMEDIATELY, GO_APPLY};
  
  public static String SAMPLING_MODE_SINGLE = "Single";
  public static String SAMPLING_MODE_CDS = "CDS";
  public static String SAMPLING_MODE_MCDS = "MCDS";
  public static String SAMPLING_MODE_UTR = "UTR";

    //   public static String[] SAMPLING_MODE_LIST = {SAMPLING_MODE_SINGLE, SAMPLING_MODE_CDS, SAMPLING_MODE_MCDS, SAMPLING_MODE_UTR}; 

  public static String[] SAMPLING_MODE_LIST = {SAMPLING_MODE_CDS, SAMPLING_MODE_MCDS,SAMPLING_MODE_UTR}; 
  
  public static String DITHER_PATTERN_SINGLE = "Stare"; 
  public static String DITHER_PATTERN_OBJECT_SKY = "Object+Sky";
  public static String DITHER_PATTERN_SKY_OBJECT = "Sky+Object";
  public static String DITHER_PATTERN_NOD = "Slit Nod";
  public static String DITHER_PATTERN_USER_NOD = "Mask Nod";
  public static String DITHER_PATTERN_BOX4 = "Box4";
  public static String DITHER_PATTERN_BOX5 = "Box5";
  public static String DITHER_PATTERN_BOX9 = "Box9";
  public static String DITHER_PATTERN_ABBA = "ABBA";
  public static String DITHER_PATTERN_ABAB = "ABA\'B\'";
	
  public static String DITHER_PATTERN_COORDS_SKY = "sky";
  public static String DITHER_PATTERN_COORDS_INSTRUMENT = "instr";
  public static String DITHER_PATTERN_COORDS_SLIT = "slit";
  public static String DITHER_PATTERN_COORDS_DEFAULT = DITHER_PATTERN_COORDS_INSTRUMENT;
  
  public static String[] DITHER_PATTERN_COORDS_LIST = {DITHER_PATTERN_COORDS_SKY, DITHER_PATTERN_COORDS_INSTRUMENT};
  
  
  public static String[] DITHER_PATTERN_LIST = {DITHER_PATTERN_SINGLE, 
																								DITHER_PATTERN_OBJECT_SKY,
																								DITHER_PATTERN_SKY_OBJECT,
  																							DITHER_PATTERN_NOD,
  																							DITHER_PATTERN_USER_NOD,
  																							DITHER_PATTERN_ABBA,
  																							DITHER_PATTERN_ABAB,
  																							DITHER_PATTERN_BOX4,
  																							DITHER_PATTERN_BOX5,
  																							DITHER_PATTERN_BOX9};
  
  
  public static String DITHER_PATTERN_DESCRIPTION_SINGLE = "A single frame taken without moving telescope.";
  public static String DITHER_PATTERN_DESCRIPTION_OBJECT_SKY = "First frame taken, then telescope offsetted user specified amount, then second frame taken.  Telescope is then offsetted back to original position.";
  public static String DITHER_PATTERN_DESCRIPTION_SKY_OBJECT = "Telescope offsetted user specified amount, first frame taken, then telescope offsetted back and second frame taken.";
  public static String DITHER_PATTERN_DESCRIPTION_NOD = "Frame taken at evenly distributed positions on slit. The number of total nod positions is the value in the \"Nod Positions\" box.  Positions alternate between top of top half and top of bottom half, moving down." +
  																											"  If the number of positions is odd, the middle position is taken first.";
  public static String DITHER_PATTERN_DESCRIPTION_USER_NOD = "Frames taken at positions evenly distributed on slit.  The number of total nod positions is the value in the \"Nod Positions\" box.  The maximum nod is plus/minus the value in the \"slit nod\" box.  Positions alternate between top of top half and top of bottom half, moving down." +
  																											"  If the number of positions is odd, the middle position is taken first.";
  public static String DITHER_PATTERN_DESCRIPTION_ABBA = "First frame is taken with image nodded above current position by value specified in the \"Nod Amplitude\" box, followed by two frames taken at that value below the original position, finally ending with another image at the first position.";
  public static String DITHER_PATTERN_DESCRIPTION_ABAB = "Four frames are taken in this order: A, B, A\', B\', such that the separation between A and B is the same as A\' and B\'.  Outside Nod Amplitude specifies the distance from the current position to the A and B\' positions, and the Inside Nod Amplitude is the distance from the current position to the A\' and B positions.  The distance of each AB and A\'B\' nod is therefore the sum of the two amplitudes, and the offset between nod pairs is the difference of the two amplitudes.";
  public static String DITHER_PATTERN_DESCRIPTION_BOX4 = " Frames taken in an overlapping box pattern with 4 frames in a 2x2 grid.";
  public static String DITHER_PATTERN_DESCRIPTION_BOX5 = " Frames taken in an overlapping box pattern with 5 frames in a 2x2 grid with one in the center.";
  public static String DITHER_PATTERN_DESCRIPTION_BOX9 = " Frames taken in an overlapping box pattern with 9 frames in a 3x3 grid.";
  
  public static String MOSFIRE_PROPERTY_MODIFY_ERROR = "ModifyError";
  public static String MOSFIRE_PROPERTY_MAGMA_SCRIPT_REPLY = "MagmaReplyMessage";
  public static String MOSFIRE_PROPERTY_MAGMA_SCRIPT_QUESTION = "MagmaQuestionMessage";
  public static String MOSFIRE_PROPERTY_SCRIPT_REPLY = "ScriptReplyMessage";
  public static String MOSFIRE_PROPERTY_SCRIPT_QUESTION = "ScriptQuestionMessage";
  public static String MOSFIRE_PROPERTY_SCRIPT_ERROR = "ScriptErrorMessage";
  public static String MOSFIRE_PROPERTY_SCRIPT_WARNING = "ScriptWarningMessage";
  public static String MOSFIRE_PROPERTY_SCRIPT_WAIT = "ScriptWaitFlag";
  public static String MOSFIRE_PROPERTY_SCRIPT_RUNNING = "ScriptRunningFlag";
  public static String MOSFIRE_PROPERTY_DATASET_STATUS = "DatasetStatus";
  public static String MOSFIRE_PROPERTY_CAMERA_READY = "CameraReady";
  public static String MOSFIRE_PROPERTY_EXPOSURE_IN_PROGRESS = "ExposureInProgress";
  public static String MOSFIRE_PROPERTY_EXPOSURE_READ_TIME = "ExposureReadTime";
  public static String MOSFIRE_PROPERTY_EXPOSURE_RESETS = "ExposureResets";
  public static String MOSFIRE_PROPERTY_EXPOSURE_IGNORED_READS = "ExposureIgnoredReads";
  public static String MOSFIRE_PROPERTY_MECH_PUPIL_ROTATOR_LOCATION = "MechPupilRotatorLocation";
  public static String MOSFIRE_PROPERTY_MECH_PUPIL_ROTATOR_POSITION = "MechPupilRotatorPosition";
  public static String MOSFIRE_PROPERTY_MECH_DUST_COVER_LOCATION = "MechHatchCoverLocation";
  public static String MOSFIRE_PROPERTY_MECH_DUST_COVER_POSITION = "MechHatchCoverPosition";
  public static String MOSFIRE_PROPERTY_MECH_DUST_COVER_TARGET_NAME = "MechHatchCoverTarget";
  public static String MOSFIRE_PROPERTY_FCS_RANGE_PERCENTAGE = "FlexureCompensationRangePercentage";
  
  public static String CSU_BAR_STATUS_OK = "OK";
  public static String CSU_BAR_STATUS_DISABLED = "DISABLED";
  public static String CSU_BAR_STATUS_MOVING = "MOVING";
  public static String CSU_BAR_STATUS_ERROR = "ERROR";
  public static String CSU_BAR_STATUS_UNKNOWN = "UNKNOWN";
  public static String CSU_BAR_STATUS_SETUP = "SETUP";
  
  public static String PROCESS_MESSAGE_ID_DELIMITER = ";";

  
  private MosfireParameters() {
    //. private constructor as per singleton design pattern
  }

  public static MosfireParameters getInstance() {
    //. method to get instance of this singleton class

    //. if not yet defined, instantiate a new class
    if (singleton == null) {
      singleton = new MosfireParameters();
    }
    //. return instance
    return singleton;
  }

}
