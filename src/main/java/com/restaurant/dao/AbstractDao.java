package com.restaurant.dao;

import java.sql.Connection;

/**
 * Abstract class providing basic DAO functionality.
 * 
 * @param <T> the type of the entity
 * @param <I> the type of the entity ID
 */
public abstract class AbstractDao<T, I> extends BaseDao implements Dao<T, I> {
	 /**
     * Default constructor.
     */
	protected AbstractDao() {
		super();
	}
	
	/**
     * Constructor with connection parameter.
     * 
     * @param connection the database connection
     */
	protected AbstractDao(Connection connection) {
		super(connection);
	}
}
