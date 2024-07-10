package com.restaurant.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for Product.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
	private int id;
	private String name;
	private BigDecimal price;
	private int quantity;
	private boolean available;
	private ProductCategoryDTO productCategory;
}
