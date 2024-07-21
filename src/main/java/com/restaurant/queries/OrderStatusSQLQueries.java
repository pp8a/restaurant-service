package com.restaurant.queries;

/**
 * Utility class containing SQL queries for the OrderStatus entity.
 */
public class OrderStatusSQLQueries {
	/**
     * SQL query to insert a new order status into the database.
     */
	public static final String INSERT_STATUS = "INSERT INTO order_status (status_name) "
			+ "VALUES (?)";
	
	/**
     * SQL query to retrieve an order status by its ID from the database.
     */
	public static final String GET_STATUS_BY_ID = "SELECT * FROM order_status "
			+ "WHERE id = ?";
	
	/**
     * SQL query to retrieve all order statuses from the database.
     */
	public static final String GET_ALL_STATUS = "SELECT * FROM order_status";
	
	/**
     * SQL query to update an existing order status in the database.
     */
	public static final String UPDATE_STATUS = "UPDATE order_status "
			+ "SET status_name = ? WHERE id = ?";
	
	/**
     * SQL query to delete an order status from the database by its ID.
     */
	public static final String DELETE_STATUS = "DELETE FROM order_status "
			+ "WHERE id = ?";	
	
	private OrderStatusSQLQueries() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
