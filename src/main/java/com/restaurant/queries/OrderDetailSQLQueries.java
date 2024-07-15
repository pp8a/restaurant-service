package com.restaurant.queries;

/**
 * Utility class containing SQL queries for the OrderDetail entity.
 */
public class OrderDetailSQLQueries {
	/**
     * SQL query to insert a new order detail into the database.
     */
	public static final String INSERT_DETAIL = "INSERT INTO order_details (order_status_id, total_amount) "
			+ "VALUES (?, ?)";
	
	/**
     * SQL query to retrieve all order details from the database.
     */
	public static final String GET_ALL_DETAILS = "SELECT od.id, od.total_amount, "
			+ "os.id as status_id, os.status_name "
            + "FROM order_details od "
            + "INNER JOIN order_status os ON od.order_status_id = os.id ";
	
	/**
     * SQL query to retrieve an order detail by its ID from the database.
     */
	public static final String GET_DETAIL_BY_ID = "SELECT od.id, od.total_amount, "
			+ "os.id as status_id, os.status_name "
            + "FROM order_details od "
            + "INNER JOIN order_status os ON od.order_status_id = os.id "
            + "WHERE od.id = ?";
	
	/**
     * SQL query to retrieve products associated with a specific order detail ID from the database.
     */
	public static final String GET_PRODUCTS_BY_ORDER_DETAIL_ID = "SELECT p.id, "
			+ "p.name, p.price, p.quantity, p.available, "
			+ "pc.id as category_id, pc.name as category_name, pc.type as category_type "
            + "FROM products p "
            + "INNER JOIN product_categories pc ON p.category_id = pc.id "
            + "INNER JOIN order_detail_products odp ON p.id = odp.product_id "
            + "WHERE odp.order_detail_id = ?";	
	
	/**
     * SQL query to update an existing order detail in the database.
     */
	public static final String UPDATE_DETAIL = "UPDATE order_details "
			+ "SET order_status_id = ?, total_amount = ? WHERE id = ?";
	
	/**
     * SQL query to delete products associated with a specific order detail ID from the database.
     */
	public static final String DELETE_ORDER_DETAIL_PRODUCTS_BY_ORDER_ID = "DELETE FROM order_detail_products "
			+ "WHERE order_detail_id = ?";
	
	/**
     * SQL query to delete an order approval by order detail ID from the database.
     */
	public static final String DELETE_APPROVAL_BY_ORDER_ID = "DELETE FROM order_approvals "
			+ "WHERE order_detail_id = ?";
	
	/**
     * SQL query to delete an order detail by its ID from the database.
     */
	public static final String DELETE_DETAIL = "DELETE FROM order_details WHERE id = ?";	
	
	private OrderDetailSQLQueries() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
