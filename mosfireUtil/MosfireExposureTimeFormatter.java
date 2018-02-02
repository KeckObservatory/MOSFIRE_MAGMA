package edu.ucla.astro.irlab.mosfire.util;

import java.text.DecimalFormat;

import edu.ucla.astro.irlab.util.ValueFormatter;

public class MosfireExposureTimeFormatter implements ValueFormatter {

	DecimalFormat formatter = new DecimalFormat();
	final String SECONDS_PATTERN = "0.### s";
	final String MILLISECONDS_PATTERN = "# ms";
	
	@Override
	public String getFormattedString(Object input) {
		if (input instanceof Integer) {
			int itime = ((Integer)input).intValue();
			if (itime >= 1000) {
				formatter.applyPattern(SECONDS_PATTERN);
				return formatter.format(itime/1000.0);
			} else {
				formatter.applyPattern(MILLISECONDS_PATTERN);
				return formatter.format(itime);
			}
		}
			
		return input.toString();
	}

	@Override
	public String getFormattingRule() {
		return "If integration time is greater then 1000 ms, the value is converted to seconds. Value is returned with units.";
	}

}
