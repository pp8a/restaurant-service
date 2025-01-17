package com.restaurant.dao;

import java.sql.Connection;

import com.restaurant.database.DatabaseConnection;

/**
 * Base class providing common DAO functionality.
 */
public abstract class BaseDao {
	protected Connection connection;
	
	protected BaseDao() {
		this.connection = DatabaseConnection.getInstance().getConnection();
	}	
	
	protected BaseDao(Connection connection) {
		super();
		this.connection = connection;
	}
}
