package edu.ucla.astro.irlab.mosfire.util;

import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_HEIGHT;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.OVERLAP;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.SINGLE_SLIT_HEIGHT;

public class AstroObj implements Cloneable {

	private String objName; // Object name.
	private double objPriority; // Object priority.
	private double objMag;
	private double objX; // Object x coodinate in arc seconds in CSU space.
	private double objY; // Object y coordinate in arc seconds in CSU space.
	private int objRR = -1; // RowRegion occupied by Object.
	private int objOR = -1; // OverlapRegion occupied by Object.
	private double raHour;
	private double raMin;
	private double raSec;
	private double decDeg;
	private double decMin;
	private double decSec;
	private double epoch;
	private double equinox;
	private double centerDistance;
	private double wcsX;  //. x-coordinate in arc seconds using center point
	private double wcsY;  //. y-coordinate in arc seconds using center point
	private final static String BLANK_NAME = "none"; 
	private boolean inValidSlit = false;
	private int minRow;
	private int maxRow;
	//. note: objX calculated from wcsX by following:
	//. xOld = wcsX - centerPosition.getXCoordinate();
	//. yOld = wcsY - centerPosition.getYCoordinate();
	//. theta = pa in radians
	//. objX = xOld * Math.cos(theta) - yOld * Math.sin(theta) + CSU_WIDTH / 2);
	//. objY = xOld * Math.sin(theta)	+ yOld * Math.cos(theta) + CSU_HEIGHT / 2);

	java.text.DecimalFormat secondPlace = new java.text.DecimalFormat("00.00");
	java.text.DecimalFormat wholePlace = new java.text.DecimalFormat("00");
	java.text.DecimalFormat wholeDecPlace = new java.text.DecimalFormat("+00;-00");

	
	/** Constructor to initialize an AstroObj with given values. **/
	public AstroObj(String name, double priority, double mag, double raHour,
			double raMin, double raSec, double decDeg, double decMin, 
			double decSec, double epoch, double equinox) {
		this.objName = name;
		this.objPriority = priority;
		this.objMag = mag;
		this.raHour = raHour;
		this.raMin = raMin;
		this.raSec = raSec;
		this.decDeg = decDeg;
		this.decMin = decMin;
		this.decSec = decSec;
		this.epoch = epoch;
		this.equinox = equinox;
	}

	public AstroObj() {
		this(BLANK_NAME);
	}

