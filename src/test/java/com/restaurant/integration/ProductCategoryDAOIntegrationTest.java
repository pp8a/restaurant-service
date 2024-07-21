package com.restaurant.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.restaurant.dao.impl.ProductCategoryDAO;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.ProductCategorySQLQueries;

/**
 * Integration tests for the {@link ProductCategoryDAO} class.
 * Uses Testcontainers for PostgreSQL to ensure tests run in an isolated environment.
 */
@Testcontainers
class ProductCategoryDAOIntegrationTest {
	@Container
    public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private Connection connection;
    private ProductCategoryDAO productCategoryDAO;
    
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
        productCategoryDAO = new ProductCategoryDAO(connection);
    }
    
    /**
     * Tests the creation of an {@link ProductCategory} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateProductCategory() throws SQLException {
        ProductCategory category = new ProductCategory();
        category.setName("New Category");
        category.setType("New Type");

        productCategoryDAO.save(category);

        int generatedId = category.getId();        
        String selectQuery = ProductCategorySQLQueries.GET_CATEGORY_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, generatedId);
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(resultSet.next());
        assertEquals("New Category", resultSet.getString("category_name"));
        assertEquals("New Type", resultSet.getString("category_type"));
    }
    
    /**
     * Tests the update of an {@link ProductCategory} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateProductCategory() throws SQLException {       
        ProductCategory category = productCategoryDAO.getById(1).orElseThrow(SQLException::new);
        category.setName("Updated Category");
        category.setType("Updated Type");

        productCategoryDAO.save(category);

        String selectQuery = ProductCategorySQLQueries.GET_CATEGORY_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, category.getId());
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(resultSet.next());
        assertEquals("Updated Category", resultSet.getString("category_name"));
        assertEquals("Updated Type", resultSet.getString("category_type"));
    }
    
    /**
     * Tests retrieving an {@link ProductCategory} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetCategoryById() throws SQLException {
        ProductCategory category = productCategoryDAO.getById(1).orElseThrow(SQLException::new);

        assertEquals(1, category.getId());
        assertEquals("Category 1", category.getName());
        assertEquals("Type 1", category.getType());
    }
    
    /**
     * Tests retrieving all {@link ProductCategory} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAllCategories() throws SQLException {

        List<ProductCategory> categories = productCategoryDAO.getAll();

        assertEquals(6, categories.size());
        ProductCategory category = categories.get(0);
        assertEquals("Category 1", category.getName());
        assertEquals("Type 1", category.getType());
    }
    
    /**
     * Tests deleting an {@link ProductCategory} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteProductCategory() throws SQLException { 
        productCategoryDAO.delete(1);

        String selectQuery = ProductCategorySQLQueries.GET_CATEGORY_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, 1);
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(!resultSet.next());
    }
}
