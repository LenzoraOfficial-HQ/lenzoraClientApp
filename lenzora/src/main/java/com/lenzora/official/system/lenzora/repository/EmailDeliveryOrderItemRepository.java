package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.ordering.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailDeliveryOrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    @Query(value = "SELECT * FROM order_item WHERE order_id = :orderId",
            nativeQuery = true)
    List<OrderItem> findItemsForEmailDelivery(
            @Param("orderId") Long orderId
    );
}