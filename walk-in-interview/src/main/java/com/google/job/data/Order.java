package com.google.job.data;

/** Enumeration for order that can be applied to sorting for job listings. */
public enum Order {
    ASCENDING("ASCENDING"),
    DESCENDING("DESCENDING");

    private final String orderId;

    Order(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns the stable id representing the order. Can be stored in database.
     *
     * @return the stable id.
     */
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
}
