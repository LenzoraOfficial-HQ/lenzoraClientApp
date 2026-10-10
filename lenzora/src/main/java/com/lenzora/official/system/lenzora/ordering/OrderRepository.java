package com.lenzora.official.system.lenzora.ordering;

import com.lenzora.official.system.lenzora.ordering.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Reading and writing orders.*/
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);
    Optional<Order> findByOrderNumber(String orderNumber);

    /** The orders sitting in one status, newest first - used by the admin review screen. */
    List<Order> findByStatusOrderByOrderDateDescIdDesc(OrderStatus status);

    /** How many orders sit in one status - used by the admin portal counters. */
    long countByStatus(OrderStatus status);
}
