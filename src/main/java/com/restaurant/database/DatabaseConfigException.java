package com.restaurant.database;

/**
 * Custom exception class for database configuration errors.
 */
public class DatabaseConfigException extends RuntimeException{	
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new DatabaseConfigException with the specified detail message.
     *
     * @param message the detail message.
     */
	public DatabaseConfigException(String message) {
		super(message);
	}
	
	/**
     * Constructs a new DatabaseConfigException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
	public DatabaseConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
