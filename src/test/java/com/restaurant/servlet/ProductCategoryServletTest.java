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

import com.restaurant.controllers.ProductCategoryServlet;
import com.restaurant.dao.impl.ProductCategoryDAO;
import com.restaurant.dto.ProductCategoryDTO;
import com.restaurant.entity.ProductCategory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit tests for the ProductCategoryServlet class.
 */
class ProductCategoryServletTest {
    @Mock
    private ProductCategoryDAO categoryDAO;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private ProductCategoryServlet categoryServlet;
    
    /**
     * Sets up the test environment before each test.
     * 
     * @throws ServletException if a servlet-specific error occurs
     */
    @BeforeEach
    public void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        categoryDAO = mock(ProductCategoryDAO.class);
        categoryServlet = new ProductCategoryServlet();
        categoryServlet.init();
        categoryServlet.setCategoryDAO(categoryDAO);      
    }
    
    /**
     * Tests the doPost method for creating an ProductCategory.
     * 
     * @throws IOException if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testDoPost_CreateCategory() throws IOException, ServletException, SQLException {
        ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
        categoryDTO.setName("Sample Category");
        
        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        when(request.getInputStream()).thenReturn(new TestUtils.TestServletInputStream(categoryDTO));

        categoryServlet.doPost(request, response);

        ArgumentCaptor<ProductCategory> categoryCaptor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(categoryDAO, times(1)).save(categoryCaptor.capture());
        ProductCategory capturedCategory = categoryCaptor.getValue();
        assertEquals("Sample Category", capturedCategory.getName());

        verify(response).setStatus(HttpServletResponse.SC_CREATED);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Sample Category"));
    }

	private TestUtils.TestServletOutputStream setupResponseOutputStream() throws IOException {
		TestUtils.TestServletOutputStream testServletOutputStream = new TestUtils.TestServletOutputStream();
        when(response.getOutputStream()).thenReturn(testServletOutputStream);
		return testServletOutputStream;
	}
    
	/**
     * Tests the doPut method for updating an ProductCategory.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoPut_UpdateCategory() throws IOException, SQLException, ServletException {
        ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Updated Category");

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        when(request.getInputStream()).thenReturn(new TestUtils.TestServletInputStream(categoryDTO));

        categoryServlet.doPut(request, response);

        ArgumentCaptor<ProductCategory> categoryCaptor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(categoryDAO, times(1)).save(categoryCaptor.capture());

        ProductCategory capturedCategory = categoryCaptor.getValue();
        assertEquals(1, capturedCategory.getId());
        assertEquals("Updated Category", capturedCategory.getName());

        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Updated Category"));
    }
    
    /**
     * Tests the doGet method for retrieving an ProductCategory by ID.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoGet_GetCategoryById() throws IOException, SQLException, ServletException {
        ProductCategory category = new ProductCategory();
        category.setId(1);
        category.setName("Sample Category");

        when(request.getPathInfo()).thenReturn("/1");
        when(categoryDAO.getById(1)).thenReturn(Optional.of(category));

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        categoryServlet.doGet(request, response);

        verify(categoryDAO, times(1)).getById(1);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Sample Category"));
    }
    
    /**
     * Tests the doGet method for retrieving all ProductCategories.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoGet_GetAllCategories() throws IOException, SQLException, ServletException {
        ProductCategory category1 = new ProductCategory();
        category1.setId(1);
        category1.setName("Sample Category 1");

        ProductCategory category2 = new ProductCategory();
        category2.setId(2);
        category2.setName("Sample Category 2");

        List<ProductCategory> categoryList = List.of(category1, category2);
        when(categoryDAO.getAll()).thenReturn(categoryList);

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        when(request.getPathInfo()).thenReturn(null);

        categoryServlet.doGet(request, response);

        verify(categoryDAO, times(1)).getAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Sample Category 1"));
        assertTrue(jsonResponse.contains("Sample Category 2"));
    }
    
    /**
     * Tests the doDelete method for deleting an ProductCategory.
     * 
     * @throws IOException if an input or output error occurs
     * @throws SQLException if a database access error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Test
    void testDoDelete_DeleteCategory() throws IOException, SQLException, ServletException {
        ProductCategory category = new ProductCategory();
        category.setId(1);
        category.setName("Sample Category");

        when(request.getPathInfo()).thenReturn("/1");
        when(categoryDAO.getById(1)).thenReturn(Optional.of(category));

        TestUtils.TestServletOutputStream testServletOutputStream = setupResponseOutputStream();

        categoryServlet.doDelete(request, response);

        verify(categoryDAO, times(1)).delete(1);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        
        String jsonResponse = testServletOutputStream.getResponseContent();
        assertTrue(jsonResponse.contains("Category deleted"));
    }
}