	public AstroObj(String name) {
		this(name, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

	}

	public AstroObj clone() {
		try {
			return (AstroObj) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
	}

	public String printObj(){
		return objName + "\t\t" + objPriority + "\t\t" + objX + 
				"\t\t" + objY + "\t\t" +
				"RR: " + objRR + 
				"\t\tOR: " + objOR;
	}

	public void printObjRaDec() {
		System.out.print(objName + "\t\t" + objPriority + "\t\t" + raHour + 
				"\t\t" + raMin + "\t\t" + raSec + "\t\t" + decDeg + "\t\t" +
				decMin + "\t\t" + decSec +
		"\n");
	}
	public String toString() {
		return objName+"-"+objPriority;
	}

	public String raDecToStringWithUnits() {
		return wholePlace.format(raHour)+"h "+wholePlace.format(raMin)+"m "+secondPlace.format(raSec)+"s   "+wholePlace.format(decDeg)+"d "+wholePlace.format(decMin)+"m "+secondPlace.format(decSec)+"s";
	}

	public String raToStringWithUnits() {
		return wholePlace.format(raHour)+"h "+wholePlace.format(raMin)+"m "+secondPlace.format(raSec)+"s";
	}
	public String decToStringWithUnits() {
		return wholePlace.format(decDeg)+"d "+wholePlace.format(decMin)+"m "+secondPlace.format(decSec)+"s";
	}
	public String raToString() {
		return wholePlace.format(raHour)+":"+wholePlace.format(raMin)+":"+secondPlace.format(raSec)+"";
	}
	public String decToString() {
/*
		String sign = "+";
		if (decDeg == 0.0) {
			if (Double.toString(decDeg).equals("-0.0")) {
				sign = "-";
			}
		} else {
			if (decDeg < 0.0) {
				sign = "-";
			}
		}
		*/
		return wholeDecPlace.format(decDeg)+":"+wholePlace.format(decMin)+":"+secondPlace.format(decSec)+"";
	}
	
	/** Return the name of the AstroObj. **/
	public String getObjName() {
		return objName;
	}

	public void setObjName(String name) {
		objName = name;
	}

	/** Return the priority of the AstroObj. **/
	public double getObjPriority() {
		return objPriority;
	}

	public void setObjPriority(double num) {
		objPriority = num;
	}

	public void setObjMag(double num) {
		objMag = num;
	}

	public double getObjMag() {
		return objMag;
	}

	/** Return the x coordinate of the AstroObj. **/
	public double getObjX() {
		return objX;
	}

	/** Set the x coordinate of the AstroObj. **/
	public void setObjX(double num) {
		this.objX = num;
	}

	/** Return the y coordinate of the AstroObj. **/
	public double getObjY() {
		return objY;
	}

	/** Set the y coordinate of the AstroObj. **/
	public void setObjY(double num) {
		this.objY = num;
	}
	public void setInitialRR(double ditherSpace) {
		setObjRR(getRow(objY, ditherSpace));
	}
	public void setInitialOR(double ditherSpace) {
		setObjOR(getOverlapRow(objY, ditherSpace));

	}
	public static int getRow(double objYInCSUCoords, double ditherSpace) {
		double rowRegionHeight = SINGLE_SLIT_HEIGHT - 2 * ditherSpace;
		double deadSpace = ditherSpace + OVERLAP / 2;
		double rowAsDouble = (objYInCSUCoords / MosfireParameters.CSU_ROW_HEIGHT);
		//. objY should have had CSU-HEIGHT/2 subtracted from it
		int row = (int)Math.floor(rowAsDouble);
		if ((rowAsDouble >= 0) && (row < MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS)) {

			double rowMin = row + deadSpace / MosfireParameters.CSU_ROW_HEIGHT;
			double rowMax = row + (deadSpace + rowRegionHeight) / MosfireParameters.CSU_ROW_HEIGHT; 
			if (rowAsDouble > rowMin && rowAsDouble < rowMax) {
				return row;
			}
		}
		return -1;
	}
	public static int getOverlapRow(double objYInCSUCoords, double ditherSpace) {
		double rowRegionHeight = SINGLE_SLIT_HEIGHT - 2 * ditherSpace;
		double deadSpace = ditherSpace + OVERLAP / 2;
		double rowAsDouble = (objYInCSUCoords - rowRegionHeight - deadSpace) / MosfireParameters.CSU_ROW_HEIGHT;
		//. objY should have had CSU-HEIGHT/2 subtracted from it
		int row = (int)Math.floor(rowAsDouble);
		if ((rowAsDouble >= 0) && (row < (MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS-1))) {

			double rowMax = row + (2*deadSpace) / MosfireParameters.CSU_ROW_HEIGHT; 
			if (rowAsDouble < rowMax) {
				return row+1;
			}
		}
		return -1;

	}
	public void updateDitherRows(double ditherSpace) {
		double maxY = objY + ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
		double minY = objY - ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
		setMinRow((int)Math.floor((minY + CSU_HEIGHT / 2. - MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT));
		setMaxRow((int)Math.floor((maxY + CSU_HEIGHT / 2. + MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT));
	}
	/** Set the Object's RowRegion number. **/
	public void setObjRR(int num) {
		this.objRR = num;
	}

	/** Return the Object's RowRegion number. **/
	public int getObjRR() {
		return objRR;
	}

	/** Set the Object's OverlapRegion number. **/
	public void setObjOR(int num) {
		this.objOR = num;
	}

	/** Return the Object's OverlapRegion number. **/
	public int getObjOR() {
		return objOR;
	}

	public void setRaHour(double num) {
		raHour = num;
	}

	public double getRaHour() {
		return raHour;
	}

	public void setRaMin(double num) {
		raMin = num;
	}

	public double getRaMin() {
		return raMin;
	}

	public void setRaSec(double num) {
		raSec = num;
	}

	public double getRaSec() {
		return raSec;
	}
	public void setDecDeg(double num) {
		decDeg = num;
	}

	public double getDecDeg() {
		return decDeg;
	}

	public void setDecMin(double num) {
		decMin = num;
	}

	public double getDecMin() {
		return decMin;
	}

	public void setDecSec(double num) {
		decSec = num;
	}

	public double getDecSec() {
		return decSec;
	}

	public void setWcsX(double num) {
		wcsX = num;
	}

	public double getWcsX() {
		return wcsX;
	}

	public void setWcsY(double num) {
		wcsY = num;
	}

	public double getWcsY() {
		return wcsY;
	}

	public void setEpoch(double num) {
		epoch = num;
	}

	public double getEpoch() {
		return epoch;
	}

	public void setEquinox(double num) {
		equinox = num;
	}

	public double getEquinox() {
		return equinox;
	}

	public void setInValidSlit(boolean inValidSlit) {
		this.inValidSlit = inValidSlit;
	}

	public boolean isInValidSlit() {
		return inValidSlit;
	}

	/** Return true if the Object's name is not BLANK_NAME. **/
	public boolean isNotBlank() {
		return !isBlank();
	}

	/** Return true if the Object's name is BLANK_NAME. **/
	public boolean isBlank() {
		return (objName.equals(BLANK_NAME));
	}
	public void setMinRow(int minRow) {
		this.minRow = minRow;
	}
	public int getMinRow() {
		return minRow;
	}
	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}
	public int getMaxRow() {
		return maxRow;
	}

	public void setCenterDistance(double centerDistance) {
		this.centerDistance = centerDistance;
	}

	public double getCenterDistance() {
		return centerDistance;
	}
	public RaDec getRaDec() {
		return new RaDec((int)raHour, (int)raMin, raSec, decDeg, decMin, decSec);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(decDeg);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(decMin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(decSec);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((objName == null) ? 0 : objName.hashCode());
		temp = Double.doubleToLongBits(raHour);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(raMin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(raSec);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AstroObj other = (AstroObj) obj;
		if (Double.doubleToLongBits(decDeg) != Double
				.doubleToLongBits(other.decDeg))
			return false;
		if (Double.doubleToLongBits(decMin) != Double
				.doubleToLongBits(other.decMin))
			return false;
		if (Double.doubleToLongBits(decSec) != Double
				.doubleToLongBits(other.decSec))
			return false;
		if (objName == null) {
			if (other.objName != null)
				return false;
		} else if (!objName.equals(other.objName))
			return false;
		if (Double.doubleToLongBits(raHour) != Double
				.doubleToLongBits(other.raHour))
			return false;
		if (Double.doubleToLongBits(raMin) != Double.doubleToLongBits(other.raMin))
			return false;
		if (Double.doubleToLongBits(raSec) != Double.doubleToLongBits(other.raSec))
			return false;
		return true;
	}
	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	// * This method returns a clean copy of AstroObj element		// 
	//	 without any x,y data										//
	public AstroObj getCleanAstroObj(){
		AstroObj newobj = new AstroObj();
		newobj.setObjName(this.getObjName());
		newobj.setObjPriority(this.getObjPriority());
		newobj.setObjMag(this.getObjMag());
		newobj.setRaHour(this.getRaHour());
		newobj.setRaMin(this.getRaMin());
		newobj.setRaSec(this.getRaSec());
		newobj.setDecDeg(this.getDecDeg());
		newobj.setDecMin(this.getDecMin());
		newobj.setDecSec(this.getDecSec());
		newobj.setEpoch(this.getEpoch());
		newobj.setEquinox(this.getEquinox());
		return newobj;
	}
	//////////////////////////////////////////////////////////////////
}
