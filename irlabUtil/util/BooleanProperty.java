package edu.ucla.astro.irlab.util;

public class BooleanProperty extends Property {
	public BooleanProperty(String name) {
		super(name);
		value = new Boolean(false);
	}
	public void setValue(boolean newValue) {
		setValue(new Boolean(newValue));
	}
	public void setValue(Boolean newValue) {
		if (!newValue.equals((Boolean)value)) {
			logger.debug("Setting property <"+name+"> to new value <"+newValue+">.");
		}
		Boolean oldValue = (Boolean)value;
		value = newValue;
		propertyChangeListeners.firePropertyChange(name, oldValue, value);

	}
	public void setValue(int newValue) {
		if (newValue == 0)
			setValue(false);
		else
			setValue(true);
	}
	public void setValue(String newValue) throws InvalidValueException {
		try {
			//. set to true iff newValue = "true"
			setValue(new Boolean(newValue));
		} catch (NumberFormatException nfEx) {
			//. throw exception
			throw new InvalidValueException("Invalid Value <"+newValue+">.  Must be a boolean value.");
		}
	}
	public int getIntValue() {
		return (((Boolean)value).booleanValue() ? 1 : 0);
	}
	public boolean getPrimitiveValue() {
		return getValue().booleanValue();
	}
	public Boolean getValue() {
		return (Boolean)value;
	}
}
