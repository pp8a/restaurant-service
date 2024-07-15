package com.restaurant.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class for initializing test data in the database.
 * This class provides methods to create tables and insert test data.
 */
public class TestDataInitializer {
	
	private TestDataInitializer() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
	
	/**
     * Executes the given SQL statement.
     *
     * @param connection the database connection
     * @param sql the SQL statement to execute
     * @throws SQLException if a database access error occurs
     */
	private static void executeStatement(Connection connection, String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }	
	
	/**
     * Creates the product_categories table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
	public static void createProductCategoriesTable(Connection connection) throws SQLException {
        String createProductCategoriesTable = "CREATE TABLE product_categories ("
                + "    id SERIAL PRIMARY KEY,"
                + "    name VARCHAR(100) NOT NULL,"
                + "    type VARCHAR(50) NOT NULL"
                + ")";
        executeStatement(connection, createProductCategoriesTable);
    }

	/**
     * Creates the products table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void createProductsTable(Connection connection) throws SQLException {
        String createProductsTable = "CREATE TABLE products ("
                + "    id SERIAL PRIMARY KEY,"
                + "    name VARCHAR(100) NOT NULL,"
                + "    price DECIMAL(10, 2) NOT NULL,"
                + "    quantity INT NOT NULL,"
                + "    available BOOLEAN NOT NULL,"
                + "    category_id INT,"
                + "    FOREIGN KEY (category_id) REFERENCES product_categories(id)"
                + ")";
        executeStatement(connection, createProductsTable);        
    }
    
    /**
     * Creates the order_status table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void createOrderStatusTable(Connection connection) throws SQLException {
        String createOrderStatusTable = "CREATE TABLE order_status ("
                + "    id SERIAL PRIMARY KEY,"
                + "    status_name VARCHAR(50) NOT NULL"
                + ")";
        executeStatement(connection, createOrderStatusTable);
    }

    /**
     * Creates the order_details table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void createOrderDetailsTable(Connection connection) throws SQLException {
        String createOrderDetailsTable = "CREATE TABLE order_details ("
                + "    id SERIAL PRIMARY KEY,"
                + "    order_status_id INT,"
                + "    total_amount DECIMAL(10, 2) NOT NULL,"
                + "    FOREIGN KEY (order_status_id) REFERENCES order_status(id)"
                + ")";
        executeStatement(connection, createOrderDetailsTable);
    }

    /**
     * Creates the order_approvals table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void createOrderApprovalTable(Connection connection) throws SQLException {
        String createOrderApprovalTable = "CREATE TABLE order_approvals ("
                + "    id SERIAL PRIMARY KEY,"
                + "    order_detail_id INT,"
                + "    FOREIGN KEY (order_detail_id) REFERENCES order_details(id)"
                + ")";
        executeStatement(connection, createOrderApprovalTable);
    }

    /**
     * Creates the order_detail_products table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void createOrderDetailProductsTable(Connection connection) throws SQLException {
        String createOrderDetailProductsTable = "CREATE TABLE order_detail_products ("
                + "    order_detail_id INT,"
                + "    product_id INT,"
                + "    PRIMARY KEY (order_detail_id, product_id),"
                + "    FOREIGN KEY (order_detail_id) REFERENCES order_details(id),"
                + "    FOREIGN KEY (product_id) REFERENCES products(id)"
                + ")";
        executeStatement(connection, createOrderDetailProductsTable);
    }
    
    /**
     * Inserts test data into the product_categories table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void insertTestProductCategory(Connection connection) throws SQLException {
        String insertProductCategory = "INSERT INTO product_categories (name, type) VALUES "
                + "('Category 1', 'Type 1'),"
                + "('Appetizers', 'Starter'),"
                + "('Main Courses', 'Main'),"
                + "('Desserts', 'Dessert'),"
                + "('Beverages', 'Drink'),"
                + "('Salads', 'Starter')";
        executeStatement(connection, insertProductCategory);
    }
    
    /**
     * Inserts test data into the products table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void insertTestProduct(Connection connection) throws SQLException {
        String insertProduct = "INSERT INTO products "
        		+ "(name, price, quantity, available, category_id) "
        		+ "VALUES "
                + "('Test product', 5.99, 50, TRUE, 1),"
                + "('Spring Rolls', 5.99, 50, TRUE, 1),"
                + "('Grilled Chicken', 12.99, 30, TRUE, 2),"
                + "('Chocolate Cake', 6.99, 20, TRUE, 3),"
                + "('Lemonade', 2.99, 100, TRUE, 4),"
                + "('Vodka', 10.99, 45, TRUE, 4),"
                + "('Caesar Salad', 7.99, 40, TRUE, 5)";
        executeStatement(connection, insertProduct);
    }

    /**
     * Inserts test data into the order_status table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void insertTestOrderStatus(Connection connection) throws SQLException {
        String insertOrderStatus = "INSERT INTO order_status (status_name) VALUES "
                + "('ACCEPTED'),"
                + "('APPROVED'),"
                + "('CANCELLED'),"
                + "('PAID')";
        executeStatement(connection, insertOrderStatus);
    }
    
    /**
     * Inserts test data into the order_details table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void insertTestOrderDetails(Connection connection) throws SQLException {
        String insertOrderDetails = "INSERT INTO order_details (order_status_id, total_amount) "
        		+ "VALUES "
                + "(1, 25.97),"
                + "(2, 10.98),"
                + "(3, 23.98)";
        executeStatement(connection, insertOrderDetails);
    }
    
    /**
     * Inserts test data into the order_approvals table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void insertTestOrderApproval(Connection connection) throws SQLException {
        String insertOrderApproval = "INSERT INTO order_approvals (order_detail_id) "
        		+ "VALUES "
                + "(2)";
        executeStatement(connection, insertOrderApproval);
    }
    
    /**
     * Inserts test data into the order_detail_products table.
     *
     * @param connection the database connection
     * @throws SQLException if a database access error occurs
     */
    public static void insertTestOrderDetailProduct(Connection connection) throws SQLException {
        String insertOrderDetailProduct = "INSERT INTO order_detail_products (order_detail_id, product_id) "
        		+ "VALUES "
                + "(1, 1),"
                + "(1, 2),"
                + "(1, 3),"
                + "(2, 4),"
                + "(2, 6),"
                + "(3, 5),"
                + "(3, 2)";
        executeStatement(connection, insertOrderDetailProduct);
    }
}