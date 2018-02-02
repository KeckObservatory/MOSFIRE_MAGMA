package edu.ucla.astro.irlab.util.process;

/**
 * <p>Title: MOSFIRE Software Package</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCLA Infrared Imaging Detector Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public interface ProcessListener {
  public void processErrMessage(ProcessInfo process, String message);
  public void processOutMessage(ProcessInfo process, String message);
  public void processExitCode(ProcessInfo process, int code);
}