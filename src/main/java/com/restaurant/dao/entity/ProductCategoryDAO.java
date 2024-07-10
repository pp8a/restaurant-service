package com.restaurant.dao.entity;

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
import com.restaurant.queries.ProductCategorySQLQueries;

/**
 * Data Access Object (DAO) for the ProductCategory entity. This class provides
 * methods to perform CRUD operations and manage ProductCategory data in the database.
 */
public class ProductCategoryDAO extends AbstractDao<ProductCategory, Integer>{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategoryDAO.class);	
	/**
	 * Default constructor.
	 */
	public ProductCategoryDAO() {
		super();
	}
	/**
     * Constructor with connection parameter for testing.
     *
     * @param connection the database connection.
     */
	public ProductCategoryDAO(Connection connection) {
		super(connection);		
	}
	
	/**
     * Retrieves a ProductCategory entity by its ID.
     *
     * @param categoryId the ID of the ProductCategory to retrieve.
     * @return an Optional containing the found ProductCategory, or an empty Optional if not found.
     * @throws SQLException if a database access error occurs.
     */
	@Override
	public Optional<ProductCategory> getById(Integer categoryId) throws SQLException {
		try(PreparedStatement pstmt = connection.prepareStatement(ProductCategorySQLQueries.GET_CATEGORY_BY_ID)) {			            
            pstmt.setInt(1, categoryId);
            try(ResultSet rs = pstmt.executeQuery()) {
            	if(rs.next()) {
                    ProductCategory productCategory = mapResultSetToCategory(rs);
                    return Optional.of(productCategory);
                }
            }
            
        } catch (SQLException e) {
        	LOGGER.error("Error retrieving products by category ID: {}", categoryId, e);             
        }
        return Optional.empty();
	}
	
	/**
     * Maps a ResultSet to a ProductCategory entity and Product entity with ID of ProductCategory.
     *
     * @param rs the ResultSet to map.
     * @return the mapped ProductCategory entity.
     * @throws SQLException if a database access error occurs.
     */
	private ProductCategory mapResultSetToCategory(ResultSet rs) throws SQLException {	
		return new ProductCategory(
				rs.getInt("category_id"),
		        rs.getString("category_name"),
		        rs.getString("category_type"),
				getProductsByCategoryId(rs.getInt("category_id"))
				);
	}
	
	 /**
     * Retrieves a list of Product entities associated with a given ProductCategory ID.
     *
     * @param categoryId the ID of the ProductCategory.
     * @return a list of associated Product entities.
     * @throws SQLException if a database access error occurs.
     */
	public List<Product> getProductsByCategoryId(Integer categoryId) throws SQLException {
        List<Product> products = new ArrayList<>(); 
        try(PreparedStatement pstmt = connection.prepareStatement(ProductCategorySQLQueries.GET_PRODUCTS_BY_CATEGORY_ID)) {
            pstmt.setInt(1, categoryId);
            try(ResultSet rs = pstmt.executeQuery()) {
            	while(rs.next()) {
                	Product product = mapResultSetToProduct(rs);
     		        products.add(product);               
                }
            }
        } catch (SQLException e) {
        	LOGGER.error("Error retrieving products by category ID: {}", categoryId, e);            
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
		Product product = new Product();
		product.setId(rs.getInt("id"));
		product.setName(rs.getString("name"));
		product.setPrice(rs.getBigDecimal("price"));
		product.setQuantity(rs.getInt("quantity"));
		product.setAvailable(rs.getBoolean("available"));
		return product;
	}

	/**
     * Retrieves all ProductCategory entities from the database.
     *
     * @return a list of all ProductCategory entities.
     * @throws SQLException if a database access error occurs.
     */
	@Override
	public List<ProductCategory> getAll() throws SQLException {
		 List<ProductCategory> productCategories = new ArrayList<>();
		 try (PreparedStatement pstmt = connection.prepareStatement(ProductCategorySQLQueries.GET_ALL_CATEGORIES)) {	            
	            ResultSet rs = pstmt.executeQuery();
	            while(rs.next()) {
	                productCategories.add(mapResultSetToCategory(rs));
	            }
	        } catch (SQLException e) {	        	
	        	LOGGER.error("Error retrieving all categories", e);	                        
	        }
	     return productCategories;  
	}

	/**
     * Saves a ProductCategory entity. If the category exists, it is updated. Otherwise, a new category is created.
     *
     * @param productCategory the ProductCategory entity to save.
     * @return the saved ProductCategory entity.
     * @throws SQLException if a database access error occurs.
     */
	@Override
	public ProductCategory save(ProductCategory category) throws SQLException {		
		return getById(category.getId()).isPresent() ? updateCategory(category) : createCategory(category);
	}
	
	/**
     * Creates a new ProductCategory entity in the database.
     *
     * @param productCategory the ProductCategory entity to create.
     * @return the created ProductCategory entity.
     * @throws SQLException if a database access error occurs.
     */
	private ProductCategory createCategory(ProductCategory category) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement(ProductCategorySQLQueries.INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS);
		populatePreparedStatement(category, pstmt);
		pstmt.executeUpdate();
		
		DAOUtils.setGeneratedKey(pstmt, category);
		
		return category;
	}

	/**
     * Populates a PreparedStatement with ProductCategory entity data.
     *
     * @param productCategory the ProductCategory entity.
     * @param pstmt the PreparedStatement to populate.
     * @throws SQLException if a database access error occurs.
     */
	private void populatePreparedStatement(ProductCategory category, PreparedStatement pstmt) throws SQLException {
		pstmt.setString(1, category.getName());
		pstmt.setString(2, category.getType());
	}
	
	/**
     * Updates an existing ProductCategory entity in the database.
     *
     * @param productCategory the ProductCategory entity to update.
     * @return the updated ProductCategory entity.
     * @throws SQLException if a database access error occurs.
     */
	private ProductCategory updateCategory(ProductCategory category) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement(ProductCategorySQLQueries.UPDATE_CATEGORY);
		populatePreparedStatement(category, pstmt);
		pstmt.setInt(3, category.getId());
		pstmt.executeUpdate();
		
		return category;
	}

	/**
    * Deletes a ProductCategory entity by its ID and its associated products.
    *
    * @param categoryId the ID of the ProductCategory to delete.
    * @throws SQLException if a database access error occurs.
    */
	@Override
    public void delete(Integer categoryId) throws SQLException {
        try {
            connection.setAutoCommit(false);
            executeUpdate(ProductCategorySQLQueries.DELETE_ORDER_DETAIL_PRODUCTS_BY_CATEGORY_ID, categoryId);
            executeUpdate(ProductCategorySQLQueries.DELETE_PRODUCTS_BY_CATEGORY_ID, categoryId);
            executeUpdate(ProductCategorySQLQueries.DELETE_CATEGORY, categoryId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();           
            LOGGER.error("Error deleting category with ID: {}", categoryId, e);           
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
}
