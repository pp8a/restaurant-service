package com.restaurant.dao;

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

import com.restaurant.dao.entity.ProductDAO;
import com.restaurant.entity.Product;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.ProductSQLQueries;

/**
 * Unit tests for the {@link ProductDAO} class.
 */
class ProductDAOTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDAO.class);
    
    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @Mock
    ResultSet mockResultSet;

    private ProductDAO productDAO;
    
    /**
     * Sets up the test environment by initializing mocks and the {@link ProductDAO} instance.
     */
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);        
        productDAO = new ProductDAO(mockConnection);
        
        try {
            setupMockPreparedStatement();
        } catch (SQLException e) {
            LOGGER.error("Error setting up mock prepared statement", e);
        }
    }

    private void setupMockPreparedStatement() throws SQLException {        
        when(mockConnection.prepareStatement(ProductSQLQueries.GET_PRODUCT_BY_ID))
            .thenReturn(mockPreparedStatement);
    }
    
    /**
     * Tests the creation of an {@link Product} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateProduct() throws SQLException {
        when(mockConnection.prepareStatement(ProductSQLQueries.INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS))
            .thenReturn(mockPreparedStatement);

        Product product = createSampleProduct();

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Дополнительная настройка поведения ResultSet и PreparedStatement
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Product not found

        productDAO.save(product);

        verifyMockPreparedStatementForProduct(product);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
    
    /**
     * Tests the update of an {@link Product} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateProduct() throws SQLException {
        when(mockConnection.prepareStatement(ProductSQLQueries.UPDATE_PRODUCT))
            .thenReturn(mockPreparedStatement);

        Product product = createSampleProduct();
        product.setId(1);

        // Дополнительная настройка поведения ResultSet и PreparedStatement
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Product found

        productDAO.save(product);

        verifyMockPreparedStatementForProduct(product);
        verify(mockPreparedStatement, times(1)).setInt(6, product.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
    
    /**
     * Tests the retrieval of an {@link Product} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetById() throws SQLException {
        Product product = createSampleProduct();
        product.setId(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Product found

        mockResultSetForProduct(product);

        Optional<Product> result = productDAO.getById(product.getId());

        verify(mockPreparedStatement, times(1)).setInt(1, product.getId());
        verify(mockPreparedStatement, times(1)).executeQuery();
        assertTrue(result.isPresent());
        assertEquals(product.getId(), result.get().getId());
        assertEquals(product.getName(), result.get().getName());
    }
    
    /**
     * Tests the retrieval of all {@link Product} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAll() throws SQLException {
        when(mockConnection.prepareStatement(ProductSQLQueries.GET_ALL_PRODUCTS))
            .thenReturn(mockPreparedStatement);

        Product product = createSampleProduct();
        product.setId(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One product found

        mockResultSetForProduct(product);

        List<Product> result = productDAO.getAll();

        verify(mockPreparedStatement, times(1)).executeQuery();
        assertEquals(1, result.size());
        assertEquals(product.getId(), result.get(0).getId());
        assertEquals(product.getName(), result.get(0).getName());
    }
    
    /**
     * Tests the deletion of an {@link Product} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteProduct() throws SQLException {
        when(mockConnection.prepareStatement(ProductSQLQueries.DELETE_ORDER_DETAIL_PRODUCTS_BY_PRODUCT_ID))
            .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(ProductSQLQueries.DELETE_PRODUCT))
            .thenReturn(mockPreparedStatement);
        
        int productId = 1;        
        productDAO.delete(productId);
        
        verify(mockPreparedStatement, times(2)).setInt(1, productId);
        verify(mockPreparedStatement, times(2)).executeUpdate();
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setName("Sample Product");
        product.setPrice(new BigDecimal("19.99"));
        product.setQuantity(100);
        product.setAvailable(true);

        ProductCategory category = new ProductCategory();
        category.setId(1);        
        product.setProductCategory(category);
        return product;
    }

    private void verifyMockPreparedStatementForProduct(Product product) throws SQLException {
        verify(mockPreparedStatement, times(1)).setString(1, product.getName());
        verify(mockPreparedStatement, times(1)).setBigDecimal(2, product.getPrice());
        verify(mockPreparedStatement, times(1)).setInt(3, product.getQuantity());
        verify(mockPreparedStatement, times(1)).setBoolean(4, product.isAvailable());
        verify(mockPreparedStatement, times(1)).setInt(5, product.getProductCategory().getId());
    }

    private void mockResultSetForProduct(Product product) throws SQLException {
        when(mockResultSet.getInt("id")).thenReturn(product.getId());
        when(mockResultSet.getString("name")).thenReturn(product.getName());
        when(mockResultSet.getBigDecimal("price")).thenReturn(product.getPrice());
        when(mockResultSet.getInt("quantity")).thenReturn(product.getQuantity());
        when(mockResultSet.getBoolean("available")).thenReturn(product.isAvailable());
        when(mockResultSet.getInt("category_id")).thenReturn(product.getProductCategory().getId());
    }
}