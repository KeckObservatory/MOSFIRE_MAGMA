package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.Polygon;

import edu.ucla.astro.irlab.util.gui.MechButton;

public class MosfirePupilRotatorButton extends MechButton {
	private String value;
	boolean unknownState = false;
	boolean errorState = false;
	private double minDim;
	private double ulx, uly;    //. coordinates of upper left corner of usable area
	private Color errorColor;
	private int numPoints;
	private boolean tracking;
	private int maxNumPoints;
	private double rotationAngle;
	private double rotationStepSize;
	private boolean pointsIncreasing = false;
	private double holeRadius;
	private double centerX;
	private double centerY;
	private Color fillColor;
	private Color outlineColor;
	private Shape pupil;
	private int currentLocation;
	private boolean opening;
	private boolean moving;
	
	public MosfirePupilRotatorButton() throws Exception {
		super();
		rotationAngle = 0.;
		rotationStepSize = Math.PI/64.;
		maxNumPoints = 32;
		fillColor = Color.BLUE;
		errorColor = Color.RED;
		outlineColor = Color.BLACK;
		setSpeed(500);
		setMotorActive(true);
		start();
	}
	public void setValue(String propName, Object newValue) {
		//. if location is > range, and moving, make animation opening
		//. else, just show location
		
		if (propName.equals(MosfireParameters.MOSFIRE_PROPERTY_MECH_PUPIL_ROTATOR_POSITION)) {			
			value = newValue.toString();

			//. turn off animation by default
			if (isMotorMoving()) {
				setMotorMoving(false);
				try {
					//. wait a bit for it to stop
					Thread.currentThread().sleep(getSpeed());
				} catch (InterruptedException ex) {
					//ignore
				}
			}
			//. for now, state is ok
			unknownState=false;
			errorState = false;
			tracking = false;
			opening = false;
			moving = false;
			if (value.compareToIgnoreCase("open") == 0) {
				//. for open, draw circle
				numPoints = -1;
			} else if (value.compareToIgnoreCase("home") == 0) {
				//. for closed, center over hole
				numPoints = 6;
				rotationAngle = 0;
			} else if (value.compareToIgnoreCase("moving") == 0) {
				moving = true;
			} else if (value.compareToIgnoreCase("homing") == 0) {
				numPoints = 6;
				//. if value is moving, animate like tracking
				tracking=true;
				setMotorMoving(true);
			} else if (value.compareToIgnoreCase("unknown") == 0) {
					//. unknown state
				numPoints = 6;
				unknownState=true;
			} else if (value.compareToIgnoreCase("error") == 0)	{
					//. error state
				numPoints = 6;
				errorState=true;
			}
		} else {
			currentLocation = ((Integer)newValue).intValue();
			opening = (currentLocation < -1 * MosfireParameters.PUPIL_MECHANISM_TRACKING_RANGE_STEPS);
			if (!opening) {
				numPoints = 6;
				//. turn off animation by default
				if (isMotorMoving()) {
					setMotorMoving(false);
					try {
						//. wait a bit for it to stop
						Thread.currentThread().sleep(getSpeed());
					} catch (InterruptedException ex) {
						//ignore
					}
				}
				
			} else {
				tracking=false;
				if (moving) {
					if (!isMotorMoving()) {
						setMotorMoving(true);
					}
				}
			}
			rotationAngle = Math.toRadians(currentLocation / MosfireParameters.PUPIL_MECHANISM_STEPS_PER_DEGREE);
		}
		repaint();
	}

	public void stepAnimation() {
		//. if tracking, rotate hexagon
		//. otherwise, moving: increase and decrease number of points
		if (tracking) {
			rotationAngle += rotationStepSize;
			if (rotationAngle > 2.*Math.PI)
				rotationAngle -= 2.*Math.PI;
		} else {
			if (pointsIncreasing) {
				if (numPoints >= maxNumPoints)
					numPoints = -1;
				else if (numPoints == -1) {
					pointsIncreasing = false;
					numPoints = maxNumPoints;
				} else 
					numPoints++;
			} else {
				if (numPoints <= 6) 
					pointsIncreasing = true;
				else
					numPoints--;
			}
		}
	}
	public void paintComponent(Graphics g) {
		//. 0,0 is in upper left
		//. angles increase clockwise


		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(fillColor);
		g2d.fillRect(0,0, this.getWidth(), this.getHeight());

		//. margins
		ulx = this.getWidth()*0.05;
		uly = this.getHeight()*0.05;

		if (this.getWidth() < this.getHeight()) 
			minDim = this.getWidth();
		else 
			minDim = this.getHeight();

		holeRadius = minDim/2. - ulx;

		centerX = this.getWidth()/2.;
		centerY = this.getHeight()/2.;

		if (numPoints == -1) {
			pupil = new Ellipse2D.Double(centerX-holeRadius, centerY-holeRadius, holeRadius*2, holeRadius*2);
		} else {
			double doubleNumPoints = new Integer(numPoints).doubleValue();
			int[] xpoints = new int[numPoints];
			int[] ypoints = new int[numPoints];
			for (int ii=0; ii<numPoints; ii++) {
				xpoints[ii] = (int)Math.floor(centerX + holeRadius * Math.cos(ii/doubleNumPoints*2*Math.PI + rotationAngle));
				ypoints[ii] = (int)Math.floor(centerY + holeRadius * Math.sin(ii/doubleNumPoints*2*Math.PI + rotationAngle));
			}
			pupil = new Polygon(xpoints, ypoints, numPoints);
		}

		g2d.setColor(Color.BLACK);
		g2d.fill(pupil);
		g2d.setColor(outlineColor);
		g2d.draw(pupil);

		//. on error, draw X over drawing
		if (errorState) {
			//. pixel limits
			float xlim = (float)this.getWidth();
			float ylim = (float)this.getHeight();
			//. create two paths for each part of the X
			GeneralPath negSlopePath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			GeneralPath posSlopePath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

			//. move path to define X (with total thickness of 9)
			negSlopePath.moveTo(0.0f, 0.0f);
			negSlopePath.lineTo(5.0f, 0.0f);
			negSlopePath.lineTo(xlim, ylim-5.0f);
			negSlopePath.lineTo(xlim, ylim);
			negSlopePath.lineTo(xlim-4.0f, ylim);
			negSlopePath.lineTo(0.0f, 4.0f);
			negSlopePath.closePath();
			posSlopePath.moveTo(0.0f, ylim);
			posSlopePath.lineTo(4.0f, ylim);
			posSlopePath.lineTo(xlim, 4.0f);
			posSlopePath.lineTo(xlim, 0);
			posSlopePath.lineTo(xlim-5.0f, 0);
			posSlopePath.lineTo(0.0f, ylim-5.0f);
			posSlopePath.closePath();

			//. set color and fill paths
			g2d.setColor(errorColor);
			g2d.fill(negSlopePath);
			g2d.fill(posSlopePath);	  
		}
		if (unknownState) {
			//. draw a big question mark
			Font f = new Font("Dialog", Font.BOLD, (int)Math.floor(this.getHeight()*0.9));	  	
			g2d.setColor(errorColor);
			g2d.setFont(f);	  	
			g2d.drawString("?", (float)(centerX - g2d.getFontMetrics().stringWidth("?")/2.), (float)(this.getHeight()*0.9));
		}
	}	
}
