package com.lenzora.official.system.lenzora.s2.service;

import com.lenzora.official.system.lenzora.s2.entity.OrderStatusHistory;
import com.lenzora.official.system.lenzora.s2.repository.OrderStatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing order status history records.
 *
 * <p>This service provides business-level operations for retrieving,
 * saving, and deleting order status history records.</p>
 *
 */
@Service
public class OrderStatusHistoryService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    /**
     * Creates the order status history service.
     *
     * @param orderStatusHistoryRepository repository used to access order status history records
     */
    public OrderStatusHistoryService(
            OrderStatusHistoryRepository orderStatusHistoryRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    }

    /**
     * Retrieves all order status history records.
     *
     * @return a list of all order status history records
     */
    public List<OrderStatusHistory> getAllHistory() {
        return orderStatusHistoryRepository.findAll();
    }

    /**
     * Retrieves a specific order status history record by its ID
     *
     * @param historyId the ID of the history record
     * @return the history record if found
     */
    public Optional<OrderStatusHistory> getHistoryById(Long historyId) {
        return orderStatusHistoryRepository.findById(historyId);
    }

    /**
     * Retrieves all status history records for a specific order
     *
     * @param orderId the ID of the order
     * @return a list of status history records for the order
     */
    public List<OrderStatusHistory> getHistoryByOrderId(Long orderId) {
        return orderStatusHistoryRepository.findByOrderId(orderId);
    }

    /**
     * Retrieves the status history of an order in chronological order
     *
     * @param orderId the ID of the order
     * @return a list of status history records ordered by change time
     */
    public List<OrderStatusHistory> getHistoryByOrderIdChronologically(Long orderId) {
        return orderStatusHistoryRepository
                .findByOrderIdOrderByChangedAtAsc(orderId);
    }

    /**
     * Saves an order status history record
     *
     * @param history the order status history record to save
     * @return the saved history record
     */
    public OrderStatusHistory saveHistory(OrderStatusHistory history) {
        return orderStatusHistoryRepository.save(history);
    }

    /**
     * Deletes an order status history record by its ID
     *
     * @param historyId the ID of the history record to delete
     */
    public void deleteHistory(Long historyId) {
        orderStatusHistoryRepository.deleteById(historyId);
    }


}

