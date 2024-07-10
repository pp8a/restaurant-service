package com.restaurant.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a product.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product implements IdentifiableEntity{
	private int id;
	private String name;
	private BigDecimal price;
	private int quantity;
	private boolean available;
	private ProductCategory productCategory;  // ManyToOne relationship
}
