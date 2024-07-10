package com.restaurant.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface defining CRUD operations.
 * 
 * @param <T> the type of the entity
 * @param <I> the type of the entity ID
 */
public interface Dao <T, I> {
	/**
     * Retrieves an entity by its ID.
     * 
     * @param id the ID of the entity
     * @return an Optional containing the entity if found, or empty if not found
     * @throws SQLException if a database access error occurs
     */
	Optional<T> getById(I id) throws SQLException;
	
	/**
     * Retrieves all entities.
     * 
     * @return a list of all entities
     * @throws SQLException if a database access error occurs
     */
	List<T> getAll() throws SQLException;
	
	/**
     * Saves an entity.
     * 
     * @param t the entity to save
     * @return the saved entity
     * @throws SQLException if a database access error occurs
     */
	T save(T t) throws SQLException;
	
	/**
     * Deletes an entity by its ID.
     * 
     * @param id the ID of the entity to delete
     * @throws SQLException if a database access error occurs
     */
	void delete(I id) throws SQLException;
}
