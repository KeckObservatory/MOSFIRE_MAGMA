package edu.ucla.astro.irlab.util;

public class DoublePercentValueValidator implements ValueValidator {

	public DoublePercentValueValidator() {
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

		if ((valueAsDouble < 0.0 ) || (valueAsDouble > 100.0)) {
			return false;
		}
		
		return true;
	}

	public String getCriteria() {
		return "Value must be a number between 0.0 and 100.0 inclusive.";
	}

}
