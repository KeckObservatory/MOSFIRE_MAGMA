package edu.ucla.astro.irlab.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ServerStatusPanel extends JPanel implements PropertyChangeListener {
  ImageIcon lightOnIcon;
  ImageIcon lightOffIcon;
  JLabel serverLabel = new JLabel();
  JLabel switchLabel = new JLabel();
  JLabel lastAliveLabel = new JLabel();
  public final static Font DEFAULT_STATUS_FONT = new Font("Dialog", 0, 11);
  public final static Font DEFAULT_SERVER_FONT = new Font("Dialog", Font.BOLD, 11);
  public final static Font DEFAULT_LASTALIVE_FONT = new Font("Dialog", 0, 9);
  String serverName="";
  boolean showLastAlive = false;
  boolean doSwitch = true;
  StringProperty lastAliveProperty;
  public ServerStatusPanel(String serverName, StringProperty lastAliveProperty, boolean showLastAlive) {
  	this.serverName = serverName;
  	this.showLastAlive = showLastAlive;
  	this.lastAliveProperty = lastAliveProperty;

  	URL lightOnPng = getClass().getResource("images/lighton.png");
		URL lightOffPng = getClass().getResource("images/lightoff.png");

		if (lightOnPng == null) {
			doSwitch = false;
		} else {
			lightOnIcon = new ImageIcon(lightOnPng);
  		if (lightOffPng == null) {
  			doSwitch = false;
  		} else {
    		lightOffIcon = new ImageIcon(lightOffPng);
  		}
		}
  	
  	if (doSwitch) {
  		switchLabel.setIcon(lightOffIcon);
  	}
  	init();
  	
  	lastAliveProperty.addPropertyChangeListener(this);
  }
  private void init() {
    serverLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    switchLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    lastAliveLabel.setBorder(BorderFactory.createLoweredBevelBorder());

    serverLabel.setText(serverName);
    serverLabel.setToolTipText("Server Name: "+serverName);
    serverLabel.setMinimumSize(new Dimension(65,20));
    serverLabel.setPreferredSize(new Dimension(65,20));
    serverLabel.setHorizontalAlignment(JLabel.CENTER);
    serverLabel.setVerticalAlignment(JLabel.CENTER);
    serverLabel.setFont(DEFAULT_SERVER_FONT);
    switchLabel.setToolTipText("Server Running?");
    switchLabel.setMinimumSize(new Dimension(17, 20));
    switchLabel.setPreferredSize(new Dimension(20, 20)); 
    switchLabel.setHorizontalAlignment(JLabel.CENTER);
    switchLabel.setVerticalAlignment(JLabel.CENTER);
    lastAliveLabel.setText(lastAliveProperty.getValue());
    lastAliveLabel.setToolTipText("Server Last Alive");
    lastAliveLabel.setMinimumSize(new Dimension(80,20));
    lastAliveLabel.setPreferredSize(new Dimension(90,20));
    lastAliveLabel.setHorizontalAlignment(JLabel.CENTER);
    lastAliveLabel.setVerticalAlignment(JLabel.CENTER);
    lastAliveLabel.setFont(DEFAULT_LASTALIVE_FONT);

    this.setLayout(new GridBagLayout());
    this.add(serverLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,
      GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
    if (doSwitch) {
    this.add(switchLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0,
      GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
    }
    if (showLastAlive) {
    	this.add(lastAliveLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0,
    			GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
    }
  }

  public void setSwitch(boolean status) {
  	if (doSwitch) {
  		if (status) 
  			switchLabel.setIcon(lightOnIcon);
  		else 
  			switchLabel.setIcon(lightOffIcon);
  		switchLabel.repaint();
  	}
  }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(lastAliveProperty.getName())) {
			lastAliveLabel.setText(lastAliveProperty.getValue());
			lastAliveLabel.setToolTipText("lastalive: "+lastAliveProperty.getValue());
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					setSwitch(!lastAliveProperty.getValue().contains("ERROR"));
				}
			});
		}
		
	}
}
