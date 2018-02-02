package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import edu.ucla.astro.irlab.util.gui.MechButton;

public class MosfireFlexureCompensationButton extends MechButton {
	private String value;
  boolean unknownState = false;
  private double minDim;
  private double ulx, uly;    //. coordinates of upper left corner of usable area
  private Color errorColor;
  private double stepSize;
  private Color outlineColor;
  private Color topColor;
  private Color frontColor;
  private Color leftColor;
  private Color rightColor;
  private boolean active; 
  private double zRotRange;  //. +/- rotation angle 
  private double zAngle;
  private Polygon frontFace;
  private Polygon topFace;
  private Polygon leftFace;
  private Polygon rightFace;
  private double height;
  private double width;
  private double depth;
  private double tiltAngle = Math.PI/3.;
  private double wedgeAngle;
  private double fulX, furX, fllX, flrX;
  private double fulY, furY, fllY, flrY;
  private double bulX, burX, bllX, blrX;
  private double bulY, burY, bllY, blrY;
  private double upperCenterX, upperCenterY;
  private double lowerCenterX, lowerCenterY;
  private double minor, major, semiminor, semimajor;
  private boolean movesClockwise = true;
	public MosfireFlexureCompensationButton() throws Exception {
		super();

		stepSize = Math.PI/128.;
		zRotRange = Math.PI/16;
		topColor = Color.LIGHT_GRAY;
		frontColor = Color.GRAY;
		leftColor = Color.DARK_GRAY;
		rightColor = Color.DARK_GRAY;
		errorColor = Color.RED;
		outlineColor = Color.BLACK;
		setSpeed(100);
		setMotorActive(true);
		start();
	}
	public void setValue(String propName, Object newValue) {
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
		active = false;
	  if (value.compareToIgnoreCase("off") == 0) {
	  	zAngle=0;
	  } else if (value.compareToIgnoreCase("active") == 0) {
	  	active = true;
			setMotorMoving(true);
	  } else {
	  	//. error state
	  	unknownState=true;
	  	zAngle=0;
	  }
	  repaint();
	}
	
	public void stepAnimation() {
		//. if active, move by changing angles randomly
		//. move arm between straight up and horizontal to right
		if (movesClockwise) {
			if (zAngle <= -zRotRange)
				movesClockwise = false;
			else
				zAngle -= stepSize;
		} else {
			if (zAngle >= zRotRange) 
				movesClockwise = true;
			else
				zAngle += stepSize;
		}	}
	public void paintComponent(Graphics g) {
	  //. 0,0 is in upper left
		//. angles increase clockwise
		
		 
		Graphics2D g2d = (Graphics2D)g;
	  
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	  //. margins
	  ulx = this.getWidth()*0.05;
	  uly = this.getHeight()*0.05;
	  
	  if (this.getWidth() < this.getHeight()) 
	  	minDim = this.getWidth();
	  else 
	  	minDim = this.getHeight();
	  
	  width = this.getWidth() - 2*ulx;
	  depth = width/3.;
	  
	  wedgeAngle = Math.atan(depth * Math.cos(tiltAngle) / width);
	  
	  major = width;
	  semimajor = major/2.;
	  minor = major * Math.cos(tiltAngle);
	  semiminor = minor/2.;

	  height = this.getHeight() - 2*uly - minor;

	  upperCenterX = this.getWidth()/2.;
	  upperCenterY = uly + semiminor;

	  /*
	   * 
	   *   ful ___ bul
	   *      |\  \
	   *      | \  \
 	   *      |  \  \
 	   *      |   \  \ 
 	   *      | fur\__\  bur
 	   *  fll \    |  |
 	   *       \   |  | 
 	   *        \  |  |
 	   *         \ |  |
 	   *          \|__| blr
 	   *        flr   
 	   *           
 	   *           
 	   *           
	   */
	  
	  
	  burX = upperCenterX + semimajor * Math.cos(zAngle);
	  bulX = upperCenterX + semimajor * Math.cos(zAngle + Math.PI- wedgeAngle);
	  fulX = upperCenterX + semimajor * Math.cos(zAngle + Math.PI);
	  furX = upperCenterX + semimajor * Math.cos(zAngle - wedgeAngle);

	  burY = upperCenterY - semiminor * Math.sin(zAngle);
	  bulY = upperCenterY - semiminor * Math.sin(zAngle + Math.PI- wedgeAngle);
	  fulY = upperCenterY - semiminor * Math.sin(zAngle + Math.PI);
	  furY = upperCenterY - semiminor * Math.sin(zAngle - wedgeAngle);
	  
	  blrX = burX;
	  bllX = bulX;
	  fllX = fulX;
	  flrX = furX;
	  
	  blrY = burY + height;
	  bllY = bulY + height;
	  flrY = furY + height;
	  fllY = fulY + height;
	  
	  
	  leftFace = new Polygon(new int[] {
	  		(int)(Math.floor(fllX)),
	  		(int)(Math.floor(fulX)), 
	  		(int)(Math.floor(bulX)), 
	  		(int)(Math.floor(bllX))
	  }, new int[] {
	  		(int)(Math.floor(fllY)),
	  		(int)(Math.floor(fulY)), 
	  		(int)(Math.floor(bulY)), 
	  		(int)(Math.floor(bllY))	  		
	  }, 4);

	  g2d.setColor(leftColor);
	  g2d.fill(leftFace);
	  g2d.setColor(outlineColor);
	  g2d.draw(leftFace);
	  
	  rightFace = new Polygon(new int[] {
	  		(int)(Math.floor(flrX)),
	  		(int)(Math.floor(furX)), 
	  		(int)(Math.floor(burX)), 
	  		(int)(Math.floor(blrX))
	  }, new int[] {
	  		(int)(Math.floor(flrY)),
	  		(int)(Math.floor(furY)), 
	  		(int)(Math.floor(burY)), 
	  		(int)(Math.floor(blrY))	  		
	  }, 4);

	  g2d.setColor(rightColor);
	  g2d.fill(rightFace);
	  g2d.setColor(outlineColor);
	  g2d.draw(rightFace);
	
	  frontFace = new Polygon(new int[] {
	  		(int)(Math.floor(flrX)),
	  		(int)(Math.floor(fllX)), 
	  		(int)(Math.floor(fulX)), 
	  		(int)(Math.floor(furX))
	  }, new int[] {
	  		(int)(Math.floor(flrY)),
	  		(int)(Math.floor(fllY)), 
	  		(int)(Math.floor(fulY)), 
	  		(int)(Math.floor(furY))	  		
	  }, 4);

	  g2d.setColor(frontColor);
	  g2d.fill(frontFace);
	  g2d.setColor(outlineColor);
	  g2d.draw(frontFace);

	  topFace = new Polygon(new int[] {
	  		(int)(Math.floor(burX)),
	  		(int)(Math.floor(bulX)), 
	  		(int)(Math.floor(fulX)), 
	  		(int)(Math.floor(furX))
	  }, new int[] {
	  		(int)(Math.floor(burY)),
	  		(int)(Math.floor(bulY)), 
	  		(int)(Math.floor(fulY)), 
	  		(int)(Math.floor(furY))	  		
	  }, 4);

	  g2d.setColor(topColor);
	  g2d.fill(topFace);
	  g2d.setColor(outlineColor);
	  g2d.draw(topFace);
	  
	  //. on error, draw X over drawing
	  if (unknownState) {
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
	}
	
}
