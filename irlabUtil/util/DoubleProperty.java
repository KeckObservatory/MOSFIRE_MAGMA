package edu.ucla.astro.irlab.util;

import java.util.ArrayList;

public class DoubleProperty extends Property {
	public DoubleProperty(String name) {
		super(name);
		value = new Double(0);
		allowedValues = new ArrayList<Double>();
	}
	public void setValue(double newValue) {
		setValue(new Double(newValue));
	}
	public void setValue(Double newValue) {
		if (!newValue.equals((Double)value)) {
			logger.debug("Setting property <"+name+"> to new value <"+newValue+">.");
		}
		Double oldValue = (Double)value;
		value = newValue;
		propertyChangeListeners.firePropertyChange(name, oldValue, value);
	}
	public void setValue(String newValue) throws InvalidValueException{
		try {
			setValue(new Double(newValue));
		} catch (NumberFormatException nfEx) {
			//. throw exception
			throw new InvalidValueException("Invalid Value <"+newValue+">.  Must be a parsable double value.");
		}
	}
	public void setValueIfAllowed(Double newValue) throws InvalidValueException {
		//. conversely, could invert logic and do a setValueIgnoreRange or such
		if (!allowedValues.isEmpty()) {
			if (!allowedValues.contains(newValue)) {
				//. throw exception
				throw new InvalidValueException("Invalid Value <"+newValue.toString()+">. Not an allowed value.");
			}
		}
		setValue(newValue);
			
	}
	public void setAllowedValues(ArrayList<Double> allowedValues) {
		this.allowedValues = allowedValues;
	}

	public double getPrimitiveDoubleValue() {
		return getValue().doubleValue();
	}
	public Double getValue() {
		return (Double)value;
	}
}
