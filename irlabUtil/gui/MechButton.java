package edu.ucla.astro.irlab.util.gui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


/**
 * <p>Title: OSIRIS</p>
 * <p>Description: Package of Java Software for OSIRIS GUIs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class MechButton extends JComponent implements Runnable {

  private boolean motorMoving;
  private boolean motorActive;
  private boolean motorError;
  private Thread myThread;
  private int speed;  /* update rate in milliseconds */
  public MechButton() throws Exception {
    jbInit();
  }

  private void jbInit() throws Exception {
    speed=1000;
    motorMoving=false;
    motorActive=false;
    motorError=false;
//    this.setSize(120, 120);
    this.setMinimumSize(new java.awt.Dimension(100,100));
    this.setPreferredSize(new java.awt.Dimension(100, 100));
    //this.setBackground(Color.BLACK);
    this.repaint();
  }
  public void paintComponent(Graphics g) {
  	Graphics2D g2d = (Graphics2D)g;
  	g2d.setColor(Color.BLACK);
  	java.awt.geom.Rectangle2D.Float bg = new java.awt.geom.Rectangle2D.Float(0,0,this.getWidth(), this.getHeight());
  	g2d.fill(bg);
  }
  public void resetDrawing(int position) {
  }
  public void stepAnimation() {
  }

  public void run () {
    long mySpeed = (long)(getSpeed());
    while (isMotorActive()) {
      while (isMotorMoving()) {
      	try {
      		Thread.currentThread().sleep(mySpeed);
      		stepAnimation();
      		repaint();
      	} catch (InterruptedException e) {
      		e.printStackTrace();
      	}
      }
      //. repaint();
      try {
      	Thread.currentThread().sleep(mySpeed);
      	Thread.currentThread().yield();
      } catch (InterruptedException e) {
      	e.printStackTrace();
      }
    }  //. end while isMotorActive()
  }

  public void start() {
    myThread = new Thread(this);
    myThread.start();
  }
  public boolean isMotorActive() {
    return motorActive;
  }
  public void setMotorActive(boolean motorActive) {
    this.motorActive = motorActive;
  }
  public boolean isMotorMoving() {
    return motorMoving;
  }
  public void setMotorMoving(boolean motorMoving) {
    this.motorMoving = motorMoving;
  }
  public int getSpeed() {
    return speed;
  }
  public void setSpeed(int speed) {
    this.speed = speed;
  }
  public boolean isMotorError() {
    return motorError;
  }
  public void setMotorError(boolean motorError) {
    this.motorError = motorError;
    repaint();
  }

}