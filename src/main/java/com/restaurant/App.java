package com.restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the restaurant service application.
 * This class initializes the application and logs a welcome message.
 */
public class App 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	 /**
     * The main method to start the application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main( String[] args )
    {
        LOGGER.info("Welcome to the restaurant service");
    }
}
