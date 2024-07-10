package com.restaurant.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.restaurant.dao.entity.ProductCategoryDAO;
import com.restaurant.entity.Product;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.ProductCategorySQLQueries;

/**
 * Unit tests for the {@link ProductCategoryDAO} class.
 */
class ProductCategoryDAOTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategoryDAO.class);
    
    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @Mock
    ResultSet mockResultSet;

    private ProductCategoryDAO categoryDAO;

    /**
     * Sets up the test environment by initializing mocks and the {@link ProductCategoryDAO} instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDAO = new ProductCategoryDAO(mockConnection);
        try {
            setupMockForGetCategoryById();
        } catch (SQLException e) {
            LOGGER.error("Error setting up mock prepared statement", e);
        }
    }

    private void setupMockForGetCategoryById() throws SQLException {
        when(mockConnection.prepareStatement(ProductCategorySQLQueries.GET_CATEGORY_BY_ID))
               .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    /**
     * Tests the creation of an {@link ProductCategory} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateCategory() throws SQLException {
        when(mockConnection.prepareStatement(ProductCategorySQLQueries.INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS))
               .thenReturn(mockPreparedStatement);

        ProductCategory category = createSampleProductCategory();

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        //for save
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        
        categoryDAO.save(category);

        verifyMockPreparedStatementForCategory(category);
        verify(mockPreparedStatement).executeUpdate();
    }

    private void verifyMockPreparedStatementForCategory(ProductCategory category) throws SQLException {
        verify(mockPreparedStatement, times(1)).setString(1, category.getName());
        verify(mockPreparedStatement, times(1)).setString(2, category.getType());
    }

    /**
     * Tests the update of an {@link ProductCategory} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateCategory() throws SQLException {
        when(mockConnection.prepareStatement(ProductCategorySQLQueries.UPDATE_CATEGORY))
               .thenReturn(mockPreparedStatement);
        
        ProductCategory category = createSampleProductCategory();
        category.setId(1);
        mockResultSetForProductCategory(category);

        Product product = createSampleProduct();
        PreparedStatement mockProductPreparedStatement = setupMockForProductsInCategory(product);

        categoryDAO.save(category);

        verifyMockPreparedStatementForCategory(category);
        verify(mockPreparedStatement, times(1)).setInt(3, category.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockProductPreparedStatement, times(1)).setInt(1, category.getId());
        verify(mockProductPreparedStatement, times(1)).executeQuery();
    }
    
    /**
     * Tests the retrieval of an {@link ProductCategory} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetById() throws SQLException {
        int categoryId = 1;
        ProductCategory category = createSampleProductCategory();
        category.setId(categoryId);
        mockResultSetForProductCategory(category);

        Product product = createSampleProduct();
        PreparedStatement mockProductPreparedStatement = setupMockForProductsInCategory(product);

        Optional<ProductCategory> result = categoryDAO.getById(categoryId);

        verify(mockPreparedStatement, times(1)).setInt(1, categoryId);
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockProductPreparedStatement, times(1)).setInt(1, categoryId);
        verify(mockProductPreparedStatement, times(1)).executeQuery();
        assertTrue(result.isPresent());
        assertEquals(categoryId, result.get().getId());
        assertEquals(category.getName(), result.get().getName());
        assertEquals(category.getType(), result.get().getType());
        assertEquals(1, result.get().getProducts().size());
        assertEquals(product.getId(), result.get().getProducts().get(0).getId());
        assertEquals(product.getName(), result.get().getProducts().get(0).getName());
    }
    
    /**
     * Tests the retrieval of all {@link ProductCategory} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAll() throws SQLException {
        when(mockConnection.prepareStatement(ProductCategorySQLQueries.GET_ALL_CATEGORIES))
               .thenReturn(mockPreparedStatement);

        int categoryId = 1;
        ProductCategory category = createSampleProductCategory();
        category.setId(categoryId);
        mockResultSetForProductCategory(category);

        Product product = createSampleProduct();
        setupMockForProductsInCategory(product);

        List<ProductCategory> result = categoryDAO.getAll();

        verify(mockPreparedStatement, times(1)).executeQuery();
        assertEquals(1, result.size());
        assertEquals(category.getId(), result.get(0).getId());
        assertEquals(category.getName(), result.get(0).getName());
        assertEquals(category.getType(), result.get(0).getType());
        assertEquals(1, result.get(0).getProducts().size());
        assertEquals(product.getId(), result.get(0).getProducts().get(0).getId());
        assertEquals(product.getName(), result.get(0).getProducts().get(0).getName());
    }
    
    /**
     * Tests the deletion of an {@link ProductCategory} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteProduct() throws SQLException {
    	 when(mockConnection.prepareStatement(ProductCategorySQLQueries.DELETE_ORDER_DETAIL_PRODUCTS_BY_CATEGORY_ID))
         		.thenReturn(mockPreparedStatement);
    	 when(mockConnection.prepareStatement(ProductCategorySQLQueries.DELETE_PRODUCTS_BY_CATEGORY_ID))
  				.thenReturn(mockPreparedStatement);
    	 when(mockConnection.prepareStatement(ProductCategorySQLQueries.DELETE_CATEGORY))
			.thenReturn(mockPreparedStatement);
    	 
    	 int productId = 1;
    	 categoryDAO.delete(productId);
    	 
    	 verify(mockPreparedStatement, times(3)).setInt(1, productId);
         verify(mockPreparedStatement, times(3)).executeUpdate();    	 
    }
    
    private void mockResultSetForProductCategory(ProductCategory category) throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("category_id")).thenReturn(category.getId());
        when(mockResultSet.getString("category_name")).thenReturn(category.getName());
        when(mockResultSet.getString("category_type")).thenReturn(category.getType());
    }

    private void mockResultSetForProductsInCategory(ResultSet mockProductResultSet, Product product) throws SQLException {
        when(mockProductResultSet.next()).thenReturn(true, false);
        when(mockProductResultSet.getInt("id")).thenReturn(product.getId());
        when(mockProductResultSet.getString("name")).thenReturn(product.getName());
        when(mockProductResultSet.getBigDecimal("price")).thenReturn(product.getPrice());
        when(mockProductResultSet.getInt("quantity")).thenReturn(product.getQuantity());
        when(mockProductResultSet.getBoolean("available")).thenReturn(product.isAvailable());
    }

    private PreparedStatement setupMockForProductsInCategory(Product product) throws SQLException {
        PreparedStatement mockProductPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockProductResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(ProductCategorySQLQueries.GET_PRODUCTS_BY_CATEGORY_ID))
                .thenReturn(mockProductPreparedStatement);
        when(mockProductPreparedStatement.executeQuery()).thenReturn(mockProductResultSet);
        mockResultSetForProductsInCategory(mockProductResultSet, product);
        return mockProductPreparedStatement;
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1);
        product.setName("Sample Product");
        product.setPrice(new BigDecimal("19.99"));
        product.setQuantity(100);
        product.setAvailable(true);
        return product;
    }

    private ProductCategory createSampleProductCategory() {
        ProductCategory category = new ProductCategory();
        category.setName("Category1");
        category.setType("Type1");
        return category;
    }
}


