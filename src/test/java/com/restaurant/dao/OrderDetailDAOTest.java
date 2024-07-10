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
import java.util.ArrayList;
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

import com.restaurant.dao.entity.OrderDetailDAO;
import com.restaurant.entity.OrderDetail;
import com.restaurant.entity.OrderStatus;
import com.restaurant.entity.Product;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.OrderDetailSQLQueries;

/**
 * Unit tests for the {@link OrderDetailDAO} class.
 */
class OrderDetailDAOTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDetailDAO.class);
    
    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @Mock
    ResultSet mockResultSet;

    private OrderDetailDAO orderDetailDAO;
    
    /**
     * Sets up the test environment by initializing mocks and the {@link OrderDetailDAO} instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);        
        orderDetailDAO = new OrderDetailDAO(mockConnection);
        
        try {
            setupMockPreparedStatement();
        } catch (SQLException e) {
            LOGGER.error("Error setting up mock prepared statement", e);
        }
    }

    private void setupMockPreparedStatement() throws SQLException {
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.GET_DETAIL_BY_ID))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }
    
    /**
     * Tests the creation of an {@link OrderDetail} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateOrderDetail() throws SQLException {
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.INSERT_DETAIL, Statement.RETURN_GENERATED_KEYS))
            .thenReturn(mockPreparedStatement);

        OrderDetail orderDetail = createSampleOrderDetail();

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Дополнительная настройка поведения ResultSet и PreparedStatement
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // OrderDetail not found

        orderDetailDAO.save(orderDetail);

        verifyMockPreparedStatementForOrderDetail(orderDetail);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
    
    /**
     * Tests the update of an {@link OrderDetail} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateOrderDetail() throws SQLException {
    	PreparedStatement getByIdPreparedStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.GET_DETAIL_BY_ID))
            .thenReturn(getByIdPreparedStatement);
        when(getByIdPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    	
    	
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.UPDATE_DETAIL))
            .thenReturn(mockPreparedStatement);

        OrderDetail orderDetail = createSampleOrderDetail();
        orderDetail.setId(1);
        
        mockResultSetForOrderDetail(orderDetail);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // OrderDetail found       
        
        Product product = createSampleProduct();
        PreparedStatement mockProductPreparedStatement = setupMockForProductsInOrderDetail(product);

        orderDetailDAO.save(orderDetail);

        verify(mockPreparedStatement, times(1)).setInt(1, orderDetail.getOrderStatus().getId());
        verify(mockPreparedStatement, times(1)).setBigDecimal(2, orderDetail.getTotalAmount()); 
        verify(mockPreparedStatement, times(1)).setInt(3, orderDetail.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
        
        verify(mockProductPreparedStatement, times(1)).setInt(1, orderDetail.getId());
        verify(mockProductPreparedStatement, times(1)).executeQuery();
    }
    
    /**
     * Tests the retrieval of an {@link OrderDetail} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetById() throws SQLException {
        OrderDetail orderDetail = createSampleOrderDetail();
        orderDetail.setId(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // OrderDetail found

        mockResultSetForOrderDetail(orderDetail);
        
        Product product = createSampleProduct();
        setupMockForProductsInOrderDetail(product);

        Optional<OrderDetail> result = orderDetailDAO.getById(orderDetail.getId());

        verify(mockPreparedStatement, times(1)).setInt(1, orderDetail.getId());
        verify(mockPreparedStatement, times(1)).executeQuery();
        assertTrue(result.isPresent());
        assertEquals(orderDetail.getId(), result.get().getId());
        assertEquals(orderDetail.getTotalAmount(), result.get().getTotalAmount());
    }
    
    /**
     * Tests the retrieval of all {@link OrderDetail} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAll() throws SQLException {
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.GET_ALL_DETAILS))
            .thenReturn(mockPreparedStatement);

        OrderDetail orderDetail = createSampleOrderDetail();
        orderDetail.setId(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One OrderDetail found

        mockResultSetForOrderDetail(orderDetail);
        
        Product product = createSampleProduct();
        setupMockForProductsInOrderDetail(product);

        List<OrderDetail> result = orderDetailDAO.getAll();

        verify(mockPreparedStatement, times(1)).executeQuery();
        assertEquals(1, result.size());
        assertEquals(orderDetail.getId(), result.get(0).getId());
        assertEquals(orderDetail.getTotalAmount(), result.get(0).getTotalAmount());
    }
    
    /**
     * Tests the deletion of an {@link OrderDetail} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteOrderDetail() throws SQLException {
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.DELETE_ORDER_DETAIL_PRODUCTS_BY_ORDER_ID))
            .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.DELETE_APPROVAL_BY_ORDER_ID))
            .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.DELETE_DETAIL))
            .thenReturn(mockPreparedStatement);
        
        int orderDetailId = 1;
        
        orderDetailDAO.delete(orderDetailId);
        
        verify(mockPreparedStatement, times(3)).setInt(1, orderDetailId);
        verify(mockPreparedStatement, times(3)).executeUpdate();
    }

    private OrderDetail createSampleOrderDetail() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setTotalAmount(new BigDecimal("99.99"));

        OrderStatus orderStatus = OrderStatus.ACCEPTED;	   
	    orderDetail.setOrderStatus(orderStatus);

        Product product = createSampleProduct();
        List<Product> products = new ArrayList<>();
        products.add(product);
        orderDetail.setProducts(products);

        return orderDetail;
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1);
        product.setName("Sample Product");
        product.setPrice(new BigDecimal("19.99"));
        product.setQuantity(100);
        product.setAvailable(true);

        ProductCategory category = new ProductCategory();
        category.setId(1);
        product.setProductCategory(category);
        return product;
    }

    private void verifyMockPreparedStatementForOrderDetail(OrderDetail orderDetail) throws SQLException {
        verify(mockPreparedStatement, times(1)).setInt(1, orderDetail.getOrderStatus().getId());
        verify(mockPreparedStatement, times(1)).setBigDecimal(2, orderDetail.getTotalAmount());          
    }

    private void mockResultSetForOrderDetail(OrderDetail orderDetail) throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(orderDetail.getId());
        when(mockResultSet.getBigDecimal("total_amount")).thenReturn(orderDetail.getTotalAmount());
        when(mockResultSet.getString("status_name")).thenReturn(orderDetail.getOrderStatus().name());
    }

    private PreparedStatement setupMockForProductsInOrderDetail(Product product) throws SQLException {
        PreparedStatement mockProductPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockProductResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(OrderDetailSQLQueries.GET_PRODUCTS_BY_ORDER_DETAIL_ID))
            .thenReturn(mockProductPreparedStatement);
        when(mockProductPreparedStatement.executeQuery()).thenReturn(mockProductResultSet);
        mockResultSetForProduct(mockProductResultSet, product);
        return mockProductPreparedStatement;
    }

    private void mockResultSetForProduct(ResultSet mockProductResultSet, Product product) throws SQLException {
        when(mockProductResultSet.next()).thenReturn(true, false);
        when(mockProductResultSet.getInt("id")).thenReturn(product.getId());
        when(mockProductResultSet.getString("name")).thenReturn(product.getName());
        when(mockProductResultSet.getBigDecimal("price")).thenReturn(product.getPrice());
        when(mockProductResultSet.getInt("quantity")).thenReturn(product.getQuantity());
        when(mockProductResultSet.getBoolean("available")).thenReturn(product.isAvailable());
        when(mockProductResultSet.getInt("category_id")).thenReturn(product.getProductCategory().getId());
        when(mockProductResultSet.getString("category_name")).thenReturn(product.getProductCategory().getName());
        when(mockProductResultSet.getString("category_type")).thenReturn(product.getProductCategory().getType());
    }
}
