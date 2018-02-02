package edu.ucla.astro.irlab.mosfire.util;

import edu.ucla.astro.irlab.util.gui.MechButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

public class MosfireGratingMirrorExchangeButton extends MechButton {
  private String value = "";
  
  private double height;
  private double major;
  private double minor;
  private double semimajor;
  private double semiminor;
  private double tiltAngle = Math.PI/3.;  //. angle from vertical (in plane of screen)
  private double rotationAngle = 0;
  private double centerX;
  private double centerY;
  private double aAngle;
  private double bAngle;
  private double vAngle;
  private double cTAx, cTBx, cTVx, cBAx, cBBx, cBVx;  //. x pixels of corners of wedge;
  private double cTAy, cTBy, cTVy, cBAy, cBBy, cBVy;  //. y pixels of corners of wedge;
  private Polygon topFace, leftFace, rightFace, backFace;
  private Color errorColor=Color.RED;
  private Color outlineColor;
  private double ulx, uly;    //. coordinates of upper left corner of usable area
  boolean unknownState;
  boolean errorState = false;
  public MosfireGratingMirrorExchangeButton() throws Exception {
		super();
		outlineColor = Color.BLACK;
		setSpeed(100);   //. how often drawing updates
		setMotorActive(true);
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
  	unknownState = false;
  	errorState = false;
	  if (value.compareToIgnoreCase("mirror") == 0) {
	   	rotationAngle = Math.PI;
	  } else if (value.compareToIgnoreCase("HK") == 0) {
	  	rotationAngle = Math.PI/10;
	  } else if (value.compareToIgnoreCase("YJ") == 0) {
	  	rotationAngle = 0;
	  } else if (value.compareToIgnoreCase("safe grating") == 0) {
	  	rotationAngle = - Math.PI/10;
	  } else if (value.compareToIgnoreCase("safe mirror") == 0) {
	  	rotationAngle = Math.PI*1.1;
	  } else if (value.compareToIgnoreCase("moving") == 0) {
			setMotorMoving(true);
	  } else if (value.compareToIgnoreCase("homing") == 0) {
			setMotorMoving(true);
	  } else {
	  	rotationAngle = Math.PI/3.;
  		if (value.compareToIgnoreCase("unknown") == 0) {
  			//. unknown state
  			unknownState=true;
  		} else {
  			//. error state
  			errorState=true;
  		}
	  } 
		repaint();
	}
	
