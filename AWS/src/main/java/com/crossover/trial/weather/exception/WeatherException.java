package com.crossover.trial.weather.exception;

/**
 * An internal exception marker
 */
public class WeatherException extends Exception {

	private static final long serialVersionUID = 1L;
	final private String message;


	public WeatherException() {
		super();
		message="";
	}
	public WeatherException(String error) {
		super(error);
		message = error;
	}

	public String getMessage() {
		return message;
	}

}
