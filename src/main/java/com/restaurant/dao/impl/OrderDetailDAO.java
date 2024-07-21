package com.restaurant.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restaurant.dao.AbstractDao;
import com.restaurant.dao.DAOUtils;
import com.restaurant.entity.OrderDetail;
import com.restaurant.entity.OrderStatus;
import com.restaurant.entity.Product;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.OrderDetailSQLQueries;

/**
 * Data Access Object (DAO) for the OrderDetail entity. This class provides
 * methods to perform CRUD operations and manage OrderDetail data in the database.
 */
public class OrderDetailDAO extends AbstractDao<OrderDetail, Integer>{
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderDetailDAO.class);	
	
	public OrderDetailDAO() {
		super();
	}	
	
	public OrderDetailDAO(Connection connection) {
		super(connection);		
	}

	@Override
	public Optional<OrderDetail> getById(Integer detailId) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(OrderDetailSQLQueries.GET_DETAIL_BY_ID)) {
			pstmt.setInt(1, detailId);
			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					OrderDetail orderDetail = mapResultSetToOrderDetail(rs);
					
					return Optional.of(orderDetail);			
				}		
			}
		}
		return Optional.empty();
	}
	
	/**
     * Maps a ResultSet to an OrderDetail entity.
     *
     * @param rs the ResultSet to map.
     * @return the mapped OrderDetail entity.
     * @throws SQLException if a database access error occurs.
     */
	private OrderDetail mapResultSetToOrderDetail(ResultSet rs) throws SQLException {
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setId(rs.getInt("id"));		
		orderDetail.setTotalAmount(rs.getBigDecimal("total_amount"));
		
		OrderStatus orderStatus = OrderStatus.valueOf(rs.getString("status_name"));	   
	    orderDetail.setOrderStatus(orderStatus);
	    
	    orderDetail.setProducts(getProductsByOrderDetailId(rs.getInt("id")));
	    
		return orderDetail;
	}
	
	/**
     * Retrieves all Products entities from the database where the OrderDetail entity by its ID.
     *
     * @param orderDetailId the ID of the OrderDetail.
     * @return a list of all Products entities.
     * @throws SQLException if a database access error occurs.
     */
	public List<Product> getProductsByOrderDetailId(Integer orderDetailId) throws SQLException {
	    List<Product> products = new ArrayList<>();
	    try(PreparedStatement pstmt = connection.prepareStatement(
	    		OrderDetailSQLQueries.GET_PRODUCTS_BY_ORDER_DETAIL_ID)) {
	    	pstmt.setInt(1, orderDetailId);
	    	try(ResultSet rs = pstmt.executeQuery()) {
	    		while (rs.next()) {
	    	        products.add(mapResultSetToProduct(rs));
	    	    }
	    	}
	    }
	    return products;
	}
	
	/**
	 * Maps a ResultSet to a Product entity.
	 *
	 * @param rs the ResultSet to map.
	 * @return the mapped Product entity.
	 * @throws SQLException if a database access error occurs.
	 */
	private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
	    ProductCategory category = new ProductCategory();
	    category.setId(rs.getInt("category_id"));
	    category.setName(rs.getString("category_name"));
	    category.setType(rs.getString("category_type"));
	    
	    Product product = new Product();
	    product.setId(rs.getInt("id"));
	    product.setName(rs.getString("name"));
	    product.setPrice(rs.getBigDecimal("price"));
	    product.setQuantity(rs.getInt("quantity"));
	    product.setAvailable(rs.getBoolean("available"));
	    product.setProductCategory(category);
	    
	    return product;
	}
	
	@Override
	public List<OrderDetail> getAll() throws SQLException {
		List<OrderDetail> orderDetails = new ArrayList<>();
		try(PreparedStatement pstmt = connection.prepareStatement(OrderDetailSQLQueries.GET_ALL_DETAILS)) {
			try(ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					orderDetails.add(mapResultSetToOrderDetail(rs));			
				}
			}
		}
		return orderDetails;
	}
	
	@Override
	public void delete(Integer detailId) throws SQLException {
		try {
            connection.setAutoCommit(false);
            executeUpdate(OrderDetailSQLQueries.DELETE_APPROVAL_BY_ORDER_ID, detailId);
            executeUpdate(OrderDetailSQLQueries.DELETE_ORDER_DETAIL_PRODUCTS_BY_ORDER_ID, detailId);            
            executeUpdate(OrderDetailSQLQueries.DELETE_DETAIL, detailId);
            connection.commit();
        } catch (SQLException e) {
        	connection.rollback();  
        	LOGGER.error("Error deleting order with ID: {}", detailId, e);
        }		
	}
	
	/**
	 * Executes a SQL update query.
	 *
	 * @param query the SQL query to execute.
	 * @param id the ID to set in the query.
	 * @throws SQLException if a database access error occurs.
	 */
	private void executeUpdate(String query, Integer id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }        
    }	
	
	@Override
	public OrderDetail save(OrderDetail detail) throws SQLException {		
		return getById(detail.getId()).isPresent() ? updateDetail(detail) : createDetail(detail);
	}
	
	/**
	 * Creates a new OrderDetail entity in the database.
	 *
	 * @param detail the OrderDetail entity to create.
	 * @return the created OrderDetail entity.
	 * @throws SQLException if a database access error occurs.
	 */
	public OrderDetail createDetail(OrderDetail detail) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement(OrderDetailSQLQueries.INSERT_DETAIL, 
				Statement.RETURN_GENERATED_KEYS);
		populatePreparedStatement(detail, pstmt);
		pstmt.executeUpdate();
		
		DAOUtils.setGeneratedKey(pstmt, detail);
		
		return detail;
	}

	/**
	 * Populates a PreparedStatement with the OrderDetail entity data.
	 *
	 * @param detail the OrderDetail entity.
	 * @param pstmt  the PreparedStatement to populate.
	 * @throws SQLException if a database access error occurs.
	 */
	private void populatePreparedStatement(OrderDetail detail, PreparedStatement pstmt) throws SQLException {
		pstmt.setInt(1, detail.getOrderStatus().getId());
		pstmt.setBigDecimal(2, detail.getTotalAmount());
	}
	
	/**
	 * Updates an existing OrderDetail entity in the database.
	 *
	 * @param detail the OrderDetail entity to update.
	 * @return the updated OrderDetail entity.
	 * @throws SQLException if a database access error occurs.
	 */	
	public OrderDetail updateDetail(OrderDetail detail) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement(OrderDetailSQLQueries.UPDATE_DETAIL);
		populatePreparedStatement(detail, pstmt);
		pstmt.setInt(3, detail.getId());
		pstmt.executeUpdate();
		return detail;
	}
}
