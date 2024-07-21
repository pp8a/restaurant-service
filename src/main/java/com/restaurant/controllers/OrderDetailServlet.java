package com.restaurant.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.restaurant.dao.DaoFactory;
import com.restaurant.dao.impl.OrderDetailDAO;
import com.restaurant.dto.OrderDetailDTO;
import com.restaurant.entity.OrderDetail;
import com.restaurant.mapper.OrderDetailMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class OrderDetailServlet
 * This servlet handles the CRUD operations for OrderDetail entities.
 */
@WebServlet(urlPatterns = {ApiPaths.ORDER_DETAILS, ApiPaths.ORDER_DETAILS + "/*"})
public class OrderDetailServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private transient OrderDetailDAO orderDetailDAO;
	
	/**
     * Initializes the servlet and sets up the  OrderDetailDAO instance for test.
     */
	@Override
	public void init() throws ServletException {
		DaoFactory daoFactory = new DaoFactory();
		orderDetailDAO = daoFactory.getDetailDAO();
	}	
	
	public void setOrderDetailDAO(OrderDetailDAO orderDetailDAO) {
		this.orderDetailDAO = orderDetailDAO;
	}

	/**
     * Handles HTTP GET requests to retrieve detail of product.
     * If a detail ID is provided, it retrieves the specific detail of product.
     * Otherwise, it retrieves all details.
     *
     * @param req  the HttpServletRequest object containing the request parameters.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs while writing the response.
     */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if(pathInfo == null || pathInfo.equals(ApiPaths.PRODUCT_CATEGORIES)) {			
			getAllOrders(resp);
		} else {
			getOrderById(resp, pathInfo);
		}
	}
	
	/**
     * Retrieves a detail of product by its ID and sends it as a JSON response.
     *
     * @param resp     the HttpServletResponse object for sending the response.
     * @param pathInfo the URL path info containing the detail ID of product.
     * @throws IOException if an I/O error occurs while writing the response.
     */
	private void getOrderById(HttpServletResponse resp, String pathInfo) {
		// Extract product ID from the URL
		String orderIdStr = pathInfo.substring(1);		
		try {
			int orderId = Integer.parseInt(orderIdStr);
			Optional<OrderDetail> orderOptional = orderDetailDAO.getById(orderId);
			if(orderOptional.isPresent()) {
				OrderDetailDTO orderDTO = OrderDetailMapper.INSTANCE.toDTO(orderOptional.get());
				sendResponse(resp, HttpServletResponse.SC_OK, orderDTO);
			}else {
				sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
			}
		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving detail of order");
		}
	}
	
	/**
     * Retrieves all details of orders and sends them as a JSON response.
     *
     * @param resp the HttpServletResponse object for sending the response.
     * @throws IOException if an I/O error occurs while writing the response.
     */
	private void getAllOrders(HttpServletResponse resp) {
		try {
			List<OrderDetail> orders = orderDetailDAO.getAll();
			List<OrderDetailDTO> orderDTOs = orders.stream()
					.map(OrderDetailMapper.INSTANCE::toDTO)
					.toList();
			sendResponse(resp, HttpServletResponse.SC_OK, orderDTOs);
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving detail of orders");
		}
	}
	
	/**
     * Handles HTTP POST requests to create a new detail of order.
     *
     * @param req  the HttpServletRequest object containing the request body.
     * @param resp the HttpServletResponse object for sending the response.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs while reading the request body or writing the response.
     */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OrderDetailDTO orderDTO;
		try {
			orderDTO = parseRequestBody(req, resp, OrderDetailDTO.class);
		} catch (IOException e) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request body");
	        return;
	    }
		if(orderDTO == null) return;
		
		OrderDetail order = OrderDetailMapper.INSTANCE.toEntity(orderDTO);
		
		try {
            orderDetailDAO.save(order);
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating or updating detail of order");
            return;
        }
		
		OrderDetailDTO createOrderDTO = OrderDetailMapper.INSTANCE.toDTO(order);
		sendResponse(resp, HttpServletResponse.SC_CREATED, createOrderDTO);	
		
	}
	
	/**
	 * Handles HTTP PUT requests to update an existing detail of order.
	 *
	 * @param req  the HttpServletRequest object containing the request parameters and body.
	 * @param resp the HttpServletResponse object for sending the response.
	 * @throws ServletException if a servlet-specific error occurs.
	 * @throws IOException      if an I/O error occurs while reading the request body or writing the response.
	 */
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OrderDetailDTO orderDTO;	
		try {
			orderDTO = parseRequestBody(req, resp, OrderDetailDTO.class);
		} catch (IOException e) {
	        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Failed to parse request body");
	        return;
	    }
		if(orderDTO == null) return;
		
		if(orderDTO.getId() == 0) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Order Details ID is required for update");
		}
		
		OrderDetail order = OrderDetailMapper.INSTANCE.toEntity(orderDTO);
		
		try {
            orderDetailDAO.save(order);
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating or updating detail of order");
            return;
        }
		
		OrderDetailDTO createOrderDTO = OrderDetailMapper.INSTANCE.toDTO(order);
		sendResponse(resp, HttpServletResponse.SC_OK, createOrderDTO);	
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
     * Handles HTTP DELETE requests to delete a detail of order.
     * If a order ID is provided, it deletes the specific order.
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
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Order Detail ID is required");
            return;
        }
        
        String orderIdStr = pathInfo.substring(1);
        try {
        	int orderId = Integer.parseInt(orderIdStr);
        	Optional<OrderDetail> existingOrder = orderDetailDAO.getById(orderId);
        	if(existingOrder.isPresent()) {
        		orderDetailDAO.delete(orderId);
        		sendResponse(resp, HttpServletResponse.SC_NO_CONTENT, "Order deleted");
        	} else {
        		sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
        	}        	
		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
		} catch (SQLException e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting detail of order");
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
