package edu.ucla.astro.irlab.util.process;

public class ProcessInfo {
  String[] command;
  long idNumber;   //. normally, use System.currentTimeMillis()

  public ProcessInfo(String[] command) {
    this(command, 0);
  }
  public ProcessInfo(String[] command, long id) {
    this.command = command;
    this.idNumber = id;
  }
  public void setCommand(String[] command) {
    this.command = command;
  }
  public void setIdNumber(long idNumber) {
    this.idNumber = idNumber;
  }
  public String getCommandString() {
    return arrayToString(command);
  }
  public String[] getCommand() {
    return command;
  }
  public long getIdNumber() {
    return idNumber;
  }
  private String arrayToString(String[] array) {
    StringBuffer s = new StringBuffer();
    for (int ii=0; ii<array.length; ii++) {
      s.append(array[ii]+" ");
    }
    if (s.length() > 0) {
			s.deleteCharAt(s.length()-1);
    }
    return s.toString();
  }

}