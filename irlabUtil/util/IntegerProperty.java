package edu.ucla.astro.irlab.util;

import java.util.ArrayList;

public class IntegerProperty extends Property {
	public IntegerProperty(String name) {
		super(name);
		value = new Integer(0);
		allowedValues = new ArrayList<Integer>();
	}
	public void setValue(int newValue) {
		setValue(new Integer(newValue));
	}
	public void setValue(Integer newValue) {
		if (!newValue.equals((Integer)value)) {
			logger.debug("Setting property <"+name+"> to new value <"+newValue+">.");
		}
		Integer oldValue = (Integer)value;
		value = newValue;
		propertyChangeListeners.firePropertyChange(name, oldValue, value);

	}
	public void setValue(String newValue) throws InvalidValueException {
		try {
			setValue(new Integer(newValue));
		} catch (NumberFormatException nfEx) {
			//. throw exception
			throw new InvalidValueException("Invalid Value <"+newValue+">.  Must be a parsable integer value.");
		}
	}
	public void setValueIfAllowed(Integer newValue) throws InvalidValueException {
		//. conversely, could invert logic and do a setValueIgnoreRange or such
		if (!allowedValues.isEmpty()) {
			if (!allowedValues.contains(newValue)) {
				//. throw exception
				throw new InvalidValueException("Invalid Value <"+newValue.toString()+">. Not an allowed value.");
			}
		}
		setValue(newValue);
			
	}
	public void setAllowedValues(ArrayList<Integer> allowedValues) {
		this.allowedValues = allowedValues;
	}
	public int getIntValue() {
		return getValue().intValue();
	}
	public Integer getValue() {
		return (Integer)value;
	}
}
