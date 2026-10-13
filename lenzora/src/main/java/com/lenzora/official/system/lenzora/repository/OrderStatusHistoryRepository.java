package com.lenzora.official.system.lenzora.repository;


import com.lenzora.official.system.lenzora.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Reads and writes order status history rows. */

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long>{

    /**
     * Retrieves all status history records for a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of status history records for the order
     */
    List<OrderStatusHistory> findByOrderId(Long orderId);

    /**
     * Retrieves the status history records for an order in chronological order.
     *
     * @param orderId the ID of the order
     * @return a list of status history records ordered by change time
     */
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Long orderId);

}



