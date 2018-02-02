package edu.ucla.astro.irlab.util;

import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import org.jdom.*;

/**
 * <p>Title: XMLFileChooser</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class XMLFileChooser extends JFileChooser {

  JScrollPane xmlScrollPane = new JScrollPane();
  JTextArea xmlTextArea = new JTextArea();
  org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
  org.jdom.output.XMLOutputter outputter = new org.jdom.output.XMLOutputter();
  public static int XML_PREVIEW_MAX_CHARS = 5000;
  public static java.awt.Dimension DIM_XML_PREVIEW = new java.awt.Dimension(400,300);

  public XMLFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
    jbInit();
  }
  public XMLFileChooser(File currentDirectory) {
    super(currentDirectory);
    jbInit();
  }
  public XMLFileChooser() {
    super();
    jbInit();
  }
  private void jbInit() {
    xmlTextArea.setEditable(false);
    xmlScrollPane.getViewport().add(xmlTextArea);
    xmlScrollPane.setPreferredSize(DIM_XML_PREVIEW);
    this.setAccessory(xmlScrollPane);
    this.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
    	  if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
    	  	if (e.getNewValue() == null)
    	  		return;
    		  File f = (File)e.getNewValue();
    		  try {
    		  	if (f.isDirectory())
    		  		return;
    			  org.jdom.Document myDoc = builder.build(f);
    			  xmlTextArea.setText(outputter.outputString(myDoc));
    		  } catch (JDOMException jdEx) {
    			  StringBuffer fileText = new StringBuffer();
    			  char[] fileChars = new char[XML_PREVIEW_MAX_CHARS];
    			  FileReader reader;
    			  int charsRead;
    			  try {
    				  reader = new FileReader(f);
    				  BufferedReader breader = new BufferedReader(reader);
    				  charsRead = breader.read(fileChars, 0, XML_PREVIEW_MAX_CHARS);
    				  if (charsRead < 0)
    					  fileText.append("Error reading file.");
    				  else {
    					  if (charsRead == XML_PREVIEW_MAX_CHARS)
    						  fileText.append("WARNING: File has been truncated to show only first "+
    								  XML_PREVIEW_MAX_CHARS+" characters.\n\n");
    					  fileText.append(fileChars);
    				  }
    			  } catch (Exception ex) {  //. java.io.FileNotFoundException, java.io.IOException
    				  fileText = new StringBuffer("Error reading file: "+ex.getMessage());
    			  } finally {
    				  xmlTextArea.setText(fileText.toString());
    			  }
    		  } catch (IOException ioEx) {
    			  xmlTextArea.setText("File is not XML.");
    		  } finally {
    			  xmlTextArea.moveCaretPosition(0);
    		  }
    	  }
      }
    });
  }
}