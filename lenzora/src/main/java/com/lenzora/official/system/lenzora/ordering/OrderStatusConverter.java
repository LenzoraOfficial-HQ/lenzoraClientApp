package com.lenzora.official.system.lenzora.ordering;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<Order.OrderStatus, String> {

    /**
     * Turns a status into the text the database stores.
     *
     * @param status status
     * @return the database word
     */
    @Override
    public String convertToDatabaseColumn(Order.OrderStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    /**
     * Turns the text read from the database back into a status.
     *
     * @param code the DB word
     * @return the matching status
     */
    @Override
    public Order.OrderStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        Order.OrderStatus[] known = Order.OrderStatus.values();
        for (int i = 0; i < known.length; i++) {
            if (known[i].getCode().equals(code)) {
                return known[i];
            }
        }
        System.out.println("Unknown order status read from the database: " + code);
        return null;
    }
}
