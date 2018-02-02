package edu.ucla.astro.irlab.util.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public abstract class GenericController {

  private GenericModel myModel;
  public GenericController() {
    this(new GenericModel());
  }

  public GenericController(GenericModel newModel) {
    this.setModel(newModel);
  }

  public GenericModel getModel() {
    return myModel;
  }

  public void setModel(GenericModel newModel) {
    this.myModel=newModel;
    myModel.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        model_propertyChange(e);
      }
    });  }

  abstract public void model_propertyChange(PropertyChangeEvent e);

}