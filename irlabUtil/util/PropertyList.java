package edu.ucla.astro.irlab.util;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jdom.*;

//. note: should just extend arraylist?
//. note: need to add number formatting.
public class PropertyList extends Hashtable<String, Property> implements PropertySetter {

	private static final Logger logger = Logger.getLogger(PropertyList.class);
	
//	ArrayList<Property> propertyList;
		
  public static String XML_ROOT = "propertyList";
  public static String XML_ELEMENT_PROPERTY = "property";
  public static String XML_PROPERTY_ATTRIBUTE_NAME = "name";
  public static String XML_PROPERTY_ATTRIBUTE_SHORTNAME = "shortname";
  public static String XML_PROPERTY_ATTRIBUTE_KEYWORD_NAME = "keywordname";
  public static String XML_PROPERTY_ATTRIBUTE_DATATYPE = "datatype";
  public static String XML_PROPERTY_ATTRIBUTE_DESCRIPTION = "desc";
  public static String XML_PROPERTY_ATTRIBUTE_FORMAT = "format";
  public static String XML_PROPERTY_ATTRIBUTE_UNITS = "units";
  public static String XML_PROPERTY_ATTRIBUTE_ALLOWED_VALUES = "enum";
	public transient HashSet<ExternalPropertySetter> externalSetters = new HashSet<ExternalPropertySetter>();

  public PropertyList() {
  	super();
  }
  @Override
  public Property get(Object key) {
  	return getProperty(key.toString());
  }
  public Property getProperty(String name) {
  	return super.get(name.toUpperCase());
  }
  public Property getPropertyFromKeywordName(String keywordName) {
  	Enumeration<String> e = keys();
  	while (e.hasMoreElements()) {
  		Property prop = get(e.nextElement());
  		if (prop.getKeywordName().compareToIgnoreCase(keywordName) == 0) {
  			return prop;
  		}
  	}
  	return null;
  }
  public void addProperty(Property prop) {
  	this.put(prop.getName().toUpperCase(), prop);
  }
  public void registerListener(String name, PropertyChangeListener listener) throws NoSuchPropertyException {
  	Property prop = getProperty(name);
  	if (prop != null) {
  		registerListener(prop, listener);
  	} else {
  		logger.error("Cannot register property <"+name+">. Property not found.");		
  		throw new NoSuchPropertyException("Cannot register property <"+name+">. Property not found.");
  	}
  }
  public static void registerListener(Property prop, PropertyChangeListener listener) {
  	prop.addPropertyChangeListener(listener);
  }
  public void registerExternalSetter(ExternalPropertySetter setter) {
  	externalSetters.add(setter);
  }
  public void removeExternalSetter(ExternalPropertySetter setter) {
  	externalSetters.remove(setter);
  }
  public void setPropertyValue(String propertyName, String value) throws NoSuchPropertyException, InvalidValueException {
  	Property prop = getProperty(propertyName);
		if (prop == null) {
			throw new NoSuchPropertyException("Property <"+propertyName+"> is not registered.  Check property list definition.");
		}
		setPropertyValue(prop, value);
  }
  public void setPropertyValueFromKeywordName(String keywordName, String value) throws InvalidValueException {
  	Enumeration<String> e = keys();
  	while (e.hasMoreElements()) {
  		Property prop = get(e.nextElement());
  		if (prop.getKeywordName().compareToIgnoreCase(keywordName) == 0) {
  			setPropertyValue(prop, value);
  		}
  	}
  }

