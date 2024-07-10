package com.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.restaurant.dto.ProductDTO;
import com.restaurant.entity.Product;

/**
 * Mapper interface for Product entity and DTO.
 */
@Mapper
public interface ProductMapper {
	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);		
	
	/**
     * Converts Product entity to ProductDTO.
     *
     * @param product the Product entity.
     * @return the ProductDTO.
     */
	ProductDTO toDTO(Product product);	
	
	/**
     * Converts ProductDTO to Product entity.
     *
     * @param productDTO the ProductDTO.
     * @return the Product entity.
     */
	Product toEntity(ProductDTO productDTO);
}
