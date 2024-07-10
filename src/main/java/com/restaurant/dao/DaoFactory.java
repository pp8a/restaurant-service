package com.restaurant.dao;

import com.restaurant.dao.entity.OrderDetailDAO;
import com.restaurant.dao.entity.ProductCategoryDAO;
import com.restaurant.dao.entity.ProductDAO;

/**
 * Factory class for creating DAO instances.
 */
public class DaoFactory {
	 /**
     * Returns an instance of ProductDAO.
     * 
     * @return an instance of ProductDAO
     */
	public ProductDAO getProductDao() {
		return new ProductDAO();
	}
	
	 /**
     * Returns an instance of ProductCategoryDAO.
     * 
     * @return an instance of ProductCategoryDAO
     */
	public ProductCategoryDAO getCategoryDAO() {
		return new ProductCategoryDAO();
	}
	
	/**
     * Returns an instance of OrderDetailDAO.
     * 
     * @return an instance of OrderDetailDAO
     */
	public OrderDetailDAO getDetailDAO() {
		return new OrderDetailDAO();
	}
	
}
