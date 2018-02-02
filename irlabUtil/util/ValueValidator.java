package edu.ucla.astro.irlab.util;

/**
 * <p>Title: ValueValidator/p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public interface ValueValidator {
  public boolean isValueValid(Object value);
  public String getCriteria();
}