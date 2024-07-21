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
import com.restaurant.entity.Product;
import com.restaurant.entity.ProductCategory;
import com.restaurant.queries.ProductSQLQueries;

/**
 * Data Access Object (DAO) for the Product entity. This class provides
 * methods to perform CRUD operations and manage Product data in the database.
 */
public class ProductDAO extends AbstractDao<Product, Integer>{	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductDAO.class);
	
	public ProductDAO() {
		super();
	}
	
	public ProductDAO(Connection connection) {
		super(connection);
	}		
	
	@Override
	public Optional<Product> getById(Integer productId) throws SQLException {	
		try(PreparedStatement pstmt = connection.prepareStatement(
				ProductSQLQueries.GET_PRODUCT_BY_ID)){
			pstmt.setInt(1, productId);
			try(ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {			
					Product product = mapResultSetToProduct(rs);
					return Optional.of(product);			
				}	
			}
		}	
		return Optional.empty();
	}
	
	/**
     * Maps a ResultSet to a Product entity with Product Category.
     *
     * @param rs the ResultSet to map.
     * @return the mapped Product entity with Product Category.
     * @throws SQLException if a database access error occurs.
     */
	public Product mapResultSetToProduct(ResultSet rs) throws SQLException {	
		ProductCategory category = new ProductCategory();
		category.setId(rs.getInt("category_id"));
        category.setName(rs.getString("category_name"));
        category.setType(rs.getString("category_type"));
		return new Product(
		        rs.getInt("id"),
		        rs.getString("name"),
		        rs.getBigDecimal("price"),
		        rs.getInt("quantity"),
		        rs.getBoolean("available"),
		        category
		    );
	}	
	
	@Override
	public List<Product> getAll() throws SQLException {
		List<Product> products = new ArrayList<>();
		try(PreparedStatement pstmt = connection.prepareStatement(
				ProductSQLQueries.GET_ALL_PRODUCTS)) {
			ResultSet rs = pstmt.executeQuery();			
			while (rs.next()) {
				products.add(mapResultSetToProduct(rs));
			}	
		}
		return products;
	}	
	
	@Override
	public void delete(Integer productId) throws SQLException {
		try {
            connection.setAutoCommit(false);
            executeUpdate(ProductSQLQueries.DELETE_ORDER_DETAIL_PRODUCTS_BY_PRODUCT_ID, productId);            
            executeUpdate(ProductSQLQueries.DELETE_PRODUCT, productId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();  
            LOGGER.error("Error deleting product with ID: {}", productId, e);
        }
	}
	
	/**
     * Executes an update operation on the database.
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
	public Product save(Product product) throws SQLException {
		return getById(product.getId()).isPresent() ? updateProduct(product) : createProduct(product);
	}
	
	/**
     * Creates a new Product entity in the database.
     *
     * @param product the Product entity to create.
     * @return the created Product entity.
     * @throws SQLException if a database access error occurs.
     */
	private Product createProduct(Product product) throws SQLException{
		PreparedStatement pstmt = connection.prepareStatement(
				ProductSQLQueries.INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS);
		populatePreparedStatement(product, pstmt);
		pstmt.executeUpdate();		
		
		DAOUtils.setGeneratedKey(pstmt, product);		
		
		return product;
	}
	
	/**
     * Populates a PreparedStatement with Product entity data.
     *
     * @param product the Product entity.
     * @param pstmt the PreparedStatement to populate.
     * @throws SQLException if a database access error occurs.
     */
	private void populatePreparedStatement(Product product, PreparedStatement pstmt) throws SQLException {
		pstmt.setString(1, product.getName());
		pstmt.setBigDecimal(2, product.getPrice());
		pstmt.setInt(3, product.getQuantity());
		pstmt.setBoolean(4, product.isAvailable());
		pstmt.setInt(5, product.getProductCategory().getId());
	}
	
	/**
     * Updates an existing Product entity in the database.
     *
     * @param product the Product entity to update.
     * @return the updated Product entity.
     * @throws SQLException if a database access error occurs.
     */
	private Product updateProduct(Product product) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement(
				ProductSQLQueries.UPDATE_PRODUCT);
		populatePreparedStatement(product, pstmt);
		pstmt.setInt(6, product.getId());	
		pstmt.executeUpdate();
		
		return product;
	}
}
