package com.restaurant.dao;

import java.sql.Connection;

/**
 * Abstract class providing basic DAO functionality.
 * 
 * @param <T> the type of the entity
 * @param <I> the type of the entity ID
 */
public abstract class AbstractDao<T, I> extends BaseDao implements Dao<T, I> {
	 
	protected AbstractDao() {
		super();
	}	
	
	protected AbstractDao(Connection connection) {
		super(connection);
	}
}
