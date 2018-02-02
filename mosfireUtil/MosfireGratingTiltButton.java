package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import edu.ucla.astro.irlab.util.gui.MechButton;

public class MosfireGratingTiltButton extends MechButton {
	private String value;
  private double ulx, uly;    //. coordinates of upper left corner of usable area
  private double axisRadius;
  private double boltRadius;
  private double armLength;
  private double armWidth;
  private double armAngle;
  private double ltX, rtX, lbX, rbX, ltY, rtY, lbY, rbY;   //. coordinates of arm corners
  private double axisCenterX, axisCenterY;
  private Color boltColor;
  private Color axisColor;
  private Color armColor;
  private Color errorColor;
  private Color outlineColor;
  private Polygon arm;
  private Ellipse2D.Double axis;
  private Ellipse2D.Double bolt;
  private boolean armDirectionUp;
  private double armStep;
  private double minDim;
  boolean unknownState = false;
  boolean errorState = false;

	public MosfireGratingTiltButton() throws Exception {
		super();
		boltColor = Color.GRAY;
		axisColor = Color.BLUE;
		armColor = Color.ORANGE;
		errorColor = Color.RED;
		outlineColor = Color.BLACK;
		armDirectionUp = true;
		armStep = Math.PI/32.;
		armAngle = 0;
		setSpeed(200);
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
  	errorState = false;
	  if (value.compareToIgnoreCase("in") == 0)
	  	//. for in, set horizontal
	  	armAngle = 0;
	  else if (value.compareToIgnoreCase("out") == 0)
	  	//. for out, set vertical
	  	armAngle = -Math.PI/2.;
	  else if (value.compareToIgnoreCase("moving") == 0) 
			//. if value is moving, animate
			setMotorMoving(true);
	  else if (value.compareToIgnoreCase("homing") == 0) 
			//. if value is moving, animate
			setMotorMoving(true);
	  else {
  		if (value.compareToIgnoreCase("unknown") == 0) {
  			//. unknown state
  			unknownState=true;
  		} else {
  			//. error state
  			errorState=true;
  		}
	  	armAngle = -Math.PI/4.;
	  }
	  repaint();
	}
	
	public void stepAnimation() {
		//. move arm between straight up and horizontal to right
		if (armDirectionUp) {
			if (armAngle <= -Math.PI/2.)
				armDirectionUp = false;
			else
			  armAngle -= armStep;
		} else {
			if (armAngle >= 0) 
				armDirectionUp = true;
			else
  			armAngle += armStep;
		}
	}
	public void paintComponent(Graphics g) {
	  //. 0,0 is in upper left
		//. angles increase clockwise
		
		 
		Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //g2d.setColor(Color.BLACK);
	  //g2d.fillRect(0,0, this.getWidth(), this.getHeight());
	  
	  //. margins
	  ulx = this.getWidth()*0.05;
	  uly = this.getHeight()*0.05;

	  
	  
	  if (this.getWidth() < this.getHeight()) 
	  	minDim = this.getWidth();
	  else 
	  	minDim = this.getHeight();
	  
	  /*
	   *       ___
	   *      /   \---------------
	   *     |  O  |              |
	   *      \___/---------------
	   *      
	   */

	  axisRadius = minDim * 0.1;
	  boltRadius = axisRadius/2.;
	  
	  axisCenterX = ulx + axisRadius;
	  axisCenterY = this.getHeight() - uly - axisRadius;
	  
	  armLength = minDim - 2 * (ulx + axisRadius);
	  armWidth = axisRadius * 1.5;   //. three quarter size of axis diameter
	  
	  
	  //. left edge of arm corners
	  ltX = axisCenterX - armWidth/2. * Math.sin(armAngle);
	  ltY = axisCenterY + armWidth/2. * Math.cos(armAngle);
	  lbX = axisCenterX + armWidth/2. * Math.sin(armAngle);
	  lbY = axisCenterY - armWidth/2. * Math.cos(armAngle);
	  
	  //. right edge of arm corners
	  rtX = axisCenterX + armLength * Math.cos(armAngle) - armWidth/2. * Math.sin(armAngle);
	  rbX = axisCenterX + armLength * Math.cos(armAngle) + armWidth/2. * Math.sin(armAngle);
	  rtY = axisCenterY + armLength * Math.sin(armAngle) + armWidth/2. * Math.cos(armAngle);
	  rbY = axisCenterY + armLength * Math.sin(armAngle) - armWidth/2. * Math.cos(armAngle);
	  
	  //. arm polygon (rotating rectangle)
	  arm = new Polygon(new int[] {
	  		(int)(Math.floor(ltX)),
	  		(int)(Math.floor(rtX)),
	  		(int)(Math.floor(rbX)),
	  		(int)(Math.floor(lbX))
	  }, new int[] {
	  		(int)(Math.floor(ltY)),
	  		(int)(Math.floor(rtY)),
	  		(int)(Math.floor(rbY)),
	  		(int)(Math.floor(lbY))	  		
	  }, 4);
	  
	  //. axis and arm circles
	  axis = new Ellipse2D.Double(axisCenterX-axisRadius, axisCenterY-axisRadius, 2.*axisRadius, 2.*axisRadius);
	  bolt = new Ellipse2D.Double(axisCenterX-boltRadius, axisCenterY-boltRadius, 2.*boltRadius, 2.*boltRadius);
	  
	  //	. draw arm
	  g2d.setColor(armColor);
	  g2d.fill(arm);
	  g2d.setColor(outlineColor);
	  g2d.draw(arm);
	  
	  //. draw axis
	  g2d.setColor(axisColor);
	  g2d.fill(axis);
	  g2d.setColor(outlineColor);
	  g2d.draw(axis);
	  
	  //. draw bolt
	  g2d.setColor(boltColor);
	  g2d.fill(bolt);
	  g2d.setColor(outlineColor);
	  g2d.draw(bolt);

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
	  	g2d.drawString("?", (float)((this.getWidth() - g2d.getFontMetrics().stringWidth("?"))/2.), (float)(this.getHeight()*0.9));
    }
	}
}
