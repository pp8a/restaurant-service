package com.restaurant.queries;

/**
 * Utility class containing SQL queries for the ProductCategory entity.
 */
public class ProductCategorySQLQueries {
	
	/**
     * SQL query to retrieve a product category by its ID from the database.
     */
	public static final String GET_CATEGORY_BY_ID = "SELECT pc.id AS category_id, pc.name AS category_name, pc.type AS category_type "
            + "FROM product_categories pc "
			+ "WHERE pc.id = ?";
	
	/**
     * SQL query to retrieve all product categories from the database.
     */
	public static final String GET_ALL_CATEGORIES = "SELECT pc.id AS category_id, pc.name AS category_name, pc.type AS category_type "
            + "FROM product_categories pc";
	
	/**
     * SQL query to retrieve all products by category ID from the database.
     */
	public static final String GET_PRODUCTS_BY_CATEGORY_ID = "SELECT * FROM products WHERE category_id = ?";
	
	/**
     * SQL query to retrieve a product category along with its associated products by category ID.
     * Note: This query is not currently used.
     */
	public static final String GET_CATEGORY_WITH_PRODUCTS_BY_ID = 
	        "SELECT pc.id AS category_id, pc.name AS category_name, pc.type AS category_type, " +
	        "p.id AS product_id, p.name AS product_name, p.price, p.quantity, p.available " +
	        "FROM product_categories pc " +
	        "LEFT JOIN products p ON pc.id = p.category_id " +
	        "WHERE pc.id = ?";
	
	/**
     * SQL query to insert a new product category into the database.
     */
	public static final String INSERT_CATEGORY = "INSERT INTO product_categories (name, type) VALUES (?, ?)";
	
	/**
     * SQL query to update an existing product category in the database.
     */
	public static final String UPDATE_CATEGORY = "UPDATE product_categories SET name = ?, type = ? WHERE id = ?";
	
	/**
     * SQL query to delete product associations in the order_detail_products table by category ID.
     */
	public static final String DELETE_ORDER_DETAIL_PRODUCTS_BY_CATEGORY_ID = 
	        "DELETE FROM order_detail_products WHERE product_id IN (SELECT id FROM products WHERE category_id = ?)";
	
	/**
     * SQL query to delete products by category ID from the database.
     */
	public static final String DELETE_PRODUCTS_BY_CATEGORY_ID = 
	        "DELETE FROM products WHERE category_id = ?";
	
	/**
     * SQL query to delete a product category from the database by its ID.
     */
	public static final String DELETE_CATEGORY = 
	        "DELETE FROM product_categories WHERE id = ?";
	
	/**
     * Private constructor to prevent instantiation of this utility class.
     */
	private ProductCategorySQLQueries() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
