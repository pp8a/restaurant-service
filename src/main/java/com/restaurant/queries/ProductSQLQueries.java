package com.restaurant.queries;

/**
 * Utility class containing SQL queries for the Product entity.
 */
public class ProductSQLQueries {
	 /**
     * SQL query to insert a new product into the database.
     */
	public static final String INSERT_PRODUCT = ""
			+ "INSERT INTO products "
			+ "(name, price, quantity, available, category_id) "
			+ "VALUES (?, ?, ?, ?, ?)";	
	
	/**
     * SQL query to update an existing product in the database.
     */
	public static final String UPDATE_PRODUCT = ""
			+ "UPDATE products "
			+ "SET name = ?, price = ?, quantity = ?, available = ?, category_id = ? "
			+ "WHERE id = ?";
	
	/**
     * SQL query to retrieve a product by its ID from the database.
     */
	public static final String GET_PRODUCT_BY_ID = ""
			+ "SELECT p.id, p.name, p.price, p.quantity, p.available, "
			+ "pc.id AS category_id, pc.name AS category_name, pc.type AS category_type "
			+ "FROM products p "
			+ "INNER JOIN product_categories pc "
			+ "ON p.category_id = pc.id "
			+ "WHERE p.id = ?";
	
	/**
     * SQL query to retrieve all products from the database.
     */
	public static final String GET_ALL_PRODUCTS =  ""
			+ "SELECT p.id, p.name, p.price, p.quantity, p.available, "
			+ "pc.id AS category_id, pc.name AS category_name, pc.type AS category_type "
			+ "FROM products p "
			+ "INNER JOIN product_categories pc "
			+ "ON p.category_id = pc.id";
	
	/**
     * SQL query to delete product associations in the order_detail_products table by product ID.
     */
	public static final String DELETE_ORDER_DETAIL_PRODUCTS_BY_PRODUCT_ID = ""
			+ "DELETE FROM order_detail_products "
			+ "WHERE product_id = ?";	
	
	/**
     * SQL query to delete a product from the database by its ID.
     */
	public static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";	
	
	private ProductSQLQueries() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
