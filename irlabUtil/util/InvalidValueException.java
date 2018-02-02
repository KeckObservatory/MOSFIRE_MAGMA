package edu.ucla.astro.irlab.util;

public class InvalidValueException extends Exception {

  private String variableName;
  private String variableType;
  private String invalidValue;
  public InvalidValueException() {
    this("");
  }
  public InvalidValueException(String message) {
    this(message, "", "", "");
  }
  public InvalidValueException(String message, String variableName, String variableType, String invalidValue) {
    super(message);
    this.variableName=variableName;
    this.variableType=variableType;
    this.invalidValue=invalidValue;
  }
  public String getInvalidValue() {
    return invalidValue;
  }
  public String getVariableName() {
    return variableName;
  }
  public String getVariableType() {
    return variableType;
  }
  public void setInvalidValue(String invalidValue) {
    this.invalidValue = invalidValue;
  }
  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }
  public void setVariableType(String variableType) {
    this.variableType = variableType;
  }

}