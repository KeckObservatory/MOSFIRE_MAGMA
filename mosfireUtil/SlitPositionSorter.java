package edu.ucla.astro.irlab.mosfire.util;

import java.util.Comparator;

public class SlitPositionSorter implements Comparator<SlitPosition> {

	@Override
	public int compare(SlitPosition pos1, SlitPosition pos2) {
		if (pos1.getSlitNumber() > pos2.getSlitNumber()) {
			return 1;
		} else if (pos1.getSlitNumber() < pos2.getSlitNumber()) {
			return -1;
		} else {
			return 0;
		}
	}
}
