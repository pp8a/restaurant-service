package com.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.restaurant.dto.ProductCategoryDTO;
import com.restaurant.entity.ProductCategory;

/**
 * Mapper interface for ProductCategory entity and DTO.
 */
@Mapper
public interface ProductCategoryMapper {
	ProductCategoryMapper INSTANCE = Mappers.getMapper(ProductCategoryMapper.class);	
	
	 /**
     * Converts ProductCategory entity to ProductCategoryDTO.
     *
     * @param productCategory the ProductCategory entity.
     * @return the ProductCategoryDTO.
     */
	ProductCategoryDTO toDTO(ProductCategory productCategory);	
	
	/**
     * Converts ProductCategoryDTO to ProductCategory entity.
     *
     * @param productCategoryDTO the ProductCategoryDTO.
     * @return the ProductCategory entity.
     */
	ProductCategory toEntity(ProductCategoryDTO productCategoryDTO);

}
