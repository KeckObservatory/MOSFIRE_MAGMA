package edu.ucla.astro.irlab.util;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Property {
	protected static final Logger logger = Logger.getLogger(Property.class);
	
	String name;
	Object value;
	Object maxValue;
	Object minValue;
	String format;
	String shortName;
	String keywordName;
	String description;
	String datatype;
	String units;
	ArrayList allowedValues;
	public static String DATATYPE_FLOAT = "float";
	public static String DATATYPE_INT = "integer";
	public static String DATATYPE_LONG = "long";
	public static String DATATYPE_STRING = "string";
	public static String DATATYPE_BOOLEAN = "boolean";
	
	public transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
  public Property(String propName) {
  	name=propName;
  	format = "";
  	shortName="";
  	keywordName="";
  	description="";
  	datatype="";
  	units="";
  }
  public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChangeListeners.removePropertyChangeListener(l);
  }
  public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChangeListeners.addPropertyChangeListener(l);
  }
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKeywordName() {
		return keywordName;
	}
	public void setKeywordName(String keywordName) {
		this.keywordName = keywordName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public Object getValue() {
		return value;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public ArrayList getAllowedValues() {
		return allowedValues;
	}
	
}
