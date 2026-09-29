package com.lenzora.official.system.lenzora.ordering;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Reading and writing orders.*/
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);
    Optional<Order> findByOrderNumber(String orderNumber);
}
