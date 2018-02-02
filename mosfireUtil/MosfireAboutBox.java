package edu.ucla.astro.irlab.mosfire.util;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import edu.ucla.astro.irlab.util.FileUtilities;

/**
 * <p>Title: MosfireAboutBox</p>
 * <p>Description: About Dialog for MOSFIRE Software</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA IR Lab</p>
 * @author Jason Weiss
 * @version 1.0
 */

public class MosfireAboutBox extends JDialog implements ActionListener {
  //. serialVersionUID created using serialver command 2006/07/11
	static final long serialVersionUID = -6174410998076893291L;

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton closeButton = new JButton();
  JLabel mosfireImageLabel = new JLabel();
  JLabel productLabel = new JLabel();
  JLabel versionLabel = new JLabel();
  JLabel releasedLabel = new JLabel();
  JLabel authorLabel = new JLabel();
  JLabel companyLabel = new JLabel();
  JLabel copyrightLabel = new JLabel();
  JLabel webLabel = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();
  String product =   "MOSFIRE Control Software";
  String version =   "Version 1.0";
  String released =   "Released: 12 September 2012";
  String author =    "Jason L. Weiss";
  String company =   "UCLA Infrared Lab";
  String copyright = "Copyright (c) 2010";
  String web = "http://www.astro.ucla.edu/irlab/mosfire/";
  public MosfireAboutBox() {
    this(null, "MOSFIRE Control Software");
  }

  public MosfireAboutBox(Frame parent, String productName) {
    super(parent);
    product=productName;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
  }
  //Component initialization
  private void jbInit() throws Exception  {
  	URL iconResource = MosfireAboutBox.class.getResource("images/mosfire_logo.gif");
  	if (iconResource != null) {
  	ImageIcon icon = new ImageIcon(iconResource);
  		if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
  			mosfireImageLabel.setIcon(new ImageIcon());
  		}
  	}
    this.setTitle("About");
    setResizable(false);
    panel1.setLayout(new BorderLayout());
    panel2.setLayout(new BorderLayout());
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    String fontName="Serif";
    productLabel.setFont(new Font(fontName, Font.BOLD, 24));
    productLabel.setText(product);
    versionLabel.setFont(new Font(fontName, Font.BOLD, 14));
    versionLabel.setText(version);
    releasedLabel.setFont(new Font(fontName, 0, 14));
    releasedLabel.setText(released);
    authorLabel.setFont(new Font(fontName, 0, 14));
    authorLabel.setText(author);
    companyLabel.setFont(new Font(fontName, 0, 14));
    companyLabel.setText(company);
    copyrightLabel.setFont(new Font(fontName, 0, 14));
    copyrightLabel.setText(copyright);
    webLabel.setFont(new Font(fontName, Font.ITALIC, 14));
    webLabel.setText(web);

    insetsPanel3.setLayout(new GridBagLayout());
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    closeButton.setText("Ok");
    closeButton.addActionListener(this);
    insetsPanel2.add(mosfireImageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(productLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    insetsPanel3.add(versionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    insetsPanel3.add(releasedLabel, new GridBagConstraints(0, 2, 1, 1, 0.0,0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    insetsPanel3.add(authorLabel, new GridBagConstraints(0, 3, 1, 1, 0.0,0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    insetsPanel3.add(companyLabel, new GridBagConstraints(0, 4, 1, 1, 0.0,0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    insetsPanel3.add(copyrightLabel, new GridBagConstraints(0, 5, 1, 1, 0.0,0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    insetsPanel3.add(webLabel, new GridBagConstraints(0, 6, 1, 1, 0.0,0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(closeButton, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  //Close the dialog
  void cancel() {
    dispose();
  }
  //Close the dialog on a button event
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == closeButton) {
      cancel();
    }
  }
  public void setLocationAtCenter(Component component) {
    //Center the window over the component
    Dimension thisSize = this.getSize();
    Dimension frameSize = component.getSize();
    Point frameLocation = component.getLocationOnScreen();
    this.setLocation((int)(frameLocation.getX())+(frameSize.width - thisSize.width)/2, (int)(frameLocation.getY())+(frameSize.height - thisSize.height)/2);
  }
  public void setVersion(String version) {
    this.version = version;
    versionLabel.setText(version);
  }
  public void setReleased(String released) {
    this.released = released;
    releasedLabel.setText(released);
  }
  public void setWeb(String web) {
    this.web = web;
    webLabel.setText(web);
  }
  public void setAuthor(String author) {
    this.author = author;
    authorLabel.setText(author);
  }
  public void setProduct(String product) {
    this.product = product;
    productLabel.setText(product);
  }
  public void setCompany(String company) {
    this.company = company;
    companyLabel.setText(company);
  }
  public void setCopyright(String copyright) {
    this.copyright = copyright;
    copyrightLabel.setText(copyright);
  }
}