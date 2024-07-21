package com.restaurant.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.restaurant.dao.DaoFactory;
import com.restaurant.dao.impl.ProductDAO;
import com.restaurant.dto.ProductDTO;
import com.restaurant.entity.Product;
import com.restaurant.mapper.ProductMapper;

/**
 * Servlet implementation class ProductServlet.
 * This servlet handles HTTP requests for managing products in the restaurant service.
 */
@WebServlet(urlPatterns = {ApiPaths.PRODUCTS, ApiPaths.PRODUCTS + "/*"})
public class ProductServlet extends HttpServlet{	
	private static final long serialVersionUID = 1L;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private transient ProductDAO productDAO;	

	/**
     * Initializes the servlet and sets up the ProductDAO.
     *
     * @throws ServletException if an error occurs during initialization.
     */
	@Override
	public void init() throws ServletException {
		DaoFactory daoFactory = new DaoFactory();
		productDAO = daoFactory.getProductDao();
	}
	
	/**
	 * Set with ProductDao parameters for testing.
	 * @param productDAO to be used for test
	 */
	public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
	
	/**
     * Handles HTTP POST requests to create or update a product.
     *
     * @param req  the HttpServletRequest object.
     * @param resp the HttpServletResponse object.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ProductDTO productDTO;	
		try {
			productDTO = parseRequestBody(req, resp, ProductDTO.class);
		} catch (IOException e) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request body");
	        return;
	    }
		
		if(productDTO == null) return;
		
		Product product = ProductMapper.INSTANCE.toEntity(productDTO);
		
		try {
            productDAO.save(product);
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating or updating product");
            return;
        }
		
		ProductDTO createProductDTO = ProductMapper.INSTANCE.toDTO(product);
		sendResponse(resp, HttpServletResponse.SC_CREATED, createProductDTO);		
	}
	
	/**
	 * Handles HTTP PUT requests to update an existing product.
	 *
	 * @param req  the HttpServletRequest object containing the request parameters and body.
	 * @param resp the HttpServletResponse object for sending the response.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException      if an I/O error occurs while reading the request body or writing the response.
	 */
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    ProductDTO productDTO;
	    try {
	    	productDTO = parseRequestBody(req, resp, ProductDTO.class);
	    } catch (IOException e) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request body");
	        return;
	    }
	    
	    if (productDTO == null) return;

	    // Check if the product ID is provided
	    if (productDTO.getId() == 0) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product ID is required for update");
	        return;
	    }

	    Product product = ProductMapper.INSTANCE.toEntity(productDTO);

	    try {
	        productDAO.save(product);
	    } catch (SQLException e) {
	        sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating product");
	        return;
	    }

	    ProductDTO updatedProductDTO = ProductMapper.INSTANCE.toDTO(product);
	    sendResponse(resp, HttpServletResponse.SC_OK, updatedProductDTO);
	}
	
	/**
     * Parses the request body to a specified DTO class.
     *
     * @param req   the HttpServletRequest object.
     * @param resp  the HttpServletResponse object.
     * @param clazz the DTO class to parse the request body to.
     * @param <T>   the type of the DTO.
     * @return the parsed DTO object, or null if parsing fails.
     * @throws IOException if an I/O error occurs.
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
     * Handles HTTP GET requests to retrieve products.
     *
     * @param req  the HttpServletRequest object.
     * @param resp the HttpServletResponse object.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if(pathInfo == null || pathInfo.equals(ApiPaths.PRODUCTS)) {
			getAllProducts(resp);
		} else {
			getProductById(resp, pathInfo);
		}
	}
	
	/**
     * Retrieves a product by ID and sends the response.
     *
     * @param resp     the HttpServletResponse object.
     * @param pathInfo the path info containing the product ID.
     * @throws IOException if an I/O error occurs.
     */
	private void getProductById(HttpServletResponse resp, String pathInfo) {
		// Extract product ID from the URL
		String productIdStr = pathInfo.substring(1);		
		try {
			int productId = Integer.parseInt(productIdStr);
			Optional<Product> productOptional = productDAO.getById(productId);
			if(productOptional.isPresent()) {
				ProductDTO productDTO = ProductMapper.INSTANCE.toDTO(productOptional.get());
				sendResponse(resp, HttpServletResponse.SC_OK, productDTO);
			}else {
				sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
			}
		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving product");
		}
	}

	/**
     * Retrieves all products and sends the response.
     *
     * @param resp the HttpServletResponse object.
     * @throws IOException if an I/O error occurs.
     */
	private void getAllProducts(HttpServletResponse resp) {
		try {
			List<Product> products = productDAO.getAll();
			List<ProductDTO> productDTOs = products.stream()
					.map(ProductMapper.INSTANCE::toDTO)
					.toList();
			sendResponse(resp, HttpServletResponse.SC_OK, productDTOs);
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving products");
		}
	}	
	
	/**
     * Handles HTTP DELETE requests to delete a product by ID.
     *
     * @param req  the HttpServletRequest object.
     * @param resp the HttpServletResponse object.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product ID is required");
            return;
        }
        
        String productIdStr = pathInfo.substring(1);
        try {
        	int productId = Integer.parseInt(productIdStr);
        	Optional<Product> existingProduct = productDAO.getById(productId);
        	if(existingProduct.isPresent()) {
        		productDAO.delete(productId);
        		sendResponse(resp, HttpServletResponse.SC_NO_CONTENT, "Product deleted");
        	} else {
        		sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Product not found");
        	}        	
		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting product");
		}
	}
	
	/**
     * Sends an error response with the specified status code and message.
     *
     * @param resp         the HttpServletResponse object.
     * @param statusCode   the HTTP status code.
     * @param errorMessage the error message.
     * @throws IOException if an I/O error occurs.
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
     * Sends a response with the specified status code and response object.
     *
     * @param resp           the HttpServletResponse object.
     * @param statusCode     the HTTP status code.
     * @param responseObject the response object to send.
     * @throws IOException if an I/O error occurs.
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
