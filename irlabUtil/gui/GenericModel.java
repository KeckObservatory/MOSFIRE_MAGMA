package edu.ucla.astro.irlab.util.gui;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class GenericModel {

  public transient PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);

  public GenericModel() {
  }

  public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChangeListeners.removePropertyChangeListener(l);
  }
  public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChangeListeners.addPropertyChangeListener(l);
  }

}