	public void setPropertyValue(Property prop, String value) throws InvalidValueException {
		if (prop instanceof BooleanProperty) {
			if (value.compareTo("1") == 0) {
				value = "true";
			} else if (value.compareTo("0") == 0) {
				value = "false";
			}
			((BooleanProperty)prop).setValue(value);
		} else if (prop instanceof IntegerProperty) {
			((IntegerProperty)prop).setValue(value);
		} else if (prop instanceof LongProperty) {
			((LongProperty)prop).setValue(value);
		} else if (prop instanceof DoubleProperty) {
			((DoubleProperty)prop).setValue(value);
		} else if (prop instanceof StringProperty) {
			((StringProperty)prop).setValue(value);
		} else {
			logger.error("Unknown Property Type");
		}
	}
	public void setNewPropertyValue(String propertyName, String value) throws NoSuchPropertyException, InvalidValueException {
		logger.debug("Setting property <"+propertyName+"> to new value <"+value+">.");
		Property prop = getProperty(propertyName);
		if (prop == null) {
			throw new NoSuchPropertyException("Property <"+propertyName+"> not found.");
		}
		if (externalSetters.isEmpty()) {
			setPropertyValue(prop, value);
		} else {
			try {
				for (ExternalPropertySetter setter : externalSetters) {
					setter.setExternalProperty(prop, value);
				}	
			} catch (SetExternalPropertyException sepEx) {
				//. TODO: don't cast?
				throw new InvalidValueException(sepEx.getMessage());
			}
		}
	}
  public void readXML(File file) throws IOException, JDOMException {
    org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
    org.jdom.Document myDoc = builder.build(file);

    Attribute workingAtt;
    //. get root element.
    Element root=myDoc.getRootElement();
    //. check that it is a guilayout
    if (root.getName().compareTo(XML_ROOT) != 0)
    	throw new JDOMException("Root element must be "+XML_ROOT);
   	
    //. get children elements
    List<Element> elements=root.getChildren();
    //. loop through them
    for (Element current : elements) {
      //. check
      if (current.getName().compareTo(XML_ELEMENT_PROPERTY) == 0) {
      	Property prop;
      	String name, datatype;
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_NAME);
      	if (workingAtt != null) {
      		name = workingAtt.getValue();
      	} else {
      		throw new JDOMException("Property elements must have a "+XML_PROPERTY_ATTRIBUTE_NAME+" attribute");
      	}
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_DATATYPE);
      	if (workingAtt != null) {
      		datatype = workingAtt.getValue();
      		if (datatype.compareTo(Property.DATATYPE_STRING) == 0) {
      			prop = new StringProperty(name);
      		} else if(datatype.compareTo(Property.DATATYPE_BOOLEAN) == 0) {
      			prop = new BooleanProperty(name);
      		} else if(datatype.compareTo(Property.DATATYPE_INT) == 0) {
      			prop = new IntegerProperty(name);
      		} else if(datatype.compareTo(Property.DATATYPE_LONG) == 0) {
      			prop = new LongProperty(name);
      		} else if(datatype.compareTo(Property.DATATYPE_FLOAT) == 0) {
      			prop = new DoubleProperty(name);
      		} else {
      			throw new JDOMException("Invalid datatype: <"+datatype+"> for property <"+name+">.");
      		}
      		prop.setDatatype(datatype);
      	} else {
      		throw new JDOMException("Property <"+name+"> must have a "+XML_PROPERTY_ATTRIBUTE_DATATYPE+" attribute.");
      	}
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_SHORTNAME);
      	if (workingAtt != null) {
      		prop.setShortName(workingAtt.getValue());
      	}
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_UNITS);
      	if (workingAtt != null) {
      		prop.setUnits(workingAtt.getValue());
      	}
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_KEYWORD_NAME);
      	if (workingAtt != null) {
      		prop.setKeywordName(workingAtt.getValue());
      	}
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_DESCRIPTION);
      	if (workingAtt != null) {
      		prop.setDescription(workingAtt.getValue());
      	}
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_FORMAT);
      	if (workingAtt != null) {
      		prop.setFormat(workingAtt.getValue());
      	}
      	
      	workingAtt = current.getAttribute(XML_PROPERTY_ATTRIBUTE_ALLOWED_VALUES);
      	if (workingAtt != null) {
      		if (datatype.compareTo(Property.DATATYPE_STRING) == 0) {
      			((StringProperty)prop).setAllowedValues(parseAllowedValues(workingAtt.getValue()));
      		} else if(datatype.compareTo(Property.DATATYPE_INT) == 0) {
      			ArrayList<String> stringSet = parseAllowedValues(workingAtt.getValue());
      			ArrayList<Integer> intSet = new ArrayList<Integer>();
      			for (Iterator<String> it1 = stringSet.iterator(); it1.hasNext();) {
      				intSet.add(new Integer(it1.next()));
      			}
      			((IntegerProperty)prop).setAllowedValues(intSet);
      		} else if(datatype.compareTo(Property.DATATYPE_LONG) == 0) {
      			ArrayList<String> stringSet = parseAllowedValues(workingAtt.getValue());
      			ArrayList<Long> longSet = new ArrayList<Long>();
      			for (Iterator<String> it1 = stringSet.iterator(); it1.hasNext();) {
      				longSet.add(new Long(it1.next()));
      			}
      			((LongProperty)prop).setAllowedValues(longSet);
      		} else if(datatype.compareTo(Property.DATATYPE_FLOAT) == 0) {
      			ArrayList<String> stringSet = parseAllowedValues(workingAtt.getValue());
      			ArrayList<Double> doubleSet = new ArrayList<Double>();
      			for (Iterator<String> it1 = stringSet.iterator(); it1.hasNext();) {
      				doubleSet.add(new Double(it1.next()));
      			}
      			((DoubleProperty)prop).setAllowedValues(doubleSet);
      		} else {
      			logger.error("Warning: " + XML_PROPERTY_ATTRIBUTE_ALLOWED_VALUES +
      					" tag ignored for property <" + name + "> because datatype <" + datatype + 
      					"> must be String, Integer, Long, or Float");
      		}
      		
      	}
      	
      addProperty(prop);	
      }
    }
  }
  private ArrayList<String> parseAllowedValues(String values) {
  	String[] result = values.split("\\|");
  	ArrayList<String> set = new ArrayList<String>();
  	for (int ii=0; ii<result.length; ii++) {
  		set.add(result[ii]);
  	}
  	return set;
  }
  
  public void fillWithTestValues() {
  	
  	//. observing
  	((StringProperty)getProperty("ObservingMode")).setValue("imaging");
  	
  	//. exposure
  	((StringProperty)getProperty("ExposureFilename")).setValue("/data/1/mosfire/061002/061002_023.fits");
  	((StringProperty)getProperty("ExposureStatus")).setValue("Exposing");
  	((StringProperty)getProperty("ExposureObject")).setValue("Hubble Deep Field");
  	((StringProperty)getProperty("ExposureComment")).setValue("Field 1 of 4");
  	((StringProperty)getProperty("ExposureSamplingMode")).setValue("MCDS");
  	((StringProperty)getProperty("DatasetPatternType")).setValue("Nod");

  	((IntegerProperty)getProperty("ExposureProgress")).setValue(new Integer(60));
  	((IntegerProperty)getProperty("ExposureCoadds")).setValue(new Integer(2));
  	((IntegerProperty)getProperty("ExposureItime")).setValue(new Integer(60));
  	((DoubleProperty)getProperty("ExposureElapsedTime")).setValue(new Double(72.1));
  	((DoubleProperty)getProperty("ExposureTotalTime")).setValue(new Double(120.0));
  	((IntegerProperty)getProperty("ExposureFrameNumber")).setValue(new Integer(1));
  	((IntegerProperty)getProperty("ExposureReads")).setValue(new Integer(16));
  	
  	//. mechanism
  	((StringProperty)getProperty("MechGratingMirrorStatus")).setValue("Moving");
  	((StringProperty)getProperty("MechGratingMirrorPosition")).setValue("Grating");
  	((StringProperty)getProperty("MechGratingMirrorTarget")).setValue("Mirror");
  	((StringProperty)getProperty("MechGratingTiltStatus")).setValue("OK");
  	((StringProperty)getProperty("MechGratingTiltPosition")).setValue("Out");
  	((StringProperty)getProperty("MechGratingTiltTarget")).setValue("Out");
  	((StringProperty)getProperty("MechFilterStatus")).setValue("OK");
  	((StringProperty)getProperty("MechFilterPosition")).setValue("H");
  	((StringProperty)getProperty("MechFilterTarget")).setValue("H");
  	((StringProperty)getProperty("MechPupilRotatorStatus")).setValue("OK");
  	((StringProperty)getProperty("MechPupilRotatorPosition")).setValue("Open");
  	((StringProperty)getProperty("MechPupilRotatorTarget")).setValue("Open");
  	((StringProperty)getProperty("FlexureCompensationStatus")).setValue("OK");
  	((DoubleProperty)getProperty("FlexureCompensationLocation1")).setValue(new Double(10.0));
  	((DoubleProperty)getProperty("FlexureCompensationVoltage1")).setValue(new Double(1.2));
  	((StringProperty)getProperty("MechFocusStatus")).setValue("OK");
  	((StringProperty)getProperty("MechFocusPosition")).setValue("10");
  	((StringProperty)getProperty("MechFocusTarget")).setValue("10");
  	((StringProperty)getProperty("MechHatchCoverStatus")).setValue("OK");
  	((StringProperty)getProperty("MechHatchCoverPosition")).setValue("Open");
  	((StringProperty)getProperty("MechHatchCoverTarget")).setValue("Open");
  	
  	
  	//. power
  	((BooleanProperty)getProperty("CabinetAPowerStatus1")).setValue(new Boolean(true));
  	((StringProperty)getProperty("CabinetAPowerLocation1")).setValue("Communications");
  	((StringProperty)getProperty("CabinetAPowerLocation2")).setValue("Detector CPU");
  	((StringProperty)getProperty("CabinetAPowerLocation3")).setValue("CSU Controller");
  	((StringProperty)getProperty("CabinetAPowerLocation4")).setValue("CSU Drives Crate");
  	((StringProperty)getProperty("CabinetAPowerLocation5")).setValue("Cal Lamps A");
  	((StringProperty)getProperty("CabinetAPowerLocation6")).setValue("Cal Lamps B");
  	((StringProperty)getProperty("CabinetAPowerLocation7")).setValue("Jade2");
  	((StringProperty)getProperty("CabinetAPowerLocation8")).setValue("Heat Exchanger");
  	((StringProperty)getProperty("CabinetBPowerLocation1")).setValue("Pressure");
  	((StringProperty)getProperty("CabinetBPowerLocation2")).setValue("Guider");
  	((StringProperty)getProperty("CabinetBPowerLocation3")).setValue("Snout Heater");
  	((StringProperty)getProperty("CabinetBPowerLocation4")).setValue("Lake Shore 340");
  	((StringProperty)getProperty("CabinetBPowerLocation5")).setValue("Motors");
  	((StringProperty)getProperty("CabinetBPowerLocation6")).setValue("Dewar Heater");
  	((StringProperty)getProperty("CabinetBPowerLocation7")).setValue("FCS Controller");
  	((StringProperty)getProperty("CabinetBPowerLocation8")).setValue("Heat Exchanger");
  	
  	//. pressure
  	((DoubleProperty)getProperty("DewarPressure")).setValue(0.123);
  	((StringProperty)getProperty("DewarGauge1Pressure")).setValue("0.123");
  	((StringProperty)getProperty("DewarGauge2Pressure")).setValue("0.0123");
  	((StringProperty)getProperty("DewarGauge3Pressure")).setValue("0.00123");
  	((StringProperty)getProperty("DewarGauge4Pressure")).setValue("0.000123");
  	((StringProperty)getProperty("DewarPressureGauge1Name")).setValue("TC 1");
  	((StringProperty)getProperty("DewarPressureGauge2Name")).setValue("TC 2");
  	((StringProperty)getProperty("DewarPressureGauge3Name")).setValue("IMG 1");
  	((StringProperty)getProperty("DewarPressureGauge4Name")).setValue("IMG 2");  	
  	((StringProperty)getProperty("PressureLogFile")).setValue("pressure.log");
  	((StringProperty)getProperty("PressureLogDirectory")).setValue("/u/mosdev/kroot/kss/mosfire/gui/temp/");
  	
  	//. temp control
  	((StringProperty)getProperty("RegulatedTempLocation1")).setValue("Detector");
  	((StringProperty)getProperty("RegulatedTempLocation2")).setValue("Baseplate");
  	((DoubleProperty)getProperty("RegulatedTemp1")).setValue(70.3);
  	((DoubleProperty)getProperty("RegulatedTemp2")).setValue(99.2);
  	((DoubleProperty)getProperty("RegulatedTempSetpoint1")).setValue(72.5);
  	((DoubleProperty)getProperty("RegulatedTempSetpoint2")).setValue(100.0);

  	
  	//. temp monitoring
  	((StringProperty)getProperty("TempLocation1")).setValue("Baseplate");
  	((StringProperty)getProperty("TempLocation2")).setValue("Filter Wheel 1");
  	((StringProperty)getProperty("TempLocation3")).setValue("Grating Turret");
  	((StringProperty)getProperty("TempLocation4")).setValue("Secondary Plate");
  	((StringProperty)getProperty("TempLocation5")).setValue("Pupil Rotator");
  	((StringProperty)getProperty("TempLocation6")).setValue("FCS");
  	((StringProperty)getProperty("TempLocation7")).setValue("CSU");
  	((StringProperty)getProperty("TempLocation8")).setValue("Window");
  	
  	((DoubleProperty)getProperty("Temperature1")).setValue(91.1);
  	((DoubleProperty)getProperty("Temperature2")).setValue(92.2);
  	((DoubleProperty)getProperty("Temperature3")).setValue(93.3);
  	((DoubleProperty)getProperty("Temperature4")).setValue(94.4);
  	((DoubleProperty)getProperty("Temperature5")).setValue(95.5);
  	((DoubleProperty)getProperty("Temperature6")).setValue(96.6);
  	((DoubleProperty)getProperty("Temperature7")).setValue(97.7);
  	((DoubleProperty)getProperty("Temperature8")).setValue(98.8);

  	((StringProperty)getProperty("TempLogFile")).setValue("temp.log");
  	((StringProperty)getProperty("TempLogDirectory")).setValue("/u/mosdev/kroot/kss/mosfire/gui/temp/");

  	((StringProperty)getProperty("ExternalTemperature1")).setValue("71.1");
  	((StringProperty)getProperty("ExternalTemperature2")).setValue("72.2");
  	((StringProperty)getProperty("ExternalTemperature3")).setValue("73.3");
  	((StringProperty)getProperty("ExternalTemperature4")).setValue("-40.4");
  	((StringProperty)getProperty("ExternalTemperature5")).setValue("155.5");
  	((StringProperty)getProperty("ExternalTemperatureLocation1")).setValue("Left Electronics Bay");
  	((StringProperty)getProperty("ExternalTemperatureLocation2")).setValue("Right Electronics Bay");
  	((StringProperty)getProperty("ExternalTemperatureLocation3")).setValue("Ambient");
  	((StringProperty)getProperty("ExternalTemperatureLocation4")).setValue("Dewar Inner Window");
  	((StringProperty)getProperty("ExternalTemperatureLocation5")).setValue("TBD");

  	((StringProperty)getProperty("PowerSupply1Current")).setValue("1.1");
  	((StringProperty)getProperty("PowerSupply1Voltage")).setValue("11");
  	((StringProperty)getProperty("PowerSupply1CurrentLocation")).setValue("Window Heater");
  	((StringProperty)getProperty("PowerSupply1VoltageLocation")).setValue("Window Heater");
  	((StringProperty)getProperty("PowerSupply2Current")).setValue("2.2");
  	((StringProperty)getProperty("PowerSupply2Voltage")).setValue("22");
  	((StringProperty)getProperty("PowerSupply2CurrentLocation")).setValue("Dewar Heater");
  	((StringProperty)getProperty("PowerSupply2VoltageLocation")).setValue("Dewar Heater");

  	((StringProperty)getProperty("ElectronicsBayDoorStatus")).setValue("closed");
  	((StringProperty)getProperty("GlycolSupplyFlowState")).setValue("flow");
  	((StringProperty)getProperty("GlycolReturnFlowState")).setValue("no flow");
  	((StringProperty)getProperty("HeaterPowerSupplyState")).setValue("crowbarred");

  
  }
}
