package edu.ucla.astro.irlab.util;

public class PositiveDoubleValueValidator implements ValueValidator {

	public PositiveDoubleValueValidator() {
	}

	public boolean isValueValid(Object value) {
		double valueAsDouble;
		
		//. parse number to double
		if (value instanceof Number)
			valueAsDouble = ((Number)value).doubleValue();
		else {
			try {
				valueAsDouble = Double.parseDouble(value.toString());
			} catch (NumberFormatException nfE) {
				return false;
			}
		}

		return (valueAsDouble > 0.0);	
	}

	public String getCriteria() {
		return "Value must be a postive number.";
	}

}
