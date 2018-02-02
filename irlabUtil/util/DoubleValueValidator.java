package edu.ucla.astro.irlab.util;

public class DoubleValueValidator implements ValueValidator {

	public DoubleValueValidator() {
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

		return true;	}

	public String getCriteria() {
		return "Value must be a number.";
	}

}
