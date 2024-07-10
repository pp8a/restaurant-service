package com.restaurant.entity;

/**
 * Interface for entities that can be identified by an ID.
 */
public interface IdentifiableEntity {
	void setId(int id);
    int getId();
}
