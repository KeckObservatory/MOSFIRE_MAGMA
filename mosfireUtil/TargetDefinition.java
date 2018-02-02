package edu.ucla.astro.irlab.mosfire.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.geom.Point2D;

//. from JMIlburn's TargetDefinitionObject class

public class TargetDefinition {
  private transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
  private String targetName;
  private int priority;
  private float brightness;
  private int raHours;
  private int raMinutes;
  private float raSeconds;
  private int decDegrees;
  private int decMinutes;
  private float decSeconds;
  private double ra;
  private double dec;
  private float epoch;
  private float equinox;
  private float raPropermotion;
  private float decPropermotion;
  private Point2D.Double coordsAsPoint = new Point2D.Double();;
  
/*  The target list will have the same format as the DEIMOS target list, used by the slitassign and autoslit software. It will simply be:
  Column 	Field 	Datatype 	Description
  1 	ID 	String 	Name of target
  2 	Priority 	Integer 	Ranking of importance of target. Can be any integer, but usually from 0-1000. Potential acquisition stars are given a -1 priority
  3 	Magnitude 	Float 	        Brightness of target
  4 	RA H    	Integer 	Target Right Ascension hours
  5 	RA M 	        Integer 	Target Right Ascension minutes
  6 	RA S 	        Float 	        Target Right Ascension seconds
  7 	DEC D 	        Integer 	Target Declination degress
  8 	DEC M 	        Integer 	Target Declination minutes
  9 	DEC S    	Float 	        Target Declination seconds
  10 	Epoch   	Float       	Epoch for RA and Dec coordinates
  11 	Equinox         Float 	        ???
  12 	Proper Motion in RA 	Float 	RA Proper motion of the target in arcsec/year
  13 	Proper Motion in Dec 	Float 	Dec Proper motion of the target in arcsec/year

EXAMPLE target definition statement
//  BX370f 500 25.36 17 00 44.616 64 08 14.69 2000.0 2000.0 0.0 0.0
*/
/*=============================================================================================
/     TargetDefinitionObject() constructor
/=============================================================================================*/
  public TargetDefinition() {
  }
/*=============================================================================================
/     setTargetName(String targetName)
/=============================================================================================*/
  public String getTargetName() {
    return targetName;
  }
  public void setTargetName(String targetName) {
    String  oldTargetName = this.targetName;
    this.targetName = targetName;
    propertyChangeListeners.firePropertyChange("targetName", oldTargetName, targetName);
  }
/*=============================================================================================
/     addPropertyChangeListener(PropertyChangeListener l)
/=============================================================================================*/
  public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChangeListeners.removePropertyChangeListener(l);
  }
  public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChangeListeners.addPropertyChangeListener(l);
  }
/*=============================================================================================
/     setPriority(int priority)
/=============================================================================================*/
  public int getPriority() {
    return priority;
  }
  public void setPriority(int priority) {
    int  oldPriority = this.priority;
    this.priority = priority;
    propertyChangeListeners.firePropertyChange("priority", new Integer(oldPriority), new Integer(priority));
  }
/*=============================================================================================
/     setBrightness(float brightness)
/=============================================================================================*/
  public float getBrightness() {
    return brightness;
  }
  public void setBrightness(float brightness) {
    float  oldBrightness = this.brightness;
    this.brightness = brightness;
    propertyChangeListeners.firePropertyChange("brightness", new Float(oldBrightness), new Float(brightness));
  }
/*=============================================================================================
/     setRAhours(int RAhours)
/=============================================================================================*/
  public int getRAHours() {
    return raHours;
  }
  public void setRAHours(int raHours) {
    int  oldRAHours = this.raHours;
    this.raHours = raHours;
    propertyChangeListeners.firePropertyChange("raHours", new Integer(oldRAHours), new Integer(raHours));
  }
/*=============================================================================================
/     setRAminutes(int RAminutes)
/=============================================================================================*/
  public int getRAMinutes() {
    return raMinutes;
  }
  public void setRAMinutes(int raMinutes) {
    int  oldRAMinutes = this.raMinutes;
    this.raMinutes = raMinutes;
    propertyChangeListeners.firePropertyChange("raMinutes", new Integer(oldRAMinutes), new Integer(raMinutes));
  }
