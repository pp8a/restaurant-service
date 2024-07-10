package com.restaurant.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for OrderDetail.
 */
@Getter
@Setter
public class OrderDetailDTO {
	private int id;
    private OrderStatusDTO orderStatus;
    private List<ProductDTO> products;
    private BigDecimal totalAmount;
}
