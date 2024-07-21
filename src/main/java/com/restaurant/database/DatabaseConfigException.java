package com.restaurant.database;

/**
 * Custom exception class for database configuration errors.
 */
public class DatabaseConfigException extends RuntimeException{	
	private static final long serialVersionUID = 1L;
	
	public DatabaseConfigException(String message) {
		super(message);
	}	
	
	public DatabaseConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
