package com.restaurant.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a product category.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory implements IdentifiableEntity {	
	private int id;
    private String name;
    private String type;
    private List<Product> products; // OneToMany relationship
}
