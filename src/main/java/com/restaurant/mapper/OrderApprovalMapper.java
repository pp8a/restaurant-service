package com.restaurant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.restaurant.dto.OrderApprovalDTO;
import com.restaurant.entity.OrderApproval;

/**
 * Mapper interface for OrderApproval entity and DTO.
 */
@Mapper(uses = OrderStatusMapper.class)
public interface OrderApprovalMapper {
	OrderApprovalMapper INSTANCE = Mappers.getMapper(OrderApprovalMapper.class);

	/**
     * Converts OrderApproval entity to OrderApprovalDTO.
     *
     * @param orderApproval the OrderApproval entity.
     * @return the OrderApprovalDTO.
     */
    @Mapping(source = "orderDetail.orderStatus", target = "orderDetail.orderStatus", qualifiedByName = "toDto")
    OrderApprovalDTO toDTO(OrderApproval orderApproval);

    /**
     * Converts OrderApprovalDTO to OrderApproval entity.
     *
     * @param orderApprovalDTO the OrderApprovalDTO.
     * @return the OrderApproval entity.
     */
    @Mapping(source = "orderDetail.orderStatus", target = "orderDetail.orderStatus", qualifiedByName = "toEntity")
    OrderApproval toEntity(OrderApprovalDTO orderApprovalDTO);
}