/*=============================================================================================
/     setRAseconds(float RAseconds)
/=============================================================================================*/
  public float getRAseconds() {
    return raSeconds;
  }
  public void setRASeconds(float raSeconds) {
    float  oldRASeconds = this.raSeconds;
    this.raSeconds = raSeconds;
    propertyChangeListeners.firePropertyChange("raSeconds", new Float(oldRASeconds), new Float(raSeconds));
  }
  
  public String  getRAString() {
	  // format is 00:00:00.000
	  return String.format("%02d:%02d:%5.3f", raHours, raMinutes, raSeconds);
  }
  
  public String getDecString() {
	 //  Format is +00:00:00.00
	  return String.format("%+02d:%02d:%4.2f",  decDegrees, decMinutes, decSeconds);
  }
  
/*=============================================================================================
/     setDECdegrees(int DECdegrees)
/=============================================================================================*/
  public int getDecDegrees() {
    return decDegrees;
  }
  public void setDecDegrees(int decDegrees) {
    int  oldDecDegrees = this.decDegrees;
    this.decDegrees = decDegrees;
    propertyChangeListeners.firePropertyChange("decDegrees", new Integer(oldDecDegrees), new Integer(decDegrees));
  }
/*=============================================================================================
/     setDECminutes(int DECminutes)
/=============================================================================================*/
  public int getDecMinutes() {
    return decMinutes;
  }
  public void setDecMinutes(int decMinutes) {
    int  oldDecMinutes = this.decMinutes;
    this.decMinutes = decMinutes;
    propertyChangeListeners.firePropertyChange("decMinutes", new Integer(oldDecMinutes), new Integer(decMinutes));
  }
/*=============================================================================================
/     setDECseconds(float DECseconds)
/=============================================================================================*/
  public float getDecSeconds() {
    return decSeconds;
  }
  public void setDecSeconds(float decSeconds) {
    float  oldDecSeconds = this.decSeconds;
    this.decSeconds = decSeconds;
    propertyChangeListeners.firePropertyChange("decSeconds", new Float(oldDecSeconds), new Float(decSeconds));
  }
/*=============================================================================================
/     setRA(double RA)
/=============================================================================================*/
  public double getRA() {
    return ra;
  }
  public void setRA(double ra) {
    double  oldRA = this.ra;
    this.ra = ra;
    coordsAsPoint.x = ra;
    propertyChangeListeners.firePropertyChange("ra", new Double(oldRA), new Double(ra));
  }
/*=============================================================================================
/     setDec(double dec)
/=============================================================================================*/
  public double getDec() {
    return dec;
  }
  public void setDec(double dec) {
    double  oldDec = this.dec;
    this.dec = dec;
    coordsAsPoint.y = dec;
    propertyChangeListeners.firePropertyChange("dec", new Double(oldDec), new Double(dec));
  }
/*=============================================================================================
/      setEpoch(float epoch)
/=============================================================================================*/
  public float getEpoch() {
    return epoch;
  }
  public void setEpoch(float epoch) {
    float  oldEpoch = this.epoch;
    this.epoch = epoch;
    propertyChangeListeners.firePropertyChange("epoch", new Float(oldEpoch), new Float(epoch));
  }
/*=============================================================================================
/     setEquinox(float equinox)
/=============================================================================================*/
  public float getEquinox() {
    return equinox;
  }
  public void setEquinox(float equinox) {
    float  oldEquinox = this.equinox;
    this.equinox = equinox;
    propertyChangeListeners.firePropertyChange("equinox", new Float(oldEquinox), new Float(equinox));
  }
/*=============================================================================================
/     setRApropermotion(float RApropermotion)
/=============================================================================================*/
  public float getRAPropermotion() {
    return raPropermotion;
  }
  public void setRAPropermotion(float raPropermotion) {
    float  oldRAPropermotion = this.raPropermotion;
    this.raPropermotion = raPropermotion;
    propertyChangeListeners.firePropertyChange("raPropermotion", new Float(oldRAPropermotion), new Float(raPropermotion));
  }
/*=============================================================================================
/     setDECpropermotion(float DECpropermotion)
/=============================================================================================*/
  public float getDecPropermotion() {
    return decPropermotion;
  }
  public void setDecPropermotion(float decPropermotion) {
    float  oldDecPropermotion = this.decPropermotion;
    this.decPropermotion = decPropermotion;
    propertyChangeListeners.firePropertyChange("decPropermotion", new Float(oldDecPropermotion), new Float(decPropermotion));
  }
  public Point2D.Double getPoint() {
  	return coordsAsPoint;
  }
}
