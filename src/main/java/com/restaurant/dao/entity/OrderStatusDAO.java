package com.restaurant.dao.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.restaurant.dao.AbstractDao;
import com.restaurant.entity.OrderStatus;
import com.restaurant.queries.OrderStatusSQLQueries;

/**
 * Data Access Object (DAO) for the OrderStatus entity. This class provides
 * methods to perform CRUD operations and manage OrderStatus data in the database.
 */
public class OrderStatusDAO extends AbstractDao<OrderStatus, Integer>{		
	 /**
     * Constructor with connection parameter for testing.
     *
     * @param connection the database connection.
     */
	public OrderStatusDAO(Connection connection) {
		super(connection);
	}
	
	/**
     * Retrieves an OrderStatus entity by its ID.
     *
     * @param statusId the ID of the OrderStatus to retrieve.
     * @return an Optional containing the found OrderStatus, or an empty Optional if not found.
     * @throws SQLException if a database access error occurs.
     */
	@Override
	public Optional<OrderStatus> getById(Integer statusId) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderStatusSQLQueries.GET_STATUS_BY_ID)) {
			pstmt.setInt(1, statusId);
			try(ResultSet rs = pstmt.executeQuery()) {
				if(rs.next()) {
					OrderStatus orderStatus = mapResultSetToOrderStatus(rs);
					return Optional.of(orderStatus);
				}
			}
		}
		return Optional.empty();
	}

	/**
     * Maps a ResultSet to an OrderStatus entity.
     *
     * @param rs the ResultSet to map.
     * @return the mapped OrderStatus entity.
     * @throws SQLException if a database access error occurs.
     */
	private OrderStatus mapResultSetToOrderStatus(ResultSet rs) throws SQLException {
		return OrderStatus.valueOf(rs.getString("status_name"));
	}

	/**
     * Retrieves all OrderStatus entities from the database.
     *
     * @return a list of all OrderStatus entities.
     * @throws SQLException if a database access error occurs.
     */
	@Override
	public List<OrderStatus> getAll() throws SQLException {
		List<OrderStatus> orderStatus = new ArrayList<>();
		try(PreparedStatement pstmt = connection.prepareStatement(OrderStatusSQLQueries.GET_ALL_STATUS)) {
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					 orderStatus.add(mapResultSetToOrderStatus(rs));
				}
			}
		}		
		return orderStatus;
	}
	
	/**
	 * Saves an OrderStatus entity.
	 *
	 * @param status the OrderStatus entity to save.
	 * @return the saved OrderStatus entity.
	 * @throws SQLException if a database access error occurs or if creating the order status fails.
	 */
	@Override
	public OrderStatus save(OrderStatus status) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderStatusSQLQueries.INSERT_STATUS)) {
			pstmt.setString(1, status.name());
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows == 0) {
	            throw new SQLException("Creating order status failed, no rows affected.");
	        }
		}
		return status;
	}
	
	/**
     * Deletes an OrderStatus entity by its ID.
     *
     * @param statusId the ID of the OrderStatus to delete.
     * @throws SQLException if a database access error occurs.
     */
	@Override
	public void delete(Integer statusId) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderStatusSQLQueries.DELETE_STATUS)) {
			pstmt.setInt(1, statusId);
			pstmt.executeUpdate();
		}
	}

}