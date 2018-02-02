package edu.ucla.astro.irlab.util;

public interface PropertySetter {
	//. throw exception (on propNotFound or invalid Value)
	public void setNewPropertyValue(String propertyName, String propertyValue) throws NoSuchPropertyException, InvalidValueException;
}
