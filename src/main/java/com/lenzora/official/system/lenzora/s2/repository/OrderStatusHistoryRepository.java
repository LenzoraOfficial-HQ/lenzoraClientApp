package com.lenzora.official.system.lenzora.s2.repository;

import com.lenzora.official.system.lenzora.s2.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for managing order status history records.
 *
 * <p>This repository provides database operations for the
 * {@link OrderStatusHistory} entity using Spring Data JPA.</p>
 *
 * @author S2
 * @version 1.0
 */

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

