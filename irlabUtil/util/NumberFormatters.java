package edu.ucla.astro.irlab.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberFormatters {
	private NumberFormatters() {}
	public static DecimalFormat StandardFloatFormatter(int minPrecision, int maxPrecision) {
		int ii;
		StringBuilder format = new StringBuilder("0");
		if (minPrecision > 0) {
			format.append(".");
		}
		for (ii=0; ii<minPrecision; ii++) {
			format.append("0");
		}
		if ((ii == 0) && (maxPrecision > ii)) {
			format.append(".");
		}
		for (;ii<maxPrecision;ii++) {
			format.append("#");
		}
		DecimalFormat formatter = new DecimalFormat(format.toString());
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		return formatter;
	}
	public static DecimalFormat StandardFloatFormatter(int minPrecision) {
		return StandardFloatFormatter(minPrecision, minPrecision);
	}
}
