package com.restaurant.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.restaurant.dao.entity.ProductDAO;
import com.restaurant.entity.Product;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.ProductSQLQueries;

/**
 * Integration tests for the {@link ProductDAO} class.
 * Uses Testcontainers for PostgreSQL to ensure tests run in an isolated environment.
 */
@Testcontainers
class ProductDAOIntegrationTest {
	
	@Container
	public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpassword");
			
	
	private Connection connection;
	private ProductDAO productDAO;
	
	/**
     * Sets up the database connection and initializes the DAO.
     *
     * @throws SQLException if a database access error occurs.
     */
	@BeforeEach
	public void setUp() throws SQLException {
		connection = DriverManager.getConnection(
				postgreSQLContainer.getJdbcUrl(), 
				postgreSQLContainer.getUsername(), 
				postgreSQLContainer.getPassword());
		TestDataInitializer.createProductCategoriesTable(connection);
	    TestDataInitializer.createProductsTable(connection);
	    TestDataInitializer.createOrderStatusTable(connection);
	    TestDataInitializer.createOrderDetailsTable(connection);	    
	    TestDataInitializer.createOrderDetailProductsTable(connection);
        
	    TestDataInitializer.insertTestProductCategory(connection);
	    TestDataInitializer.insertTestProduct(connection);
	    TestDataInitializer.insertTestOrderStatus(connection);
	    TestDataInitializer.insertTestOrderDetails(connection);
	    TestDataInitializer.insertTestOrderDetailProduct(connection);
		productDAO = new ProductDAO(connection);
	}
	
	/**
     * Tests the creation of an {@link Product} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
	@Test
	void testCreateProduct() throws SQLException {		
		Product product = new Product();
		product.setName("New product");
		product.setPrice(new BigDecimal("55.99"));
		product.setQuantity(10);
		product.setAvailable(true);
		
		ProductCategory category = new ProductCategory();
		category.setId(1);		
		product.setProductCategory(category);
		
		productDAO.save(product);
		
		int generatedId = product.getId();
	    String selectQuery = ProductSQLQueries.GET_PRODUCT_BY_ID;
	    
	    PreparedStatement stmt = connection.prepareStatement(selectQuery);
	    stmt.setInt(1, generatedId);
	    ResultSet resultSet = stmt.executeQuery();
		
		assertTrue(resultSet.next());
		assertEquals("New product", resultSet.getString("name"));
		assertEquals(new BigDecimal("55.99"), resultSet.getBigDecimal("price"));
		assertEquals(10, resultSet.getInt("quantity"));
		assertTrue(resultSet.getBoolean("available"));
		assertEquals(1, resultSet.getInt("category_id"));		
        assertEquals("Category 1", resultSet.getString("category_name")); 
        assertEquals("Type 1", resultSet.getString("category_type")); 
	}
	
	/**
     * Tests the update of an {@link Product} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
	@Test
	void testUpdateProduct() throws SQLException {			
		Product product = productDAO.getById(1).orElseThrow(SQLException::new);	
		product.setName("Update Product");
		product.setPrice(new BigDecimal("19.99"));
		product.setQuantity(10);
		product.setAvailable(true);
						
		productDAO.save(product);
		
		String selectQuery = ProductSQLQueries.GET_PRODUCT_BY_ID;
		
		PreparedStatement stmt = connection.prepareStatement(selectQuery);
		stmt.setInt(1, product.getId());
		ResultSet resultSet = stmt.executeQuery();
		
		assertTrue(resultSet.next());
		assertEquals("Update Product", resultSet.getString("name"));
		assertEquals(new BigDecimal("19.99"), resultSet.getBigDecimal("price"));
		assertEquals(10, resultSet.getInt("quantity"));
		assertTrue(resultSet.getBoolean("available"));
		assertEquals(1, resultSet.getInt("category_id"));	
		assertEquals("Category 1", resultSet.getString("category_name")); 
        assertEquals("Type 1", resultSet.getString("category_type")); 
	}
	
	/**
     * Tests retrieving an {@link Product} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
	@Test
    void testGetProductById() throws SQLException {        
        Product product = productDAO.getById(1).orElseThrow(SQLException::new);
        
        assertEquals("Test product", product.getName());
        assertEquals(new BigDecimal("5.99"), product.getPrice());
        assertEquals(50, product.getQuantity());
        assertTrue(product.isAvailable());
        assertEquals(1, product.getProductCategory().getId());
    }

	/**
     * Tests retrieving all {@link Product} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAllProducts() throws SQLException {        
        List<Product> products = productDAO.getAll();
        
        assertEquals(7, products.size());
        Product product = products.get(0);
        assertEquals("Test product", product.getName());
        assertEquals(new BigDecimal("5.99"), product.getPrice());
        assertEquals(50, product.getQuantity());
        assertTrue(product.isAvailable());
        assertEquals(1, product.getProductCategory().getId());
    }

    /**
     * Tests deleting an {@link Product} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteProduct() throws SQLException {        
        productDAO.delete(1);
        
        String selectQuery = ProductSQLQueries.GET_PRODUCT_BY_ID;
        
        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, 1);
        ResultSet resultSet = stmt.executeQuery();
        
        assertTrue(!resultSet.next());
    }   
    
}
