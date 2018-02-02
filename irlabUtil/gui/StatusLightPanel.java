package edu.ucla.astro.irlab.util.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
/**
 * <p>Title: OSIRIS</p>
 * <p>Description: Package of Java Software for OSIRIS GUIs</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class StatusLightPanel extends JPanel {
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JButton statusButton = new JButton();
  private JTextPane statusLabel = new JTextPane();
  private Color onColor = new Color(0, 200, 0);
  private Color offColor = new Color(0, 50, 0);
  private String onText = "";
  private String offText = "";
  private Color onTextColor = new Color(255, 255, 255);
  private Color offTextColor = new Color(150, 150, 150);
  private boolean hasLabel;

  private String statusName = "Status";
    private boolean status=false;;
  private transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
  private java.awt.Font statusNameFont;
  private boolean hasBorder;
  private java.awt.Dimension lightSize;
  private boolean controllable=false;

  public StatusLightPanel(boolean addLabel, boolean controllable) throws Exception {
    hasLabel=addLabel;
    this.controllable=controllable;
    jbInit();
  }
  public StatusLightPanel(boolean addLabel) throws Exception {
    this(addLabel, false);
  }
  public StatusLightPanel() throws Exception {
    this(true);
  }

  void jbInit() throws Exception {
    SimpleAttributeSet sas=new SimpleAttributeSet();
    this.setLayout(gridBagLayout1);
    statusButton.setPreferredSize(new Dimension(40, 40));
    statusButton.setMinimumSize(new Dimension(40, 40));
    statusButton.setEnabled(controllable);
    statusButton.setBackground(offColor);
    statusButton.setMargin(new Insets(1,1,1,1));
    statusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    statusLabel.setOpaque(false);
    statusLabel.setEditable(false);

    statusLabel.setText("status");

    //. center text
    StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);

    statusLabel.setParagraphAttributes(sas, false);

    this.add(statusButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    if (hasLabel)
      this.add(statusLabel,  new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
      		,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    propertyChangeListeners.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        updateView(evt.getPropertyName(), evt.getNewValue());
      }
    });
  }

  public void addActionListener(ActionListener l) {
    statusButton.addActionListener(l);
  }
  /**
   * Event handler for property change events.
   * Updates the view according to the property that was changed.
   *
   * @param propertyName  name of the property that was changed
   * @param newValue      new value the property was changed to
   */
  public void updateView(String propertyName, Object newValue) {
    if ("status".equals(propertyName)) {
      if (((Boolean)(newValue)).booleanValue()) {
      	this.turnOn();
      } else {
      	this.turnOff();
      }
    } else if ("statusNameFont".equals(propertyName)) {
      statusLabel.setFont((Font)newValue);
    } else if ("statusName".equals(propertyName)) {
      statusLabel.setText((String)newValue);
    } else if ("lightSize".equals(propertyName)) {
      statusButton.setSize((Dimension)newValue);
      statusButton.setMinimumSize((Dimension)newValue);
    } else if ("hasBorder".equals(propertyName)) {
      if (((Boolean)(newValue)).booleanValue()) {
        this.setBorder(BorderFactory.createEtchedBorder());
      } else {
      	this.setBorder(null);
      }
    }
  }


  public String getStatusName() {
    return statusName;
  }
  public void setStatusName(String statusName) {
    String oldStatusName=this.statusName;
    this.statusName = statusName;
    propertyChangeListeners.firePropertyChange("statusName", oldStatusName, statusName);
  }
  public Color getOffColor() {
    return offColor;
  }
  public void setOffColor(Color offColor) {
    this.offColor = offColor;
    if (!status) turnOff();
  }
  public Color getOnColor() {
    return onColor;
  }
  public void setOnColor(Color onColor) {
    this.onColor = onColor;
    if (status) turnOn();
  }
  public void setIsControllable(boolean controllable) {
  	this.controllable = controllable;
  	statusButton.setEnabled(controllable);
  }
  private void turnOn() {
  	status=true;
    statusButton.setBackground(onColor);
    statusButton.setText(onText);
    statusButton.setForeground(onTextColor);
  }

  private void turnOff() {
  	status=false;
    statusButton.setBackground(offColor);
    statusButton.setText(offText);
    statusButton.setForeground(offTextColor);
  }
  public void setStatus(boolean status) {
    boolean  oldStatus = this.status;
    this.status = status;
    propertyChangeListeners.firePropertyChange("status", new Boolean(oldStatus), new Boolean(status));
  }
  public boolean getStatus() {
    return status;
  }
  public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
    super.removePropertyChangeListener(l);
    propertyChangeListeners.removePropertyChangeListener(l);
  }
  public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
    super.addPropertyChangeListener(l);
    propertyChangeListeners.addPropertyChangeListener(l);
  }
  public void setStatusNameFont(java.awt.Font statusNameFont) {
    java.awt.Font  oldStatusNameFont = this.statusNameFont;
    this.statusNameFont = statusNameFont;
    propertyChangeListeners.firePropertyChange("statusNameFont", oldStatusNameFont, statusNameFont);
  }
  public java.awt.Font getStatusNameFont() {
    return statusNameFont;
  }
  public void setHasBorder(boolean hasBorder) {
    boolean  oldHasBorder = this.hasBorder;
    this.hasBorder = hasBorder;
    propertyChangeListeners.firePropertyChange("hasBorder", new Boolean(oldHasBorder), new Boolean(hasBorder));
  }
  public boolean isHasBorder() {
    return hasBorder;
  }
  public void setLightSize(java.awt.Dimension lightSize) {
    java.awt.Dimension  oldLightSize = this.lightSize;
    this.lightSize = lightSize;
    propertyChangeListeners.firePropertyChange("lightSize", oldLightSize, lightSize);
  }
  public java.awt.Dimension getLightSize() {
    return lightSize;
  }
	public void setOnText(String onText) {
//		hasButtonText = (!onText.isEmpty() || !offText.isEmpty());
		this.onText = onText;
    if (status) turnOn();
	}
	public String getOnText() {
		return onText;
	}
	public void setOffText(String offText) {
//		hasButtonText = (!onText.isEmpty() || !offText.isEmpty());
		this.offText = offText;
    if (!status) turnOff();
	}
	public String getOffText() {
		return offText;
	}
	public void setOnTextColor(Color onTextColor) {
		this.onTextColor = onTextColor;
    if (status) turnOn();
	}
	public Color getOnTextColor() {
		return onTextColor;
	}
	public void setOffTextColor(Color offTextColor) {
		this.offTextColor = offTextColor;
    if (!status) turnOff();
	}
	public Color getOffTextColor() {
		return offTextColor;
	}
	public void setForeground(Color c) {
	    if (statusButton != null) {
		setOnColor(c);
	    }
	}
	public void setBackground(Color c) {
	    if (statusButton != null) {
		setOffColor(c);
	    }
	}
}
