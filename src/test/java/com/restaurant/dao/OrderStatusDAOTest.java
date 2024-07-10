package com.restaurant.dao;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restaurant.dao.entity.OrderStatusDAO;
import com.restaurant.entity.OrderStatus;

/**
 * Unit tests for the {@link OrderStatusDAO} class.
 */
public class OrderStatusDAOTest {
    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private OrderStatusDAO orderStatusDAO;

    /**
     * Sets up the test environment by initializing mocks and the {@link OrderStatusDAO} instance.
     */
    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        orderStatusDAO = new OrderStatusDAO(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    /**
     * Tests the retrieval of an {@link OrderStatus} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testGetById() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("status_name")).thenReturn("ACCEPTED");

        Optional<OrderStatus> result = orderStatusDAO.getById(1);
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.ACCEPTED, result.get());

        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).executeQuery();
    }
    
    /**
     * Tests the retrieval of all {@link OrderStatus} entities.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testGetAll() throws SQLException {
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getString("status_name")).thenReturn("ACCEPTED", "APPROVED", "CANCELLED");

        List<OrderStatus> result = orderStatusDAO.getAll();
        assertEquals(3, result.size());
        assertEquals(OrderStatus.ACCEPTED, result.get(0));
        assertEquals(OrderStatus.APPROVED, result.get(1));
        assertEquals(OrderStatus.CANCELLED, result.get(2));

        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the save of an {@link OrderStatus} entity.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testSave() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderStatus status = OrderStatus.ACCEPTED;
        OrderStatus result = orderStatusDAO.save(status);
        assertEquals(status, result);

        verify(preparedStatement, times(1)).setString(1, "ACCEPTED");
        verify(preparedStatement, times(1)).executeUpdate();
    }
    
    /**
     * Tests the deletion of an {@link OrderStatus} entity by its ID.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testDelete() throws SQLException {
        orderStatusDAO.delete(1);

        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }
}