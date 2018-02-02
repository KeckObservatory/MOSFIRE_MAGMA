package edu.ucla.astro.irlab.util;

public class TemperatureSetpointValueValidator implements ValueValidator {
	public static double DEFAULT_MAX_ALLOWED_VALUE = 400.;
	public static double DEFAULT_MIN_ALLOWED_VALUE = 1.;
	private double maxAllowedValue;
	private double minAllowedValue;
	private boolean maxInclusive = false;
	private boolean minInclusive = false;
	
	public TemperatureSetpointValueValidator() {
	  this(DEFAULT_MIN_ALLOWED_VALUE, DEFAULT_MAX_ALLOWED_VALUE);
	}
	public TemperatureSetpointValueValidator(double minValue, double maxValue) {
		minAllowedValue = minValue;
		maxAllowedValue = maxValue;
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

		//. now test versus min and max values
		if (maxInclusive) {
			if (valueAsDouble > maxAllowedValue) {
				return false;
			}
		} else {
			if (valueAsDouble >= maxAllowedValue) {
				return false;
			}
		}
		
		if (minInclusive) {
			if (valueAsDouble < minAllowedValue) {
				return false;
			}
		} else {
			if (valueAsDouble <= minAllowedValue) {
				return false;
			}
		}
		return true;
	}

	public String getCriteria() {
		//. construct criteria: number between min and max allowed values;
		StringBuffer criteria = new StringBuffer("Value must be a number between ");
		
		criteria.append(minAllowedValue);
		if (minInclusive)
			criteria.append(" (Inclusive)");
		criteria.append(" and ");
		criteria.append(maxAllowedValue);
		if (maxInclusive)
			criteria.append(" (Inclusive)");
  	criteria.append(".");
		
		return criteria.toString();
	}
	public void setMaxAllowedValue(double maxAllowedValue) {
		this.maxAllowedValue = maxAllowedValue;
	}
	public void setMaxInclusive(boolean maxInclusive) {
		this.maxInclusive = maxInclusive;
	}
	public void setMinAllowedValue(double minAllowedValue) {
		this.minAllowedValue = minAllowedValue;
	}
	public void setMinInclusive(boolean minInclusive) {
		this.minInclusive = minInclusive;
	}

}
