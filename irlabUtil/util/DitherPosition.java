package edu.ucla.astro.irlab.util;

/**
 * <p>Title:  UCLA IRLAB Utilities </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: UCLA IR Lab</p>
 * @author Jason L. Weiss
 * @version 1.0
 */
public class DitherPosition {

  private double xOffset;
  private double yOffset;
  private String identifier;


  public DitherPosition(String newIdentifier, double newXOffset, double newYOffset) {
    identifier=newIdentifier;
    xOffset=newXOffset;
    yOffset=newYOffset;
  }
  public DitherPosition(boolean isSky, double newXOffset, double newYOffset) {
 		this("", newXOffset, newYOffset); 		
  	if (isSky) {
  		setIdentifier("Sky");
  	}
  }
  public DitherPosition(String identifier) {
    this(identifier, 0.0, 0.0);
  }
  public DitherPosition() {
    this("");
  }
  public DitherPosition(boolean isSky) {
  	this(isSky, 0.0, 0.0);
  }
  
  public boolean isSkyFrame() {
  	return (identifier.compareTo("Sky") == 0);
  }

  public void setSkyFrame(boolean skyFrame) {
  	if (skyFrame) {
  		setIdentifier("Sky");
  	} else {
  		setIdentifier("");
  	}
  }

  public double getXOffset() {
    return xOffset;
  }
  public double getYOffset() {
    return yOffset;
  }
  public void setXOffset(double xOffset) {
    this.xOffset = xOffset;
  }
  public void setYOffset(double yOffset) {
    this.yOffset = yOffset;
  }
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
  public String getIdentifier() {
    return identifier;
  }
  public String toString() {
  	String retString = Double.toString(xOffset)+","+Double.toString(yOffset);
  	if (!identifier.isEmpty()) {
  		retString += (" (" + identifier + "}");
  	}
    return retString;
  }
}