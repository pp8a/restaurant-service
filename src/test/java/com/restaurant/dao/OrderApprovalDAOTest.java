package com.restaurant.dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.restaurant.dao.entity.OrderApprovalDAO;
import com.restaurant.entity.OrderApproval;
import com.restaurant.entity.OrderDetail;
import com.restaurant.queries.OrderApprovalSQLQueries;

/**
 * Unit tests for the {@link OrderApprovalDAO} class.
 */
class OrderApprovalDAOTest {
	@Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @Mock
    ResultSet mockResultSet;

    private OrderApprovalDAO orderApprovalDAO;

    /**
     * Sets up the test environment by initializing mocks and the {@link OrderApprovalDAO} instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orderApprovalDAO = new OrderApprovalDAO(mockConnection);

        try {
            setupMockPreparedStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupMockPreparedStatement() throws SQLException {
        when(mockConnection.prepareStatement(OrderApprovalSQLQueries.GET_APPROVAL_BY_ID))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }
    
    /**
     * Tests the creation of an {@link OrderApproval} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testCreateOrderApproval() throws SQLException {
    	
        when(mockConnection.prepareStatement(OrderApprovalSQLQueries.INSERT_APPROVAL, Statement.RETURN_GENERATED_KEYS))
            .thenReturn(mockPreparedStatement);

        OrderApproval orderApproval = createSampleOrderApproval();

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // OrderApproval not found

        orderApprovalDAO.save(orderApproval);

        verify(mockPreparedStatement, times(1)).setInt(1, orderApproval.getOrderDetail().getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
    
    /**
     * Tests the update of an {@link OrderApproval} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testUpdateOrderApproval() throws SQLException {
        PreparedStatement getByIdPreparedStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(OrderApprovalSQLQueries.GET_APPROVAL_BY_ID))
            .thenReturn(getByIdPreparedStatement);
        when(getByIdPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockConnection.prepareStatement(OrderApprovalSQLQueries.UPDATE_APPROVAL))
            .thenReturn(mockPreparedStatement);

        OrderApproval orderApproval = createSampleOrderApproval();
        orderApproval.setId(1);

        mockResultSetForOrderApproval(orderApproval);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // OrderApproval found

        orderApprovalDAO.save(orderApproval);

        verify(mockPreparedStatement, times(1)).setInt(1, orderApproval.getOrderDetail().getId());
        verify(mockPreparedStatement, times(1)).setInt(2, orderApproval.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

   /**
     * Tests the retrieval of an {@link OrderApproval} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetById() throws SQLException {
        OrderApproval orderApproval = createSampleOrderApproval();
        orderApproval.setId(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // OrderApproval found

        mockResultSetForOrderApproval(orderApproval);

        Optional<OrderApproval> result = orderApprovalDAO.getById(orderApproval.getId());

        verify(mockPreparedStatement, times(1)).setInt(1, orderApproval.getId());
        verify(mockPreparedStatement, times(1)).executeQuery();
        assertTrue(result.isPresent());
        assertEquals(orderApproval.getId(), result.get().getId());
        assertEquals(orderApproval.getOrderDetail().getId(), result.get().getOrderDetail().getId());
    }
    
    /**
     * Tests the retrieval of all {@link OrderApproval} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testGetAll() throws SQLException {
        when(mockConnection.prepareStatement(OrderApprovalSQLQueries.GET_ALL_APPROVALS))
            .thenReturn(mockPreparedStatement);

        OrderApproval orderApproval = createSampleOrderApproval();
        orderApproval.setId(1);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One OrderApproval found

        mockResultSetForOrderApproval(orderApproval);

        List<OrderApproval> result = orderApprovalDAO.getAll();

        verify(mockPreparedStatement, times(1)).executeQuery();
        assertEquals(1, result.size());
        assertEquals(orderApproval.getId(), result.get(0).getId());
        assertEquals(orderApproval.getOrderDetail().getId(), result.get(0).getOrderDetail().getId());
    }

    /**
     * Tests the deletion of an {@link OrderApproval} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    void testDeleteOrderApproval() throws SQLException {
        when(mockConnection.prepareStatement(OrderApprovalSQLQueries.DELETE_APPROVAL))
            .thenReturn(mockPreparedStatement);

        int orderApprovalId = 1;

        orderApprovalDAO.delete(orderApprovalId);

        verify(mockPreparedStatement, times(1)).setInt(1, orderApprovalId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    private OrderApproval createSampleOrderApproval() {
        OrderApproval orderApproval = new OrderApproval();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1);
        orderApproval.setOrderDetail(orderDetail);
        return orderApproval;
    }

    private void mockResultSetForOrderApproval(OrderApproval orderApproval) throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(orderApproval.getId());
        when(mockResultSet.getInt("order_detail_id")).thenReturn(orderApproval.getOrderDetail().getId());
    }
    
}
