package com.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.restaurant.dto.OrderDetailDTO;
import com.restaurant.entity.OrderDetail;

/**
 * Mapper interface for OrderDetail entity and DTO.
 */
@Mapper(uses = {OrderStatusMapper.class})
public interface OrderDetailMapper {
	OrderDetailMapper INSTANCE = Mappers.getMapper(OrderDetailMapper.class);
	
	/**
     * Converts OrderDetail entity to OrderDetailDTO.
     *
     * @param orderDetail the OrderDetail entity.
     * @return the OrderDetailDTO.
     */
	@Mapping(source = "orderStatus", target = "orderStatus", qualifiedByName = "toDto")
	OrderDetailDTO toDTO(OrderDetail orderDetail);
	
	 /**
     * Converts OrderDetailDTO to OrderDetail entity.
     *
     * @param orderDetailDTO the OrderDetailDTO.
     * @return the OrderDetail entity.
     */
	@Mapping(source = "orderStatus", target = "orderStatus", qualifiedByName = "toEntity")
	OrderDetail toEntity(OrderDetailDTO orderDetailDTO);
}
