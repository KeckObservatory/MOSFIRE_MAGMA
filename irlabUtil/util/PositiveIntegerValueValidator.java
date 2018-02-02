package edu.ucla.astro.irlab.util;

public class PositiveIntegerValueValidator implements ValueValidator {

	@Override
	public String getCriteria() {
		return "Value must be a positive integer";
	}

	@Override
	public boolean isValueValid(Object value) {
		int valueAsInt;
		
		//. parse number to double
		if (value instanceof Number)
			valueAsInt = ((Number)value).intValue();
		else {
			try {
				valueAsInt = Integer.parseInt(value.toString());
			} catch (NumberFormatException nfE) {
				return false;
			}
		}
		
		return (valueAsInt > 0);
	}

}
