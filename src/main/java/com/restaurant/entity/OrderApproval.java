package com.restaurant.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing an order approval.
 */
@Getter
@Setter
public class OrderApproval implements IdentifiableEntity{
	private int id;
	private OrderDetail orderDetail; // OneToOne relationship
}
