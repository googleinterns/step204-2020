package com.google.job.data;

import com.google.cloud.firestore.Query.Direction;

/** Enumeration for order that can be applied to sorting for job listings. */
public enum Order {
    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING");

    private final String orderId;

    Order(String orderId) {
        this.orderId = orderId;
    }

    /** Returns the stable id representing the order. Can be stored in database. */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Returns the order enum matching the provided id.
     *
     * @throws IllegalArgumentException If a status stable id cannot be found for provided id.
     */
    public static Order getFromId(String id) throws IllegalArgumentException {
        for (Order order: values()){
            if (order.getOrderId().equals(id)){
                return order;
            }
        }

        throw new IllegalArgumentException("Invalid order id: " + id);
    }

    public static Direction getQueryDirection(Order order) {
        switch(order) {
            case ASCENDING:
                return Direction.ASCENDING;
            case DESCENDING:
                return Direction.DESCENDING;
        }

        throw new IllegalArgumentException("Invalid order: " + order);
    }
}
