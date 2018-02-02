package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.ucla.astro.irlab.util.gui.MechButton;

public class MosfireDetectorFocusButton extends MechButton {
	private String value;
  boolean unknownState = false;
  boolean errorState = false;
  private double minDim;
  private double ulx, uly;    //. coordinates of upper left corner of usable area
  private Color errorColor;
  private double stepSize;
  private int position;
  private int maxPosition;
  private int minPosition;
  private boolean moveDirUp;
  private Color stageTopColor;
  private Color stageFrontColor;
  private Color stageSideColor;
  private Color pedastalColor;
  private Color outlineColor;
  private Polygon stageTop;
  private Polygon stageSide;
  private Rectangle2D.Double stageFront;
  private Rectangle2D.Double pedastal;
  private Ellipse2D.Double pedastalBase;
  private double pedastalMinHeight;
  private double pedastalWidth;
  private double stageWidth;
  private double stageHeight;
  private double centerX;
  private double stageCenterX;
  private double pedastalBaseY;
  private double tiltAngle;
  private double backEdgeOffsetX;
  private double backEdgeOffsetY;
  private double pedastalBaseHeight;
  private double stageTopY;
  private double stageMinY;
  private double stageMaxY;
  private int homePosition;
  private int focusPosition;
	public MosfireDetectorFocusButton() throws Exception {
		super();
		maxPosition = 2200;
		minPosition = -2200;
		homePosition = -2000;
		focusPosition = 1000;
		stepSize = (maxPosition - minPosition) / 40.0;
		tiltAngle = Math.PI/6.;
		pedastalColor = Color.BLUE;
		stageTopColor = Color.LIGHT_GRAY;
		stageFrontColor = Color.GRAY;
		stageSideColor = Color.DARK_GRAY;
		outlineColor = Color.BLACK;
		errorColor = Color.RED;
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
		errorState=false;
	  if ((value.compareToIgnoreCase("moving") == 0) || (value.compareToIgnoreCase("homing") == 0) ) {
			//. if value is moving, animate
			setMotorMoving(true);
	  } else if ((value.compareToIgnoreCase("home") == 0)) {
	  	position = homePosition;
	  } else if ((value.compareToIgnoreCase("focused") == 0)) {
		  position = focusPosition;
	  } else {
	  	try {
	  		position = Integer.parseInt(value);
	  	} catch (NumberFormatException nfEx) {
	  		if (value.compareToIgnoreCase("unknown") == 0) {
	  			//. unknown state
	  			unknownState=true;
	  		} else {
	  			//. error state
	  			errorState=true;
	  		}
	    }
	  }
	  repaint();
	}
	
