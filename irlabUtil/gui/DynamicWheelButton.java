package edu.ucla.astro.irlab.util.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * <p>Title: OSIRIS</p>
 * <p>Description: Package of Java Software for OSIRIS GUIs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class DynamicWheelButton extends MechButton {

  private int numFilters;
  private double rotation;
  private boolean movesClockwise;
  private ArrayList filterArray;
  private double angleBetweenFilters;  //. in radians
  private int myHeight;
  private int myWidth;
  private int wheelRadius;
  private Color wheelColor;
  private Color errorColor;
  private Color outlineColor;
  private boolean positionUnknown = false;

  public DynamicWheelButton() throws Exception  {
    this(4);
  }

  public DynamicWheelButton(int numberOfFilters) throws Exception {
    numFilters=numberOfFilters;
    jbInit();
    super.setMotorActive(false);
    super.setMotorMoving(false);
    super.setMotorError(false);
  }

  public void jbInit() throws Exception {
    this.setBackground(Color.black);
    rotation=Math.PI/6.;
    movesClockwise=true;
    angleBetweenFilters=2.*Math.PI/(double)(numFilters);
    filterArray=new ArrayList(numFilters);
    int defaultHeight=220;
    int defaultWidth=220;
    int defaultWheelRadius=100;
    int defaultRadius=20;
    int defaultCenterCircleRadius=70;
    Color defaultFilterColor=new Color(0, 0, 150);
    Color defaultWheelColor=new Color(0, 150, 150);
    Color defaultErrorColor = new Color(255, 0, 0);
    outlineColor =Color.BLACK;

    myHeight=defaultHeight;
    myWidth=defaultWidth;
    setWheelRadius(defaultWheelRadius);
    setWheelColor(defaultWheelColor);
    setErrorColor(defaultErrorColor);

    for (int ii=0; ii<numFilters; ii++) {
      //. add new FilterCircle Objects
      filterArray.add(new FilterCircle(defaultRadius, defaultCenterCircleRadius, (double)(ii)*angleBetweenFilters, defaultFilterColor));
    }
  }

  public void setFilterColor(int numFilter, Color newColor) throws ArrayIndexOutOfBoundsException {
    FilterCircle myCircle=(FilterCircle)(filterArray.get(numFilter));
    myCircle.setColor(newColor);
  }

  public void resetDrawing(int position) {
    //. wait for animation to stop
    try {
      Thread.currentThread().sleep(getSpeed()*2);
    } catch (InterruptedException e) {
      //. ignore
    }
    int ii=0;
    for(Iterator i = filterArray.iterator(); i.hasNext(); ) {
      FilterCircle myCircle = (FilterCircle)(i.next());
      myCircle.setAngle((ii-position)*angleBetweenFilters);
      ii++;
    }
    repaint();
  }
  public void stepAnimation() {
    for(Iterator i = filterArray.iterator(); i.hasNext(); ) {
      FilterCircle myCircle = (FilterCircle)(i.next());
      myCircle.setAngle(myCircle.getAngle()+rotation);
    }
  }

  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    myWidth = this.getWidth();
    myHeight = this.getHeight();
    wheelRadius = (Math.min(myWidth, myHeight)/2)-2;
    Ellipse2D.Double wheel = new Ellipse2D.Double(myWidth/2-wheelRadius, myHeight/2-wheelRadius, 2*wheelRadius, 2*wheelRadius);

    //. draw a circle
    
    g2d.setColor(wheelColor);
    g2d.fill(wheel);
    g2d.setColor(outlineColor);
    g2d.draw(wheel);

    //. draw filters
    for (int ii=0; ii<numFilters; ii++) {
      FilterCircle myCircle = (FilterCircle)(filterArray.get(ii));
      //. determine filter radius based on size of wheel and number of filters
      //.
      //.   use constraints:
      //.    a) the distance from edge of filter is one half filter radius from edge
      //.    b) the angular distance between the edges of filters is same as the angular size
      //.       of the filter itself.
      //.    from b), we divide the wheel into 2*N isosceles triangles, and then those into
      //.    4*N right triangles, with one side as the radius of the filter, and the 
      //.    hypotenuse is the radius of the filter center circle, so 
      //.    Rfilter = Rcenter * sin (2*PI/4*N) = Rcenter * sin (PI/2*N)
      //.    from a), Rcenter = Rwheel - 3*Rfilter/2 so, if we let S = sin(PI/2*N), we get:
      //.    Rfilter = (Rwheel - 3*Rfilter/2) * S -> Rfilter + 3*S*Rfilter/2 = S*Rwheel
      //.    Rfilter = S*Rwheel/(1+3*S/2)
      double stemp = Math.sin(3*Math.PI/(4*filterArray.size()));
      double rFilter = stemp * wheelRadius/(1 + 3*stemp/2);
      myCircle.setRadius((int)Math.floor(rFilter));
      myCircle.setCenterCircleRadius(wheelRadius - (int)(Math.floor(rFilter*1.5)));
      Ellipse2D.Double myFilter = new Ellipse2D.Double(myCircle.getX()+myWidth/2, myHeight/2-myCircle.getY(), 2*myCircle.getRadius(), 2*myCircle.getRadius());

      g2d.setColor(myCircle.getColor());
      g2d.fill(myFilter);
      g2d.setColor(outlineColor);
      g2d.draw(myFilter);
    }

    //. on motor error, draw an x through animation
    if (isMotorError()) {
      //. pixel limits
      float xlim = (float)myWidth-1.0f;
      float ylim = (float)myHeight-1.0f;
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
	  if (positionUnknown) {
    	//. draw a big question mark
	  	Font f = new Font("Dialog", Font.BOLD, (int)Math.floor(this.getHeight()*0.9));	  	
	  	g2d.setColor(errorColor);
	  	g2d.setFont(f);	  	
	  	g2d.drawString("?", (float)((this.getWidth() - g2d.getFontMetrics().stringWidth("?"))/2.), (float)(this.getHeight()*0.9));
    }
  }
  public int getMyHeight() {
    return myHeight;
  }
  public int getMyWidth() {
    return myWidth;
  }
  public int getWheelRadius() {
    return wheelRadius;
  }
  public void setWheelRadius(int wheelRadius) {
    this.wheelRadius = wheelRadius;
  }
  public boolean isMovesClockwise() {
    return movesClockwise;
  }
  public void setMovesClockwise(boolean movesClockwise) {
    this.movesClockwise = movesClockwise;
  }
  public Color getWheelColor() {
    return wheelColor;
  }
  public void setWheelColor(Color wheelColor) {
    this.wheelColor = wheelColor;
  }
  public double getRotation() {
    return rotation;
  }
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }
  public Color getErrorColor() {
    return errorColor;
  }
  public void setErrorColor(Color errorColor) {
    this.errorColor = errorColor;
  }
  public void setPositionUnknown(boolean state) {
  	positionUnknown = state;
  }
  public class FilterCircle {

    private int centerCircleRadius;
    private int radius;
    private int x;  //. upper right corner
    private int y;  //. upper left corner
    private Color myColor;
    private double angle;

    public FilterCircle(int newRadius, int newCenterCircleRadius, double newAngle, Color newColor) {
      radius=newRadius;
      centerCircleRadius=newCenterCircleRadius;
      myColor=newColor;
      setAngle(newAngle);
    }

    public Color getColor() {
      return myColor;
    }

    public void setColor(Color newColor) {
      myColor=newColor;
    }

    public int getRadius() {
      return radius;
    }
    public void setRadius(int newRadius) {
      radius=newRadius;
      setXY();
    }

    public void setCenterCircleRadius(int newRadius) {
      centerCircleRadius=newRadius;
      setXY();
    }

    public double getAngle() {
      return angle;
    }
    public void setAngle(double newAngle) {
      angle=newAngle;
      setXY();
    }

    private void setXY() {
      //. angle is angle clockwise from top
      //. trueAngle is angle counterclockwise from right
      double trueAngle = Math.PI/2-angle;
      x=(int)Math.floor(centerCircleRadius*Math.cos(trueAngle))-radius;
      y=(int)Math.floor(centerCircleRadius*Math.sin(trueAngle))+radius;
    }
    public int getX() {
      return x;
    }
    public int getY() {
      return y;
    }


  }

}