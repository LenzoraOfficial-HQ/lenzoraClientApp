package com.lenzora.official.system.lenzora.ordering;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Read/write frame/lens lines of an order.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
