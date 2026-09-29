package com.lenzora.official.system.lenzora.s2.repository;

import com.lenzora.official.system.lenzora.s2.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for managing notification records.
 *
 * <p>This repository provides standard CRUD operations and
 * notification-specific queries for the {@link Notification} entity.</p>
 *
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retrieves all notifications belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of notifications belonging to the user
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Retrieves all unread notifications belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of unread notifications
     */
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    /**
     * Retrieves all notifications related to a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of notifications related to the order
     */
    List<Notification> findByOrderId(Long orderId);

    /**
     * Retrieves a user's notifications with the newest notifications first.
     *
     * @param userId the ID of the user
     * @return a list of notifications ordered by creation time in descending order
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
