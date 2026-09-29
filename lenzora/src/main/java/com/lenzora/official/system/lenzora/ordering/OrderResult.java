package com.lenzora.official.system.lenzora.ordering;

/**
 * The result of trying to save an order.
 */
public class OrderResult {

    private final Order order;
    private final String message;

    private OrderResult(Order order, String message) {
        this.order = order;
        this.message = message;
    }

    public static OrderResult saved(Order order) {
        return new OrderResult(order, "");
    }

    public static OrderResult failed(String message) {
        return new OrderResult(null, message);
    }

    public boolean isSaved() {
        return order != null;
    }

    public Order getOrder() {
        return order;
    }

    public String getMessage() {
        return message;
    }
}
