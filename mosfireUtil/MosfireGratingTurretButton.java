package edu.ucla.astro.irlab.mosfire.util;

import edu.ucla.astro.irlab.util.gui.MechButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import javax.swing.JFrame;

public class MosfireGratingTurretButton extends MechButton {
  private String value = "";
  
  private double height;
  private double major;
  private double minor;
  private double semimajor;
  private double semiminor;
  private double tiltAngle = Math.PI/6.;  //. angle from vertical (in plane of screen)
  private double rotationAngle = 0;
  private double angleOffset = Math.PI/6.;
  private double centerX;
  private double centerY;
  private double aAngle;
  private double cTAx, cTBx, cTCx, cTDx, cBAx, cBBx, cBCx, cBDx;  //. x pixels of corners of wedge;
  private double cTAy, cTBy, cTCy, cTDy, cBAy, cBBy, cBCy, cBDy;  //. y pixels of corners of wedge;
  private Shape topFace, frontFace, mirrorFace, gratingFace, backFace, incomingBeam, outgoingBeam;
  private Line2D.Double[] gratingLines;
  private Color errorColor=Color.RED;
  private Color outlineColor;
  private double ulx, uly;    //. coordinates of upper left corner of usable area
  boolean unknownState;
  boolean errorState = false;
  boolean mirrorState;
  boolean gratingHKState;
  boolean gratingYJState;
  private double plateThickness;
  private double incomingBeamWidth;
  public MosfireGratingTurretButton() throws Exception {
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
  	mirrorState = false;
  	gratingHKState = false;
  	gratingYJState = false;
	  if (value.compareToIgnoreCase("mirror") == 0) {
	   	rotationAngle = 0;
	   	mirrorState = true;
	  } else if (value.compareToIgnoreCase("HK") == 0) {
	  	rotationAngle = Math.PI*1.05;
	  	gratingHKState = true;
	  } else if (value.compareToIgnoreCase("YJ") == 0) {
	  	rotationAngle = Math.PI;
	  	gratingYJState = true;
	  } else if (value.compareToIgnoreCase("safe grating") == 0) {
	  	rotationAngle = Math.PI*1.05 - angleOffset;
	  } else if (value.compareToIgnoreCase("safe mirror") == 0) {
	  	rotationAngle = Math.PI/20 - angleOffset;
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

	  major = this.getWidth()*0.6;
    minor = major*Math.cos(tiltAngle);
	  
    semimajor = major/2.;
    semiminor = minor/2.;
    
    centerX = this.getWidth()/2.;
    centerY = this.getHeight()/2.;
	    
    height = this.getHeight() - 2*uly - minor;
    	
    plateThickness = semimajor/10.0;
    incomingBeamWidth = semimajor/2.0;
    
    int numGratingLines = (int)Math.floor(major/3) - 1;
    gratingLines = new Line2D.Double[numGratingLines];
    
    /*
     *       cTD  T cTC
     *           //|
     *          // |
     *         //  |
     *     cTA||---|- cTB
     *    G-> || M |
     *        ||   |
     *   cBD -||-- / cBC
     *        ||  /
     *        || /
     *        ||/
     *    cBA F cBB
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
  	 */
  	
	  aAngle = rotationAngle + angleOffset;

	  cTAx = centerX - (semimajor * Math.cos(aAngle)) - (plateThickness/2 * Math.sin(aAngle));
	  cTBx = centerX - (semimajor * Math.cos(aAngle)) + (plateThickness/2 * Math.sin(aAngle));
	  cTCx = centerX + (semimajor * Math.cos(aAngle)) + (plateThickness/2 * Math.sin(aAngle));
	  cTDx = centerX + (semimajor * Math.cos(aAngle)) - (plateThickness/2 * Math.sin(aAngle));
	  cTAy = centerY - height/2 + (semimajor * Math.sin(aAngle)) - (plateThickness/2 * Math.cos(aAngle));
	  cTBy = centerY - height/2 + (semimajor * Math.sin(aAngle)) + (plateThickness/2 * Math.cos(aAngle));
	  cTCy = centerY - height/2 - (semimajor * Math.sin(aAngle)) + (plateThickness/2 * Math.cos(aAngle));
	  cTDy = centerY - height/2 - (semimajor * Math.sin(aAngle)) - (plateThickness/2 * Math.cos(aAngle));

	  cBAx = cTAx;
	  cBBx = cTBx;
	  cBCx = cTCx;
	  cBDx = cTDx;
	  cBAy = cTAy + height;
	  cBBy = cTBy + height;
	  cBCy = cTCy + height;
	  cBDy = cTDy + height;
	  
	  double inBeamLeftX = centerX - (incomingBeamWidth/2 * Math.cos(aAngle)) + (plateThickness/2 * Math.sin(aAngle));
	  double inBeamLeftX1 = centerX - (incomingBeamWidth/6 * Math.cos(aAngle)) + (plateThickness/2 * Math.sin(aAngle));
	  double inBeamLeftX2 = centerX + (incomingBeamWidth/6 * Math.cos(aAngle)) + (plateThickness/2 * Math.sin(aAngle));
	  double inBeamRightX = centerX + (incomingBeamWidth/2 * Math.cos(aAngle)) + (plateThickness/2 * Math.sin(aAngle));
	  double outBeamTopY = centerY - (incomingBeamWidth/2 * Math.sin(aAngle)) + (plateThickness/2 * Math.cos(aAngle));
	  double outBeamTopY1 = centerY - (incomingBeamWidth/6 * Math.sin(aAngle)) + (plateThickness/2 * Math.cos(aAngle));
	  double outBeamTopY2 = centerY + (incomingBeamWidth/6 * Math.sin(aAngle)) + (plateThickness/2 * Math.cos(aAngle));
	  double outBeamBottomY = centerY + (incomingBeamWidth/2 * Math.sin(aAngle)) + (plateThickness/2 * Math.cos(aAngle));
	  	  
	  //60 -> 19 -> 0-3 1-6 2-9 -> 1/20, 2/20
	  for (int ii=0; ii<numGratingLines; ii++) {
	  	double factor = (float)(ii+1)/(numGratingLines+1);
	  	double x = centerX + (factor*major - semimajor) * Math.cos(aAngle) - (plateThickness/2 * Math.sin(aAngle));
	  	double topy = centerY - height/2 - (factor*major - semimajor) * Math.sin(aAngle) - (plateThickness/2 * Math.cos(aAngle));

	  	gratingLines[ii] = new Line2D.Double(x, topy, x, topy+height);
	  }

	  topFace = getQuadrilateral(cTAx, cTBx, cTCx, cTDx, cTAy, cTBy, cTCy, cTDy);
	  frontFace = getQuadrilateral(cTAx, cTBx, cBBx, cBAx, cTAy, cTBy, cBBy, cBAy);
	  backFace = getQuadrilateral(cTCx, cTDx, cBDx, cBCx, cTCy, cTDy, cBDy, cBCy);
	  mirrorFace = getQuadrilateral(cTBx, cTCx, cBCx, cBBx, cTBy, cTCy, cBCy, cBBy);
	  gratingFace = getQuadrilateral(cTDx, cTAx, cBAx, cBDx, cTDy, cTAy, cBAy, cBDy);

	  g2d.setColor(Color.DARK_GRAY);
	  g2d.fill(topFace);
	  g2d.fill(frontFace);
	  g2d.fill(backFace);    	

	  g2d.setColor(outlineColor);

	  g2d.draw(topFace);
	  g2d.draw(frontFace);
	  g2d.draw(backFace);    	

  	//. paint mirror/grating last 
    if ((aAngle > Math.PI/2.) && (aAngle < 3*Math.PI/2.)) {
    	g2d.setColor(Color.GRAY);
    	g2d.fill(gratingFace);
  	  g2d.setColor(outlineColor);
    	g2d.draw(gratingFace);
    	for (int ii=0; ii<numGratingLines; ii++) {
    		g2d.draw(gratingLines[ii]);
    	}
    } else {
  	  g2d.setColor(Color.LIGHT_GRAY);
  	  g2d.fill(mirrorFace);    	
  	  g2d.setColor(outlineColor);
    	g2d.draw(mirrorFace);
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
    } else if (unknownState) {
    	//. draw a big question mark
	  	Font f = new Font("Dialog", Font.BOLD, (int)Math.floor(this.getHeight()*0.9));	  	
	  	g2d.setColor(errorColor);
	  	g2d.setFont(f);	  	
	  	g2d.drawString("?", (float)(centerX - g2d.getFontMetrics().stringWidth("?")/2.), (float)(this.getHeight()*0.9));
    } else if (mirrorState || gratingHKState || gratingYJState) {
    	incomingBeam = getQuadrilateral(inBeamLeftX, inBeamLeftX, inBeamRightX, inBeamRightX, (double)this.getHeight(), outBeamBottomY, outBeamTopY, (double)this.getHeight());
    	
    	g2d.setColor(Color.white);
    	g2d.fill(incomingBeam);

    	if (mirrorState) {
    		outgoingBeam = getQuadrilateral(inBeamLeftX, inBeamRightX, (double)this.getWidth(), (double)this.getWidth(), outBeamBottomY, outBeamTopY, outBeamTopY, outBeamBottomY);

    		g2d.fill(outgoingBeam);
    	} else {
    		outgoingBeam = getQuadrilateral(inBeamLeftX2, inBeamRightX, (double)this.getWidth(), (double)this.getWidth(), outBeamTopY1, outBeamTopY, outBeamTopY, outBeamTopY1);

    		if (gratingHKState) {
    			g2d.setColor(Color.red);
    		} else {
    			g2d.setColor(Color.green);
    		}
    		g2d.fill(outgoingBeam);

    		outgoingBeam = getQuadrilateral(inBeamLeftX1, inBeamLeftX2, (double)this.getWidth(), (double)this.getWidth(), outBeamTopY2, outBeamTopY1, outBeamTopY1, outBeamTopY2);

    		if (gratingHKState) {
    			g2d.setColor(Color.orange);
    		} else {
    			g2d.setColor(Color.cyan);
    		}
    		g2d.fill(outgoingBeam);

    		outgoingBeam = getQuadrilateral(inBeamLeftX, inBeamLeftX1, (double)this.getWidth(), (double)this.getWidth(), outBeamBottomY, outBeamTopY2, outBeamTopY2, outBeamBottomY);

     		if (gratingHKState) {
    			g2d.setColor(Color.yellow);
    		} else {
    			g2d.setColor(Color.BLUE);
    		}
    		g2d.fill(outgoingBeam);

    	}
    }
	}
	
	private Shape getQuadrilateral(double x1, double x2, double x3, double x4, double y1, double y2, double y3, double y4) {
		Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.lineTo(x4, y4);
		path.closePath();
		
		return path;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		try {
			MosfireGratingTurretButton b = new MosfireGratingTurretButton();
			f.getContentPane().add(b);
			f.pack();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(200,200);
			f.setLocation(1300,50);
			f.setVisible(true);
//			b.setMotorMoving(true);
			b.setValue("", "YJ");
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
