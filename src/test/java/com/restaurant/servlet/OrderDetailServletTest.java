package com.restaurant.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restaurant.controllers.OrderDetailServlet;
import com.restaurant.dao.impl.OrderDetailDAO;
import com.restaurant.dto.OrderDetailDTO;
import com.restaurant.entity.OrderDetail;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit tests for the OrderDetailServlet class.
 */
class OrderDetailServletTest {
    @Mock
    private OrderDetailDAO orderDetailDAO;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private OrderDetailServlet orderDetailServlet;
    
    /**
     * Sets up the test environment before each test.
     * 
     * @throws ServletException if a servlet-specific error occurs
     */
    @BeforeEach
    public void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        orderDetailDAO = mock(OrderDetailDAO.class);
        orderDetailServlet = new OrderDetailServlet();
        orderDetailServlet.init();
        orderDetailServlet.setOrderDetailDAO(orderDetailDAO);
    }
    
    /**
     * Tests the doPost method for creating an OrderDetail.
     * 
     * @throws IOException if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testDoPost_CreateOrderDetail() throws IOException, ServletException, SQLException {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setTotalAmount(BigDecimal.valueOf(99.99));

        TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);

        when(request.getInputStream()).thenReturn(new TestUtils.TestServletInputStream(orderDetailDTO));

        orderDetailServlet.doPost(request, response);

        ArgumentCaptor<OrderDetail> orderCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailDAO, times(1)).save(orderCaptor.capture());
        OrderDetail capturedOrder = orderCaptor.getValue();
        assertEquals(BigDecimal.valueOf(99.99), capturedOrder.getTotalAmount());

        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("99.99"));
    }
    
    /**
     * Tests the doPut method for updating an OrderDetail.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoPut_UpdateOrderDetail() throws IOException, SQLException, ServletException {
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setId(1);
        orderDetailDTO.setTotalAmount(BigDecimal.valueOf(199.99));

        TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);

        when(request.getInputStream()).thenReturn(new TestUtils.TestServletInputStream(orderDetailDTO));

        orderDetailServlet.doPut(request, response);

        ArgumentCaptor<OrderDetail> orderCaptor = ArgumentCaptor.forClass(OrderDetail.class);
        verify(orderDetailDAO, times(1)).save(orderCaptor.capture());

        OrderDetail capturedOrder = orderCaptor.getValue();
        assertEquals(1, capturedOrder.getId());
        assertEquals(BigDecimal.valueOf(199.99), capturedOrder.getTotalAmount());

        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("199.99"));
    }
    
    /**
     * Tests the doGet method for retrieving an OrderDetail by ID.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoGet_GetOrderById() throws IOException, SQLException, ServletException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1);
        orderDetail.setTotalAmount(BigDecimal.valueOf(99.99));

        when(request.getPathInfo()).thenReturn("/1");
        when(orderDetailDAO.getById(1)).thenReturn(Optional.of(orderDetail));

        TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);

        orderDetailServlet.doGet(request, response);

        verify(orderDetailDAO, times(1)).getById(1);
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("99.99"));
    }
    
    /**
     * Tests the doGet method for retrieving all OrderDetails.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoGet_GetAllOrders() throws IOException, SQLException, ServletException {
        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setId(1);
        orderDetail1.setTotalAmount(BigDecimal.valueOf(99.99));

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setId(2);
        orderDetail2.setTotalAmount(BigDecimal.valueOf(199.99));

        List<OrderDetail> orderList = List.of(orderDetail1, orderDetail2);
        when(orderDetailDAO.getAll()).thenReturn(orderList);

        TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);

        when(request.getPathInfo()).thenReturn(null);

        orderDetailServlet.doGet(request, response);

        verify(orderDetailDAO, times(1)).getAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("99.99"));
        assertTrue(jsonResponse.contains("199.99"));
    }
    
    /**
     * Tests the doDelete method for deleting an OrderDetail.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoDelete_DeleteOrderDetail() throws IOException, SQLException, ServletException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1);
        orderDetail.setTotalAmount(BigDecimal.valueOf(99.99));

        when(request.getPathInfo()).thenReturn("/1");
        when(orderDetailDAO.getById(1)).thenReturn(Optional.of(orderDetail));

        TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);

        orderDetailServlet.doDelete(request, response);

        verify(orderDetailDAO, times(1)).delete(1);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Order deleted"));
    }
}