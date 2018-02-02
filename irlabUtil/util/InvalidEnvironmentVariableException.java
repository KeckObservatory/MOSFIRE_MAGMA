package edu.ucla.astro.irlab.util;

public class InvalidEnvironmentVariableException extends Exception {
	public InvalidEnvironmentVariableException() {
		super();
	}

	public InvalidEnvironmentVariableException(String message) {
		super(message);
	}

	public InvalidEnvironmentVariableException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidEnvironmentVariableException(Throwable cause) {
		super(cause);
	}

}
