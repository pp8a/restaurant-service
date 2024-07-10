package com.restaurant.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.restaurant.controllers.ProductServlet;
import com.restaurant.dao.entity.ProductDAO;
import com.restaurant.dto.ProductDTO;
import com.restaurant.entity.Product;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit tests for the {@link ProductServlet} class.
 */
class ProductServletTest {
	@Mock
    private ProductDAO productDAO;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private ProductServlet productServlet;
    
    /**
     * Sets up the test environment before each test.
     * 
     * @throws ServletException if a servlet-specific error occurs
     */
    @BeforeEach
    public void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        productDAO = mock(ProductDAO.class);
        productServlet = new ProductServlet();
        productServlet.init();
        productServlet.setProductDAO(productDAO);      
    }

    /**
     * Tests the doPost method for creating an Product.
     * 
     * @throws IOException if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testDoPost_CreateProduct() throws IOException, ServletException, SQLException {
    	ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Sample Product");
        
        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        // Set up request to return productDTO
        when(request.getInputStream()).thenReturn(new TestUtils.TestServletInputStream(productDTO));

        productServlet.doPost(request, response);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productDAO, times(1)).save(productCaptor.capture());
        Product capturedProduct = productCaptor.getValue();
        assertEquals("Sample Product", capturedProduct.getName());

        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        // Validate the response content
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Sample Product"));
    }

	private TestUtils.TestServletOutputStream setupResponseOutputStream() throws IOException {
		TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);
		return testServletOutputStream;
	}
    
	/**
     * Tests the doPut method for updating an Product.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoPut_UpdateProduct() throws IOException, SQLException, ServletException {        
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1);
        productDTO.setName("Updated Product");

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        // Set up request to return productDTO
        when(request.getInputStream()).thenReturn(new TestUtils.TestServletInputStream(productDTO));

        productServlet.doPut(request, response);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productDAO, times(1)).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();
        assertEquals(1, capturedProduct.getId());
        assertEquals("Updated Product", capturedProduct.getName());

        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Updated Product"));
    }
    
    /**
     * Tests the doGet method for retrieving an Product by ID.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoGet_GetProductById() throws IOException, SQLException, ServletException {        
        Product product = new Product();
        product.setId(1);
        product.setName("Sample Product");

        when(request.getPathInfo()).thenReturn("/1");
        when(productDAO.getById(1)).thenReturn(Optional.of(product));

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        productServlet.doGet(request, response);

        verify(productDAO, times(1)).getById(1);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Sample Product"));
    }

    /**
     * Tests the doGet method for retrieving all Products.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoGet_GetAllProducts() throws IOException, SQLException, ServletException {        
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Sample Product 1");

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Sample Product 2");

        List<Product> productList = List.of(product1, product2);
        when(productDAO.getAll()).thenReturn(productList);

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        // Setup path info to be null to trigger getAllProducts
        when(request.getPathInfo()).thenReturn(null);

        productServlet.doGet(request, response);

        verify(productDAO, times(1)).getAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Sample Product 1"));
        assertTrue(jsonResponse.contains("Sample Product 2"));
    }
    
    /**
     * Tests the doDelete method for deleting an Product.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoDelete_DeleteProduct() throws IOException, SQLException, ServletException {
        // Mocking request and response
        Product product = new Product();
        product.setId(1);
        product.setName("Sample Product");

        when(request.getPathInfo()).thenReturn("/1");
        when(productDAO.getById(1)).thenReturn(Optional.of(product));

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        productServlet.doDelete(request, response);

        verify(productDAO, times(1)).delete(1);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Product deleted"));
    }
}
