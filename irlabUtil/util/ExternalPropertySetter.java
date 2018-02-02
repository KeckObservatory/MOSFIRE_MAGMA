package edu.ucla.astro.irlab.util;

public interface ExternalPropertySetter {
	public void setExternalProperty(Property prop, String value) throws SetExternalPropertyException;
}
