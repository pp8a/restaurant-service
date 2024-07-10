package com.restaurant.entity;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing the details of an order.
 */
@Getter
@Setter
public class OrderDetail implements IdentifiableEntity{
	private int id;
	private OrderStatus orderStatus; // OneToOne relationship
	private List<Product> products; // ManyToMany relationship
	private BigDecimal totalAmount;
}
