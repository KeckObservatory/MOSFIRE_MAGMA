package edu.ucla.astro.irlab.util;

import java.util.ArrayList;

public class StringProperty extends Property {
	public StringProperty(String name) {
		super(name);
		value = new String();
		allowedValues = new ArrayList<String>();
	}
	public void setValue(String newValue) {
		if (!newValue.equals((String)value)) {
			logger.debug("Setting property <"+name+"> to new value <"+newValue+">.");
		}
		String oldValue = (String)value;
		value = newValue;
		propertyChangeListeners.firePropertyChange(name, oldValue, value);
	}
	public void setValueIfAllowed(String newValue) throws InvalidValueException {
		//. conversely, could invert logic and do a setValueIgnoreRange or such
		if (!allowedValues.isEmpty()) {
			if (!allowedValues.contains(newValue)) {
				//. throw exception
				throw new InvalidValueException("Invalid Value <"+newValue+">. Not an allowed value.");
			}
		}
		setValue(newValue);
			
	}
	public void setAllowedValues(ArrayList<String> allowedValues) {
		this.allowedValues = allowedValues;
	}
	public String getValue() {
		return (String)value;
	}
}
