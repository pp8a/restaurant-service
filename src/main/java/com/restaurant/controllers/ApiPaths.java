package com.restaurant.controllers;

/**
 * Utility class that defines the API paths for various resources.
 * This class contains constants for the endpoint paths used in the RESTful services.
 */
public class ApiPaths {
	 public static final String PRODUCTS = "/products";
	 public static final String PRODUCT_CATEGORIES = "/product-categories";
	 public static final String ORDER_DETAILS = "/order-details";
//	 public static final String ORDER_APPROVAL = "/order-approval";//reserve
//	 public static final String ORDER_STATUS = "/order-status";//reserve
	 
	 private ApiPaths() {
	        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	 }
}
