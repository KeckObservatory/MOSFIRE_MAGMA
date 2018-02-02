package edu.ucla.astro.irlab.mosfire.mscgui;

import java.awt.geom.Point2D;

import edu.ucla.astro.irlab.mosfire.util.RaDec;

public class MaskTarget implements Cloneable { 
	private String name;                 //. Object name
	private double priority;             //. Object priority
	private double magnitude;            //. object brightness
	private RaDec raDec;                 //. location of target in sky coordinates
	private double epoch;                //. target epoch
	private double equinox;              //. target equinox
	private Point2D.Double csuLocation;  //. location of target in CSU coordinates (arcsec)
	private Point2D.Double wcsLocation;  //. location of target in WCS coordinates (arcsec)
	private int objRR = -1;              //. RowRegion occupied by Object.
	private int objOR = -1;              //. OverlapRegion occupied by Object.

	public MaskTarget(String name, double priority, double magnitude, RaDec raDec, double epoch, double equinox) {
		this.name = name;
		this.priority = priority;
		this.magnitude = magnitude;
		this.raDec = raDec;
		this.epoch = epoch;
		this.equinox = equinox;
	}
		
	public MaskTarget(String name, RaDec raDec) {
		this(name, 0.0, 0.0, raDec, 2000.0, 2000.0);
	}
	
	public MaskTarget(String name) {
		this(name, new RaDec());
	}
	
  public MaskTarget() {
  	this("blank");
  }

  public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}

	public RaDec getRaDec() {
		return raDec;
	}

	public void setRaDec(RaDec raDec) {
		this.raDec = raDec;
	}

	public double getEpoch() {
		return epoch;
	}

	public void setEpoch(double epoch) {
		this.epoch = epoch;
	}

	public double getEquinox() {
		return equinox;
	}

	public void setEquinox(double equinox) {
		this.equinox = equinox;
	}

	public Point2D.Double getCsuLocation() {
		return csuLocation;
	}

	public void setCsuLocation(Point2D.Double csuLocation) {
		this.csuLocation = csuLocation;
	}

	public Point2D.Double getWcsLocation() {
		return wcsLocation;
	}

	public void setWcsLocation(Point2D.Double wcsLocation) {
		this.wcsLocation = wcsLocation;
	}

	public int getObjRR() {
		return objRR;
	}

	public void setObjRR(int objRR) {
		this.objRR = objRR;
	}

	public int getObjOR() {
		return objOR;
	}

	public void setObjOR(int objOR) {
		this.objOR = objOR;
	}

	/** Return true if the Object's name is not "blank". **/
  public boolean isNotBlank() {
  	if (name == "blank")
  		return false;
  	else
  		return true;
  }
  
  /** Return true if the Object's name is "blank". **/
  public boolean isBlank() {
  	if (name == "blank")
  		return true;
  	else
  		return false;
  }
  public MaskTarget clone() {
  	try {
			return (MaskTarget) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
  }

  
}
