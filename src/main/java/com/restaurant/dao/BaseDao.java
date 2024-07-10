package com.restaurant.dao;

import java.sql.Connection;

import com.restaurant.database.DatabaseConnection;

/**
 * Base class providing common DAO functionality.
 */
public abstract class BaseDao {
	protected Connection connection;
	
	/**
     * Default constructor initializing the connection.
     */
	protected BaseDao() {
		this.connection = DatabaseConnection.getInstance().getConnection();
	}
	
	/**
     * Constructor with connection parameter for testing.
     * 
     * @param connection the database connection
     */
	protected BaseDao(Connection connection) {
		super();
		this.connection = connection;
	}
}
