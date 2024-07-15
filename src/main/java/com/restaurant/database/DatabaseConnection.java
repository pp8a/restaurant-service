package com.restaurant.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing the database connection.
 */
public class DatabaseConnection {	
	private Connection connection;	
	
	private DatabaseConnection() {
		DatabaseConfig config = DatabaseConfig.load();
		try {			 
			Class.forName(config.getDriver());
			this.connection = DriverManager.getConnection(
					config.getUrl(), 
					config.getUsername(), 
					config.getPassword());
			DatabaseInitializer.initializeDatabase(connection);
		} catch (SQLException | ClassNotFoundException | IOException e) {
			throw new DatabaseConfigException("Failed to connect to the database", e);
		}
	}
	
	/**
     * Holder class for the singleton instance.
     */
	private static class Holder {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

	 /**
     * Returns the singleton instance of the DatabaseConnection.
     *
     * @return the singleton instance.
     */
    public static DatabaseConnection getInstance() {
        return Holder.INSTANCE;
    }
    
    /**
     * Returns the database connection.
     *
     * @return the database connection.
     */
	public Connection getConnection() {
		return connection;
	}
}
