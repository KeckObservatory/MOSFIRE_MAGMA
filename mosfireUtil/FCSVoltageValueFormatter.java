package edu.ucla.astro.irlab.mosfire.util;

import java.text.DecimalFormat;

import edu.ucla.astro.irlab.util.ValueFormatter;

public class FCSVoltageValueFormatter implements ValueFormatter {

	@Override
	public String getFormattedString(Object input) {
		if (input instanceof Double) {
			double value = ((Double)input).doubleValue();
			DecimalFormat formatter = new DecimalFormat("0.000 V");
			return formatter.format(value);
		}
			
		return input.toString();
	}

	@Override
	public String getFormattingRule() {
		return "Value returned with 3 digits of precision with Volt units";
	}

}
