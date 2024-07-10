package com.restaurant.entity;

/**
 * Enumeration representing the status of an order.
 */
public enum OrderStatus {
	ACCEPTED(1),
	APPROVED(2),
	CANCELLED(3),
	PAID(4);
	
	private final int id;

    OrderStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

