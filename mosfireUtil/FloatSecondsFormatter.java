package edu.ucla.astro.irlab.mosfire.util;

import java.text.DecimalFormat;

import edu.ucla.astro.irlab.util.ValueFormatter;

public class FloatSecondsFormatter implements ValueFormatter {

	DecimalFormat formatter = new DecimalFormat("0.### s");

	@Override
	public String getFormattedString(Object input) {
		if (input instanceof Double) {
			double ttime = ((Double)input).doubleValue();
			if (ttime >= 0.001) {
				return formatter.format(ttime);
			} else {
				return "< 1 ms";
			}
		}
			
		return input.toString();	}

	@Override
	public String getFormattingRule() {
		return "Show time in seconds to three digits precision.  Show \"< 1 ms\" if time is less than 1 ms";
	}

}
