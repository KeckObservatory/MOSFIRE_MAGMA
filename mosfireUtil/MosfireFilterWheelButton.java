package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;

import edu.ucla.astro.irlab.util.ColorUtilities;
import edu.ucla.astro.irlab.util.gui.DynamicWheelButton;

public class MosfireFilterWheelButton extends DynamicWheelButton {
	String value;
	
	public MosfireFilterWheelButton() throws Exception {
		super(MosfireParameters.FILTER_LIST.length);
		setRotation(-Math.PI/32.);
		setMovesClockwise(false);
		setSpeed(200);   //. how often drawing updates
		setMotorActive(true);
		
		/*  colors for filters:
		 * 
		 */
		int numFilters = MosfireParameters.FILTER_LIST.length;
		setFilterColor(numFilters-1, Color.BLACK);
		
		for (int ii=1; ii<numFilters-1; ii++) {
			setFilterColor(numFilters-ii-1, ColorUtilities.wvColor(380f+ii*(780f-380f)/(numFilters-2f), 1.0f));
		}
		setFilterColor(0, Color.WHITE);
		
		start();
	}

	public void setValue(String propName, Object newValue) {
		value = newValue.toString();
		//. if value is moving, animate
		if (isMotorMoving()) {
			setMotorMoving(false);
			try {
				//. wait a bit for it to stop
				Thread.currentThread().sleep(getSpeed());
			} catch (InterruptedException ex) {
				//ignore
			}
		}
  	setMotorError(false);
  	setPositionUnknown(false);
		if (value.compareToIgnoreCase("moving") == 0) {
			setMotorMoving(true);
		} else if (value.compareToIgnoreCase("homing") == 0) {
				setMotorMoving(true);
		} else if (value.compareToIgnoreCase("unknown") == 0) {
				setPositionUnknown(true);
		} else if (value.contains("?")) {
			setPositionUnknown(true);
	  } else {
	  	int posNum=-1;
	  	for (int ii=0; ii<MosfireParameters.FILTER_LIST.length; ii++) {
	  		if (value.compareToIgnoreCase(MosfireParameters.FILTER_LIST[ii]) ==0)
	  			posNum=ii;
	  	}
	  	if (posNum < 0) 
	  		setMotorError(true);
	  	else 
	  		resetDrawing(posNum);
	  } 
		repaint();
	}
	

	

	
}
