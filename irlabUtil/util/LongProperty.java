package edu.ucla.astro.irlab.util;

import java.util.ArrayList;

public class LongProperty extends Property {
	public LongProperty(String name) {
		super(name);
		value = new Long(0);
		allowedValues = new ArrayList<Long>();
	}
	public void setValue(long newValue) {
		setValue(new Long(newValue));
	}
	public void setValue(Long newValue) {
		if (!newValue.equals((Long)value)) {
			logger.debug("Setting property <"+name+"> to new value <"+newValue+">.");
		}
		Long oldValue = (Long)value;
		value = newValue;
		propertyChangeListeners.firePropertyChange(name, oldValue, value);

	}
	public void setValue(String newValue) throws InvalidValueException {
		try {
			setValue(new Long(newValue));
		} catch (NumberFormatException nfEx) {
			//. throw exception
			throw new InvalidValueException("Invalid Value <"+newValue+">.  Must be a parsable long value.");
		}
	}
	public void setValueIfAllowed(Long newValue) throws InvalidValueException {
		//. conversely, could invert logic and do a setValueIgnoreRange or such
		if (!allowedValues.isEmpty()) {
			if (!allowedValues.contains(newValue)) {
				//. throw exception
				throw new InvalidValueException("Invalid Value <"+newValue.toString()+">. Not an allowed value.");
			}
		}
		setValue(newValue);
			
	}
	public void setAllowedValues(ArrayList<Long> allowedValues) {
		this.allowedValues = allowedValues;
	}
	public long getLongValue() {
		return getValue().longValue();
	}
	public Long getValue() {
		return (Long)value;
	}
}
