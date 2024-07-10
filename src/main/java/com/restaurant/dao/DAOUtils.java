package com.restaurant.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.restaurant.entity.IdentifiableEntity;

/**
 * Utility class providing common DAO-related functions.
 */
public class DAOUtils {
	/**
     * Private constructor to prevent instantiation.
     */
	private DAOUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
	
	/**
     * Sets the generated key for an entity after an insert operation.
     * 
     * @param pstmt the PreparedStatement used for the insert operation
     * @param entity the entity to set the generated key for
     * @throws SQLException if a database access error occurs
     */
	public static void setGeneratedKey(PreparedStatement pstmt, IdentifiableEntity entity) throws SQLException {
		try(ResultSet generatedKeys = pstmt.getGeneratedKeys()){
			if(generatedKeys.next()) {
				entity.setId(generatedKeys.getInt(1));
			}
		}	
	}
}