	public void stepAnimation() {
		//. move hatch up and down between hole center and open position center
		if (moveDirUp) {
			if (position >= maxPosition)
				moveDirUp = false;
			else
				position += stepSize;
		} else {
			if (position <= minPosition) 
				moveDirUp = true;
			else
				position -= stepSize;
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
	   * 
	   *   |            |   = L + L cos(t)
	   *        ________
	   *       /       /|
	   *     L/       / /
	   *     /       / /\
	   *    /___L___/ /  t
	   *   |________|/ ___\____
	   *      |   |  
	   *      |   | |   |  = backEdgeOffsetX = L cos(t)
	   *      |___| 
	   * 
	   * 
	   *   stage position is the position of the top edge of front
	   *   
	   *   when at max position, top edge of top should be uly from top
	   *   when at min position, top edge of front should be at pedastalMinHeight
	   *   
	   *   bottom on pedastal should be at uly from bottom
	   */
	  
	  centerX = this.getWidth()/2.;
	  stageWidth = (this.getWidth() - 2*ulx)/(1+Math.cos(tiltAngle));
	  stageHeight = stageWidth / 8.;
	  
	  backEdgeOffsetX = stageWidth * Math.cos(tiltAngle);
	  backEdgeOffsetY = stageWidth * Math.sin(tiltAngle);

	  stageCenterX = this.getWidth() - ulx - backEdgeOffsetX - stageWidth/2.;
	  pedastalMinHeight = this.getHeight()/8.;
	  pedastalWidth = 3*stageWidth/4.;
	  pedastalBaseHeight = pedastalWidth * Math.tan(tiltAngle);
	  
	  pedastalBaseY = this.getHeight() - uly - pedastalBaseHeight/2.;
	  
	  pedastalBase = new Ellipse2D.Double(centerX-pedastalWidth/2., pedastalBaseY-pedastalBaseHeight/2., pedastalWidth, pedastalBaseHeight);  
	  
	  stageMaxY = pedastalBaseY - pedastalMinHeight;
	  stageMinY = uly + backEdgeOffsetY;
	  
	  stageTopY = stageMaxY - (stageMaxY - stageMinY) * (position - minPosition)/(double)(maxPosition - minPosition);
	  
	  pedastal = new Rectangle2D.Double(centerX-pedastalWidth/2., stageTopY-backEdgeOffsetY/2., pedastalWidth, pedastalBaseY-stageTopY+backEdgeOffsetY/2.);
	  
	  stageFront = new Rectangle2D.Double(stageCenterX-stageWidth/2., stageTopY, stageWidth, stageHeight);
	  
	  stageTop = new Polygon(new int[] {
	  		(int)Math.floor(stageCenterX-stageWidth/2.),
	  		(int)Math.floor(stageCenterX+stageWidth/2.),
	  		(int)Math.floor(stageCenterX+stageWidth/2.+backEdgeOffsetX),
	  		(int)Math.floor(stageCenterX-stageWidth/2.+backEdgeOffsetX)}, 
	  		new int[] {
	  		(int)Math.floor(stageTopY),
	  		(int)Math.floor(stageTopY),
	  		(int)Math.floor(stageTopY-backEdgeOffsetY),
	  		(int)Math.floor(stageTopY-backEdgeOffsetY)
	  }, 4);

	  stageSide = new Polygon(new int[] {
	  		(int)Math.floor(stageCenterX+stageWidth/2.),
	  		(int)Math.floor(stageCenterX+stageWidth/2.),
	  		(int)Math.floor(stageCenterX+stageWidth/2.+backEdgeOffsetX),
	  		(int)Math.floor(stageCenterX+stageWidth/2.+backEdgeOffsetX)}, 
	  		new int[] {
	  		(int)Math.floor(stageTopY),
	  		(int)Math.floor(stageTopY + stageHeight),
	  		(int)Math.floor(stageTopY + stageHeight - backEdgeOffsetY),
	  		(int)Math.floor(stageTopY-backEdgeOffsetY)
	  }, 4);

	  g2d.setColor(pedastalColor);
	  g2d.fill(pedastalBase);
	  g2d.setColor(outlineColor);
	  g2d.draw(pedastalBase);
	  
	  g2d.setColor(pedastalColor);
	  g2d.fill(pedastal);
	  g2d.setColor(outlineColor);
	  g2d.drawLine((int)Math.floor(centerX-pedastalWidth/2.), (int)Math.floor(pedastalBaseY), (int)Math.floor(centerX-pedastalWidth/2.), (int)Math.floor(stageTopY-backEdgeOffsetY/2.));
	  g2d.drawLine((int)Math.floor(centerX+pedastalWidth/2.), (int)Math.floor(pedastalBaseY), (int)Math.floor(centerX+pedastalWidth/2.), (int)Math.floor(stageTopY-backEdgeOffsetY/2.));
	  
	 
	  g2d.setColor(stageFrontColor);
	  g2d.fill(stageFront);
	  g2d.setColor(outlineColor);
	  g2d.draw(stageFront);

	  g2d.setColor(stageTopColor);
	  g2d.fill(stageTop);
	  g2d.setColor(outlineColor);
	  g2d.draw(stageTop);
	  
	  g2d.setColor(stageSideColor);
	  g2d.fill(stageSide);
	  g2d.setColor(outlineColor);
	  g2d.draw(stageSide);

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
