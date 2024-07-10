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

import com.restaurant.dao.entity.OrderDetailDAO;
import com.restaurant.entity.OrderDetail;
import com.restaurant.entity.OrderStatus;
import com.restaurant.queries.OrderDetailSQLQueries;

/**
 * Integration tests for the {@link OrderDetailDAO} class.
 * Uses Testcontainers for PostgreSQL to ensure tests run in an isolated environment.
 */
@Testcontainers
class OrderDetailDAOIntegrationTest {
	@Container
    public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private Connection connection;
    private OrderDetailDAO orderDetailDAO;
    
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
        TestDataInitializer.createOrderApprovalTable(connection);
        TestDataInitializer.createOrderDetailProductsTable(connection);
        
        TestDataInitializer.insertTestProductCategory(connection);
        TestDataInitializer.insertTestProduct(connection);
        TestDataInitializer.insertTestOrderStatus(connection);
        TestDataInitializer.insertTestOrderDetails(connection);
        
        TestDataInitializer.insertTestOrderDetailProduct(connection);
        
        orderDetailDAO = new OrderDetailDAO(connection);
    }
    
    /**
     * Tests the creation of an {@link OrderDetail} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateOrderDetail() throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setOrderStatus(OrderStatus.APPROVED);
        detail.setTotalAmount(BigDecimal.valueOf(100.00));

        orderDetailDAO.save(detail);

        int generatedId = detail.getId();
        String selectQuery = OrderDetailSQLQueries.GET_DETAIL_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, generatedId);
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(resultSet.next());
        assertEquals("APPROVED", resultSet.getString("status_name"));
        assertEquals(new BigDecimal("100.00"), resultSet.getBigDecimal("total_amount"));
    }
    
    /**
     * Tests the update of an {@link OrderDetail} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateOrderDetail() throws SQLException {       
        OrderDetail detail = orderDetailDAO.getById(1).orElseThrow(SQLException::new);
        detail.setOrderStatus(OrderStatus.PAID);
        detail.setTotalAmount(new BigDecimal("150.00"));

        orderDetailDAO.save(detail);

        String selectQuery = OrderDetailSQLQueries.GET_DETAIL_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, detail.getId());
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(resultSet.next());
        assertEquals("PAID", resultSet.getString("status_name"));
        assertEquals(new BigDecimal("150.00"), resultSet.getBigDecimal("total_amount"));
    }
    
    /**
     * Tests retrieving an {@link OrderDetail} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetOrderDetailById() throws SQLException {
        OrderDetail detail = orderDetailDAO.getById(1).orElseThrow(SQLException::new);

        assertEquals(1, detail.getId());
        assertEquals("ACCEPTED", detail.getOrderStatus().name());
        assertEquals(new BigDecimal("25.97"), detail.getTotalAmount());
    }
    
    /**
     * Tests retrieving all {@link OrderDetail} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAllOrderDetails() throws SQLException {
        List<OrderDetail> details = orderDetailDAO.getAll();

        assertEquals(3, details.size());
        OrderDetail detail = details.get(0);
        assertEquals("ACCEPTED", detail.getOrderStatus().name());
        assertEquals(new BigDecimal("25.97"), detail.getTotalAmount());
    }
    
    /**
     * Tests deleting an {@link OrderDetail} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteOrderDetail() throws SQLException { 
        orderDetailDAO.delete(1);

        String selectQuery = OrderDetailSQLQueries.GET_DETAIL_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, 1);
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(!resultSet.next());
    }
}
