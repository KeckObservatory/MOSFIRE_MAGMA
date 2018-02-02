package edu.ucla.astro.irlab.mosfire.util;

import edu.ucla.astro.irlab.util.gui.LEDMeter;
import java.awt.Color;

public class MosfireFCSRangeMeter extends LEDMeter {
	private static Color[] colors = {Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 
			Color.GREEN, Color.GREEN, Color.GREEN, Color.YELLOW, Color.YELLOW, Color.RED, Color.RED, Color.RED};
	public MosfireFCSRangeMeter() {
		super(colors, 130.0, false);
		setLEDWidth(40);
	}
	public void setValue(String propName, Object newValue) {
		if (propName.equals(MosfireParameters.MOSFIRE_PROPERTY_FCS_RANGE_PERCENTAGE)) {
			double value = ((Double)newValue).doubleValue();
			setValue(value);
		}
	}
	public static void main(String args[]) {
		javax.swing.JFrame f = new javax.swing.JFrame();
		f.add(new MosfireFCSRangeMeter());
		f.setSize(300,200);
		f.setVisible(true);
	}
}
