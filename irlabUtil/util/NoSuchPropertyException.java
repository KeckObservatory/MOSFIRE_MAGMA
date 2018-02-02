package edu.ucla.astro.irlab.util;

public class NoSuchPropertyException extends Exception {

	public NoSuchPropertyException() {
		super();
	}

	public NoSuchPropertyException(String message) {
		super(message);
	}

	public NoSuchPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchPropertyException(Throwable cause) {
		super(cause);
	}

}
