package com.restaurant.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.restaurant.dao.AbstractDao;
import com.restaurant.dao.DAOUtils;
import com.restaurant.entity.OrderApproval;
import com.restaurant.entity.OrderDetail;
import com.restaurant.queries.OrderApprovalSQLQueries;

/**
 * Data Access Object (DAO) for the OrderApproval entity. This class provides
 * methods to perform CRUD operations and manage OrderApproval data in the database.
 */
public class OrderApprovalDAO extends AbstractDao<OrderApproval, Integer>{
	
	public OrderApprovalDAO(Connection connection) {
		super(connection);
	}	
	
	@Override
	public Optional<OrderApproval> getById(Integer approvalId) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderApprovalSQLQueries.GET_APPROVAL_BY_ID)) {
			pstmt.setInt(1, approvalId);
			try(ResultSet rs = pstmt.executeQuery()) {
				if(rs.next()) {
					OrderApproval approval = mapResultSetToApproval(rs);
					return Optional.of(approval);
				}
			}
		}		
		return Optional.empty();
	}
	
	/**
     * Maps a ResultSet to an OrderApproval entity.
     *
     * @param rs the ResultSet to map.
     * @return the mapped OrderApproval entity.
     * @throws SQLException if a database access error occurs.
     */
	private OrderApproval mapResultSetToApproval(ResultSet rs) throws SQLException {
		OrderApproval approval = new OrderApproval();
		approval.setId(rs.getInt("id"));
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setId(rs.getInt("order_detail_id"));
		approval.setOrderDetail(orderDetail);
		return approval;
	}	
	
	@Override
	public List<OrderApproval> getAll() throws SQLException {
		List<OrderApproval> approvals = new ArrayList<>();
		try(PreparedStatement pstmt = connection.prepareStatement(OrderApprovalSQLQueries.GET_ALL_APPROVALS)) {
			try(ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					approvals.add(mapResultSetToApproval(rs));
				}
			}
		}		
		return approvals;
	}
	
	@Override
	public OrderApproval save(OrderApproval approval) throws SQLException {		
		return getById(approval.getId()).isPresent() ? updateApproval(approval) : createApproval(approval);
	}
	
	/**
     * Creates a new OrderApproval entity in the database.
     *
     * @param approval the OrderApproval entity to create.
     * @return the created OrderApproval entity.
     * @throws SQLException if a database access error occurs.
     */
	public OrderApproval createApproval(OrderApproval approval) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderApprovalSQLQueries.INSERT_APPROVAL, 
				Statement.RETURN_GENERATED_KEYS)){
			pstmt.setInt(1, approval.getOrderDetail().getId());
			pstmt.executeUpdate();
			
			DAOUtils.setGeneratedKey(pstmt, approval);			
		}
		return approval;
	}
	
	/**
     * Updates an existing OrderApproval entity in the database.
     *
     * @param approval the OrderApproval entity to update.
     * @return the updated OrderApproval entity.
     * @throws SQLException if a database access error occurs.
     */
	public OrderApproval updateApproval(OrderApproval approval) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderApprovalSQLQueries.UPDATE_APPROVAL)) {
			pstmt.setInt(1, approval.getOrderDetail().getId());
			pstmt.setInt(2, approval.getId());
			pstmt.executeUpdate();
		}
		return approval;
	}
	
	@Override
	public void delete(Integer approvalId) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderApprovalSQLQueries.DELETE_APPROVAL)) {
			pstmt.setInt(1, approvalId);
			pstmt.executeUpdate();
		}
	}
}
