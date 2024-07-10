package com.restaurant.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for ProductCategory.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryDTO {
	private int id;
    private String name;
    private String type;
    private List<ProductDTO> products;
}
