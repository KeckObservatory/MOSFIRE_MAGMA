package edu.ucla.astro.irlab.util.process;

import java.util.HashSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class ProcessControl {


  private HashSet<ProcessListener> listeners = new HashSet<ProcessListener>();

  public ProcessControl() {
  }
  public void addProcessListener(ProcessListener l) {
      listeners.add(l);
  }
  public void removeProcessListener(ProcessListener l) {
      listeners.remove(l);
  }
  public void execute(String[] command) throws IOException, InterruptedException {
    execute(new ProcessInfo(command, System.currentTimeMillis()));
  }
  public void execute(ProcessInfo pi) throws IOException, InterruptedException {
    ExecuteCommandThread thread = new ExecuteCommandThread(pi);
    thread.start();
  }

  public void setExitCode(ProcessInfo process, int code) {
    if (listeners.isEmpty()) {
      System.out.println(process.getCommandString()+" x "+code);
    } else {
      //. scroll through listeners and call callback method
      for (java.util.Iterator ii=listeners.iterator(); ii.hasNext(); )
        ((ProcessListener)ii.next()).processExitCode(process, code);
    }
  }
  public void setProcessErrMessage(ProcessInfo process, String message) {
    if (listeners.isEmpty()) {
      System.out.println(process.getCommandString()+" : "+message);
    } else {
      //. scroll through listeners and call callback method
      for (java.util.Iterator ii=listeners.iterator(); ii.hasNext(); )
       ((ProcessListener)ii.next()).processErrMessage(process, message);
    }
  }
  public void setProcessOutMessage(ProcessInfo process, String message) {
    if (listeners.isEmpty()) {
      System.out.println(process.getCommandString()+" : "+message);
    } else {
      //. scroll through listeners and call callback method
      for (java.util.Iterator ii=listeners.iterator(); ii.hasNext(); )
       ((ProcessListener)ii.next()).processOutMessage(process, message);
    }
  }
  public class ExecuteCommandThread extends Thread {
    ProcessInfo pi;
    public ExecuteCommandThread(ProcessInfo pi) {
      this.pi = pi;
    }
    public void run() {
      try {
        Process proc = Runtime.getRuntime().exec(pi.getCommand());
        ProcessOutputDirector pod = new ProcessOutputDirector(pi, proc.getInputStream());
        ProcessErrorDirector ped = new ProcessErrorDirector(pi, proc.getErrorStream());
        pod.start();
        ped.start(); /* ? */
        //. when complete, set exit code
        setExitCode(pi, proc.waitFor());
      } catch (Exception e) {
        setProcessErrMessage(pi, "Error executing command:");
        setProcessErrMessage(pi, pi.getCommandString());
        setProcessErrMessage(pi, "ID: "+Long.toString(pi.getIdNumber()));
        setProcessErrMessage(pi, e.getMessage());
        setExitCode(pi, -1);
      }
    }
  }
  public class ProcessOutputDirector extends Thread {
    InputStream is;
    ProcessInfo pi;
    public ProcessOutputDirector(ProcessInfo pi, InputStream is) {
      this.is = is;
      this.pi = pi;
    }
    public void run() {
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line=null;
        while( (line=br.readLine()) != null) {
          setProcessOutMessage(pi, line);
        }
      } catch (IOException ioE) {
        ioE.printStackTrace();
      }
    }
  }
  public class ProcessErrorDirector extends Thread {
    InputStream is;
    ProcessInfo pi;
    public ProcessErrorDirector(ProcessInfo pi, InputStream is) {
      this.is = is;
      this.pi = pi;
    }
    public void run() {
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line=null;
        while( (line=br.readLine()) != null) {
          setProcessErrMessage(pi, line);
        }
      } catch (IOException ioE) {
        ioE.printStackTrace();
      }
    }
  }
}
