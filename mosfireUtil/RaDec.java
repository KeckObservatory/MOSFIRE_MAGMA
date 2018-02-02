package edu.ucla.astro.irlab.mosfire.util;



public class RaDec implements Cloneable {
	
	private int raHour;
	private int raMin;
	private double raSec;
	
	//. dec is double, because java allows -0 for doubles and not ints
	private double decDeg;
	private double decMin;
	private double decSec;
	
	private double epoch;
	private double equinox;
	
	private double xCoordinate;
	private double yCoordinate;
	java.text.DecimalFormat secondPlace = new java.text.DecimalFormat("00.00");
	java.text.DecimalFormat wholePlace = new java.text.DecimalFormat("00");
	java.text.DecimalFormat wholeDecPlace = new java.text.DecimalFormat("+00;-00");

	public RaDec(int raHour, int raMin, double raSec, double decDeg, 
			double decMin, double decSec) {
		this.raHour = raHour;
		this.raMin = raMin;
		this.raSec = raSec;
		this.decDeg = decDeg;
		this.decMin = decMin;
		this.decSec = decSec;
		//. TODO initialize all fields
	}
	public RaDec(double x, double y) {
		this();
		this.xCoordinate = x;
		this.yCoordinate = y;
	}
	
	public RaDec() {
		this(0,0,0.0,0.0,0.0,0);
	}
	
	public void setRaHour(int num) {
		raHour = num;
	}
	
	public int getRaHour() {
		return raHour;
	}
	
	public void setRaMin(int num) {
		raMin = num;
	}
	
	public int getRaMin() {
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
	
	public void printRaDecCoord() {
		System.out.println("RA: " + raHour + ", " + raMin + ", " + raSec);
		System.out.println("DEC: " + decDeg + ", " + decMin + ", " + decSec);
	}
	
	public void printRaDecCoordTabs() {
		java.text.DecimalFormat secondPlace = new java.text.DecimalFormat("0.00");
		System.out.println("\t\tRA: " + raHour + "h, " + raMin + "m, " + secondPlace.format(raSec) + "s");
		System.out.println("\t\tDEC: " + decDeg + "?, " + decMin + "', " + secondPlace.format(decSec) + "\"");
	}
	
	public void setXCoordinate(double num) {
		xCoordinate = num;
	}
	
	public double getXCoordinate() {
		return xCoordinate;
	}
	
	public void setYCoordinate(double num) {
		yCoordinate = num;
	}
	
	public double getYCoordinate() {
		return yCoordinate;
	}
	public String toString() {
		return raHour+" "+raMin+" "+secondPlace.format(raSec)+" "+wholeDecPlace.format(decDeg)+" "+wholePlace.format(decMin)+" "+secondPlace.format(decSec);
	}
	public String toStringWithColons() {
		return wholePlace.format(raHour)+":"+wholePlace.format(raMin)+":"+secondPlace.format(raSec)+" "+wholeDecPlace.format(decDeg)+":"+wholePlace.format(decMin)+":"+secondPlace.format(decSec);
	}
	public String toStringWithUnits() {
		return raHour+"h "+raMin+"m "+secondPlace.format(raSec)+"s   "+wholePlace.format(decDeg)+"d "+wholePlace.format(decMin)+"m "+secondPlace.format(decSec)+"s";
	}
  public RaDec clone() {
  	try {
			return (RaDec) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
  }

}