	public void stepAnimation() {
		rotationAngle += Math.PI/64.;
		if (rotationAngle > 2.*Math.PI)
			rotationAngle -= 2.*Math.PI;
	}
	public void paintComponent(Graphics g) {
	  //. 0,0 is in upper left
		 
		Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //g2d.setColor(Color.BLACK);
	  //g2d.fillRect(0,0, this.getWidth(), this.getHeight());

	  //. margins
	  ulx = this.getWidth()*0.05;
	  uly = this.getHeight()*0.05;

	  major = this.getWidth()*0.9;
    minor = major*Math.cos(tiltAngle);
	  
    semimajor = major/2.;
    semiminor = minor/2.;
    
    centerX = ulx + major/2.;
    centerY = uly + minor/2.;
	    
    height = this.getHeight() - 2*uly - minor;
    	

	  
	  /*
	   *          B |     
  	 *   cTA _____v_ cTB 
  	 *      |\     /|
  	 *      | \ T / |
  	 *      |  \ /  |
  	 *      |   V   |
  	 *      |cTV|   |
  	 *  cBA |   | R | cBB
  	 *       \ L|  /
  	 *        \ | / 
  	 *         \|/  
  	 *          V cBV  
  	 *      
  	 *   
  	 *   Right angle wedge (half of a square block)
  	 *   i.e.  cTA-cTV-cTB is square
  	 *   
  	 *   For grating, shown as above
  	 *   For mirror, flip so vertex is above
  	 *   
  	 *   Corners follow an ellipse.
  	 *   Ellipse equation:
  	 *   
  	 *      x = h + a cos t
  	 *      y = k + b sin t
  	 *      
  	 *      x = point x
  	 *      y = point y
  	 *      h = center x
  	 *      k = center y
  	 *      a = semimajor axis
  	 *      b = semiminor axis
  	 *      t = polar angle
  	 *      
  	 *   In our case, the center of ellipse is half way between cTA and cTB.
  	 */
  	
	  aAngle = rotationAngle - Math.PI;
	  bAngle = rotationAngle;
	  vAngle = rotationAngle - Math.PI/2.;

	  cTAx = centerX + semimajor * Math.cos(aAngle);
	  cTBx = centerX + semimajor * Math.cos(bAngle);
	  cTVx = centerX + semimajor * Math.cos(vAngle);
	  cTAy = centerY - semiminor * Math.sin(aAngle);
	  cTBy = centerY - semiminor * Math.sin(bAngle);
	  cTVy = centerY - semiminor * Math.sin(vAngle);
	  cBAx = cTAx;
	  cBBx = cTBx;
	  cBVx = cTVx;
	  cBAy = cTAy + height;
	  cBBy = cTBy + height;
	  cBVy = cTVy + height;
	  
	  
	  topFace = new Polygon(new int[] {
	  		(int)(Math.floor(cTAx)),
	  		(int)(Math.floor(cTVx)), 
	  		(int)(Math.floor(cTBx))
	  }, new int[] {
	  		(int)(Math.floor(cTAy)),
	  		(int)(Math.floor(cTVy)), 
	  		(int)(Math.floor(cTBy))	  		
	  }, 3);
	  leftFace = new Polygon(new int[] {
	  		(int)(Math.floor(cTAx)),
	  		(int)(Math.floor(cTVx)), 
	  		(int)(Math.floor(cBVx)),
	  		(int)(Math.floor(cBAx))
	  }, new int[] {
	  		(int)(Math.floor(cTAy)),
	  		(int)(Math.floor(cTVy)), 
	  		(int)(Math.floor(cBVy)), 
	  		(int)(Math.floor(cBAy))	  		
	  }, 4);
	  rightFace = new Polygon(new int[] {
	  		(int)(Math.floor(cTBx)),
	  		(int)(Math.floor(cTVx)), 
	  		(int)(Math.floor(cBVx)),
	  		(int)(Math.floor(cBBx))
	  }, new int[] {
	  		(int)(Math.floor(cTBy)),
	  		(int)(Math.floor(cTVy)), 
	  		(int)(Math.floor(cBVy)), 
	  		(int)(Math.floor(cBBy))	  		
	  }, 4);
	  backFace = new Polygon(new int[] {
	  		(int)(Math.floor(cTBx)),
	  		(int)(Math.floor(cTAx)), 
	  		(int)(Math.floor(cBAx)),
	  		(int)(Math.floor(cBBx))
	  }, new int[] {
	  		(int)(Math.floor(cTBy)),
	  		(int)(Math.floor(cTAy)), 
	  		(int)(Math.floor(cBAy)), 
	  		(int)(Math.floor(cBBy))	  		
	  }, 4);
	  
	  
	  g2d.setColor(Color.CYAN);
	  g2d.fill(leftFace);
	  g2d.fill(rightFace);
	  g2d.fill(topFace);
	  //. if back side is visible, paint that last 
    if ((rotationAngle > Math.PI/2.) && (rotationAngle < 3*Math.PI/2.)) {
    	g2d.setColor(Color.GRAY);
    	g2d.fill(backFace);
    }
	  
	  g2d.setColor(outlineColor);
	  g2d.draw(topFace);
	  //. if back side is visible, paint that last 
    if ((rotationAngle > Math.PI/2.) && (rotationAngle < 3*Math.PI/2.)) {
    	g2d.draw(backFace);
    }
    if ((rotationAngle > 7*Math.PI/4.) || (rotationAngle < 3*Math.PI/4.)) {
  	  g2d.draw(leftFace);
    }
    if ((rotationAngle > 5*Math.PI/4.) || (rotationAngle < Math.PI/4.)) {
      g2d.draw(rightFace);
    }
    
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
