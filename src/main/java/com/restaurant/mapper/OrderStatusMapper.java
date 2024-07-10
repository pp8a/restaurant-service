package com.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.restaurant.dto.OrderStatusDTO;
import com.restaurant.entity.OrderStatus;

/**
 * Mapper interface for OrderStatus entity and DTO.
 */
@Mapper
public interface OrderStatusMapper {
	OrderStatusMapper INSTANCE = Mappers.getMapper(OrderStatusMapper.class);
	
	/**
     * Converts OrderStatus entity to OrderStatusDTO.
     *
     * @param orderStatus the OrderStatus entity.
     * @return the OrderStatusDTO.
     */
	@Named("toDto")
    default OrderStatusDTO toDTO(OrderStatus orderStatus) {
    	if(orderStatus == null) {
    		return null;
    	}
    	
    	OrderStatusDTO dto = new OrderStatusDTO();
    	dto.setStatusName(orderStatus.name());
    	
    	return dto;    	
    }    
    
	/**
     * Converts OrderStatusDTO to OrderStatus entity.
     *
     * @param orderStatusDTO the OrderStatusDTO.
     * @return the OrderStatus entity.
     */
    @Named("toEntity")
    default OrderStatus toEntity(OrderStatusDTO orderStatusDTO) {
    	if(orderStatusDTO == null) {
    		return null;
    	}    	   	
    	
		return OrderStatus.valueOf(orderStatusDTO.getStatusName());    	
    }
}
