package com.restaurant.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for OrderApproval.
 */
@Getter
@Setter
public class OrderApprovalDTO {
	private int id;
	private OrderDetailDTO orderDetail;
}
