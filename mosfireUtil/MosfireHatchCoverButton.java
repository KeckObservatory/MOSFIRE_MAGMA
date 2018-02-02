package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.ucla.astro.irlab.util.gui.MechButton;

public class MosfireHatchCoverButton extends MechButton {
	private String value;
  boolean unknownState = false;
  boolean errorState = false;
  private double minDim;
  private double ulx, uly;    //. coordinates of upper left corner of usable area
  private Color errorColor;
  private double stepSize;
  private Color baseColor;
  private Color holeColor;
  private Color hatchColor;
  private Color outlineColor;
  private boolean moveDirUp = true;
  private double hatchCenterY;
  private double centerX;
  private double holeCenterY;
  private double openCenterY;
  private double hatchHeight;
  private double hatchWidth;
  private double baseHeight;
  private double baseWidth;
  private double holeHeight;
  private double holeWidth;
  private double holeCornerRadius;
  private Rectangle2D.Double base;
  private RoundRectangle2D.Double hole;
  private Rectangle2D.Double hatchCover;
	public MosfireHatchCoverButton() throws Exception {
		super();
		baseColor = Color.ORANGE;
		holeColor = Color.BLACK;
		hatchColor = Color.BLUE;
		errorColor = Color.RED;
		outlineColor = Color.BLACK;
		setSpeed(200);
		setMotorActive(true);
		start();
	}
	public void setValue(String propName, Object newValue) {
		if (propName.equals(MosfireParameters.MOSFIRE_PROPERTY_MECH_DUST_COVER_POSITION)) {			
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
		}
	  repaint();
	}
	public void updateHatchCenterY() {
		//. for now, state is ok
		unknownState=false;
		errorState=false;
	  if (value.compareToIgnoreCase("open") == 0)
	  	//. for open, set where open pos is
	  	hatchCenterY = openCenterY;
	  else if (value.compareToIgnoreCase("closed") == 0)
	  	//. for closed, center over hole
	  	hatchCenterY = holeCenterY;
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
	  	hatchCenterY = (openCenterY + holeCenterY)/2.;
	  }		
	}
	public void stepAnimation() {
		//. move hatch up and down between hole center and open position center
		if (moveDirUp) {
			if (hatchCenterY <= holeCenterY)
				moveDirUp = false;
			else
			  hatchCenterY -= stepSize;
		} else {
			if (hatchCenterY >= openCenterY) 
				moveDirUp = true;
			else
				hatchCenterY += stepSize;
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
	   *    ______________
	   *   |              |
	   *   |    ______    |
	   *   |   |      |   |
	   *   |   |      |   |
	   *   |   |______|   | 
	   *   |              |  
	   *   |              |
	   *   |  __________  |
	   *   | |          | | 
	   *   | |          | |
	   *   | |          | |
	   *   | |          | |
	   *   | |__________| |
	   *   |______________|
	   *   
	   *   make hole a rounded rectangle
	   *   with height ~ 2/3 of width
	   *   
	   *   make cover square 
	   *   
	   * 
	   */
	  
	  
	  baseHeight = this.getHeight();
	  centerX = this.getWidth()/2.;
	  
	  baseWidth = this.getWidth() - 2*ulx;
	  
	  hatchHeight = (this.getHeight()-2*uly)/2.;
	  hatchWidth = hatchHeight;
	  
	  holeWidth = hatchHeight*0.8;
	  holeHeight = holeWidth*2/3.;
	  
	  openCenterY = (this.getHeight() - uly) - hatchHeight/2.;
	  holeCenterY = uly + hatchHeight/2.;
	 
	  holeCornerRadius = holeHeight/4.;
	  if (moveDirUp) {
	  	stepSize = (openCenterY - holeCenterY)/140.;
	  } else {
	  	stepSize = (openCenterY - holeCenterY)/125.;
	  	
	  }
	  updateHatchCenterY();
	  
	  base = new Rectangle2D.Double(centerX-baseWidth/2., 0, baseWidth, baseHeight);
	  hole = new RoundRectangle2D.Double(centerX-holeWidth/2., holeCenterY-holeHeight/2., holeWidth, holeHeight, holeCornerRadius, holeCornerRadius);
	  hatchCover = new Rectangle2D.Double(centerX-hatchWidth/2., hatchCenterY-hatchHeight/2., hatchWidth, hatchHeight);
	  //. draw base
	  g2d.setColor(baseColor);
	  g2d.fill(base);
	  
	  //. draw hole
	  g2d.setColor(holeColor);
	  g2d.fill(hole);
	  g2d.setColor(outlineColor);
	  g2d.draw(hole);
	 
	  //. draw hatch
	  g2d.setColor(hatchColor);
	  g2d.fill(hatchCover);
	  g2d.setColor(outlineColor);
	  g2d.draw(hatchCover);
	  
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
