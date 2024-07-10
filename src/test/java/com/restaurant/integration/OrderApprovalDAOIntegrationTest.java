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

import com.restaurant.dao.entity.OrderApprovalDAO;
import com.restaurant.entity.OrderApproval;
import com.restaurant.entity.OrderDetail;
import com.restaurant.queries.OrderApprovalSQLQueries;

/**
 * Integration tests for the {@link OrderApprovalDAO} class.
 * Uses Testcontainers for PostgreSQL to ensure tests run in an isolated environment.
 */
@Testcontainers
class OrderApprovalDAOIntegrationTest {
    @Container
    public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private Connection connection;
    private OrderApprovalDAO orderApprovalDAO;
    
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
        
        TestDataInitializer.insertTestOrderStatus(connection);
        TestDataInitializer.insertTestOrderDetails(connection);
        TestDataInitializer.insertTestOrderApproval(connection);        
        orderApprovalDAO = new OrderApprovalDAO(connection);
    }
    
    /**
     * Tests the creation of an OrderApproval entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateOrderApproval() throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setId(1); // Set an existing OrderDetail ID

        OrderApproval approval = new OrderApproval();
        approval.setOrderDetail(detail);

        orderApprovalDAO.save(approval);

        int generatedId = approval.getId();
        String selectQuery = OrderApprovalSQLQueries.GET_APPROVAL_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, generatedId);
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt("order_detail_id"));
    }
    
    /**
     * Tests the update of an OrderApproval entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateOrderApproval() throws SQLException {
        OrderApproval approval = orderApprovalDAO.getById(1).orElseThrow(SQLException::new);
        approval.getOrderDetail().setId(2); // Update to a different OrderDetail ID

        orderApprovalDAO.save(approval);

        String selectQuery = OrderApprovalSQLQueries.GET_APPROVAL_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, approval.getId());
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(resultSet.next());
        assertEquals(2, resultSet.getInt("order_detail_id"));
    }
    
    /**
     * Tests retrieving an OrderApproval entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetOrderApprovalById() throws SQLException {
        OrderApproval approval = orderApprovalDAO.getById(1).orElseThrow(SQLException::new);

        assertEquals(1, approval.getId());
        assertEquals(2, approval.getOrderDetail().getId());
    }
    
    /**
     * Tests retrieving all OrderApproval entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAllOrderApprovals() throws SQLException {
        List<OrderApproval> approvals = orderApprovalDAO.getAll();

        assertEquals(1, approvals.size());
        OrderApproval approval = approvals.get(0);
        assertEquals(2, approval.getOrderDetail().getId());
    }
    
    /**
     * Tests deleting an OrderApproval entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteOrderApproval() throws SQLException {
        orderApprovalDAO.delete(1);

        String selectQuery = OrderApprovalSQLQueries.GET_APPROVAL_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(selectQuery);
        stmt.setInt(1, 1);
        ResultSet resultSet = stmt.executeQuery();

        assertTrue(!resultSet.next());
    }
}
