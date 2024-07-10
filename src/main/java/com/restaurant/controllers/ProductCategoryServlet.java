package com.restaurant.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.restaurant.dao.DaoFactory;
import com.restaurant.dao.entity.ProductCategoryDAO;
import com.restaurant.dto.ProductCategoryDTO;
import com.restaurant.entity.ProductCategory;
import com.restaurant.mapper.ProductCategoryMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ProductCategoryServlet
 * This servlet handles the CRUD operations for ProductCategory entities.
 */
@WebServlet(urlPatterns = {ApiPaths.PRODUCT_CATEGORIES, ApiPaths.PRODUCT_CATEGORIES + "/*"})
public class ProductCategoryServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private transient ProductCategoryDAO categoryDAO;
	
	/**
     * Initializes the servlet and sets up the ProductCategoryDAO instance for test.
     */
	@Override
	public void init() throws ServletException {
		DaoFactory daoFactory = new DaoFactory();
		categoryDAO = daoFactory.getCategoryDAO();		
	}
	
	public void setCategoryDAO(ProductCategoryDAO categoryDAO) {
		this.categoryDAO = categoryDAO;
	}
	
	 /**
     * Handles HTTP POST requests to create a new product category.
     *
     * @param req  the HttpServletRequest object containing the request body.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs while reading the request body or writing the response.
     */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ProductCategoryDTO categoryDTO;	
		try {
			categoryDTO = parseRequestBody(req, resp, ProductCategoryDTO.class);
		} catch (IOException e) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request body");
	        return;
	    }
		if(categoryDTO == null) return;
		
		ProductCategory category = ProductCategoryMapper.INSTANCE.toEntity(categoryDTO);
		
		try {
            categoryDAO.save(category);
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating or updating category of rpoduct");
            return;
        }
		
		ProductCategoryDTO createCategoryDTO = ProductCategoryMapper.INSTANCE.toDTO(category);
		sendResponse(resp, HttpServletResponse.SC_CREATED, createCategoryDTO);		
	}
	
	/**
	 * Handles HTTP PUT requests to update an existing product category.
	 *
	 * @param req  the HttpServletRequest object containing the request parameters and body.
	 * @param resp the HttpServletResponse object for sending the response.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException      if an I/O error occurs while reading the request body or writing the response.
	 */
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ProductCategoryDTO categoryDTO;	
		try {
			categoryDTO = parseRequestBody(req, resp, ProductCategoryDTO.class);
		} catch (IOException e) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request body");
	        return;
	    }
		
		if(categoryDTO == null) return;
		
		if(categoryDTO.getId() == 0) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category ID is required for update");
	        return;
		}
		
		ProductCategory category = ProductCategoryMapper.INSTANCE.toEntity(categoryDTO);
		
		try {
            categoryDAO.save(category);
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating or updating category of product");
            return;
        }
		
		ProductCategoryDTO createCategoryDTO = ProductCategoryMapper.INSTANCE.toDTO(category);
		sendResponse(resp, HttpServletResponse.SC_OK, createCategoryDTO);		
	}
	
	/**
     * Parses the request body to extract a DTO object.
     *
     * @param req   the HttpServletRequest object containing the request body.
     * @param resp  the HttpServletResponse object for sending the response.
     * @param clazz the class of the DTO to be parsed.
     * @param <T>   the type of the DTO.
     * @return the parsed DTO object, or null if parsing fails.
     * @throws IOException if an I/O error occurs while reading the request body.
     */
	private <T> T parseRequestBody(HttpServletRequest req, HttpServletResponse resp, Class<T> clazz)
			throws IOException {
		try {
			return OBJECT_MAPPER.readValue(req.getInputStream(), clazz);
		} catch (IOException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request data");			
			return null;
		}		
	}
	
	/**
     * Handles HTTP GET requests to retrieve product categories.
     * If a category ID is provided, it retrieves the specific category.
     * Otherwise, it retrieves all categories.
     *
     * @param req  the HttpServletRequest object containing the request parameters.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs while writing the response.
     */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		String pathInfo = req.getPathInfo();
		try {
			if(pathInfo == null || pathInfo.equals(ApiPaths.PRODUCT_CATEGORIES)) {			
				getAllCategories(resp);
			} else {
				getCategoryById(resp, pathInfo);
			}
		} catch (Exception e) {
	        sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
	    }
	}
	
	/**
     * Retrieves a product category by its ID and sends it as a JSON response.
     *
     * @param resp     the HttpServletResponse object for sending the response.
     * @param pathInfo the URL path info containing the product category ID.
     * @throws IOException if an I/O error occurs while writing the response.
     */
	private void getCategoryById(HttpServletResponse resp, String pathInfo) {
		// Extract product ID from the URL
		String categoryIdStr = pathInfo.substring(1);		
		try {
			int categoryId = Integer.parseInt(categoryIdStr);
			Optional<ProductCategory> categoryOptional = categoryDAO.getById(categoryId);
			if(categoryOptional.isPresent()) {
				ProductCategoryDTO categoryDTO = ProductCategoryMapper.INSTANCE.toDTO(categoryOptional.get());
				sendResponse(resp, HttpServletResponse.SC_OK, categoryDTO);
			}else {
				sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
			}
		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID format");
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving category of product");
		}
	}

	 /**
     * Retrieves all product categories and sends them as a JSON response.
     *
     * @param resp the HttpServletResponse object for sending the response.
     * @throws IOException if an I/O error occurs while writing the response.
     */
	private void getAllCategories(HttpServletResponse resp) {
		try {
			List<ProductCategory> categories = categoryDAO.getAll();
			List<ProductCategoryDTO> categoryDTOs = categories.stream()
					.map(ProductCategoryMapper.INSTANCE::toDTO)
					.toList();
			sendResponse(resp, HttpServletResponse.SC_OK, categoryDTOs);
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving categories of products");
		}
	}
	
	/**
     * Handles HTTP DELETE requests to delete a product category.
     * If a category ID is provided, it deletes the specific category.
     *
     * @param req  the HttpServletRequest object containing the request parameters.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs while writing the response.
     */
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category ID is required");
            return;
        }
        
        String categoryIdStr = pathInfo.substring(1);
        try {
        	int categoryId = Integer.parseInt(categoryIdStr);
        	Optional<ProductCategory> existingCategory = categoryDAO.getById(categoryId);
        	if(existingCategory.isPresent()) {
        		categoryDAO.delete(categoryId);
        		sendResponse(resp, HttpServletResponse.SC_NO_CONTENT, "Category deleted");
        	} else {
        		sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Category not found");
        	}        	
		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID format");
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting category");
		}
	}
	
	/**
     * Sends an error response with the specified status code and error message.
     *
     * @param resp         the HttpServletResponse object for sending the response.
     * @param statusCode   the HTTP status code to set in the response.
     * @param errorMessage the error message to include in the response.
     * @throws IOException if an I/O error occurs while writing the response.
     */
	private void sendError(HttpServletResponse resp, int statusCode, String errorMessage) {
		try {
	        resp.sendError(statusCode, errorMessage);
	    } catch (IOException e) {
	        Logger logger = LoggerFactory.getLogger(OrderDetailServlet.class);
	        logger.error("Failed to send error response: " + e.getMessage(), e);
	    }
    }
	
	/**
     * Sends a JSON response with the specified status code and response object.
     *
     * @param resp           the HttpServletResponse object for sending the response.
     * @param statusCode     the HTTP status code to set in the response.
     * @param responseObject the response object to include in the response body.
     * @throws IOException if an I/O error occurs while writing the response.
     */
	private void sendResponse(HttpServletResponse resp, int statusCode, Object responseObject) {
		try {
	        resp.setStatus(statusCode);
	        resp.setContentType("application/json");
	        OBJECT_MAPPER.writeValue(resp.getOutputStream(), responseObject);
	    } catch (IOException e) {
	        Logger logger = LoggerFactory.getLogger(OrderDetailServlet.class);
	        logger.error("Failed to send response: " + e.getMessage(), e);
	    }
    }	
	
}
