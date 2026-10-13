package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.EmailDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Reads and writes email delivery records. */
public interface EmailDeliveryRepository extends JpaRepository<EmailDelivery, Long> {

    /**
     * Retrieves email delivery records with a specific delivery status.
     *
     * @param status the delivery status
     * @return a list of email delivery records with the specified status
     */
    List<EmailDelivery> findByStatus(String status);

    /**
     * Retrieves email delivery records related to a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of email delivery records related to the order
     */
    List<EmailDelivery> findByOrderId(Long orderId);

    /**
     * Retrieves email delivery records related to a specific notification.
     *
     * @param notificationId the ID of the notification
     * @return a list of email delivery records related to the notification
     */
    List<EmailDelivery> findByNotificationId(Long notificationId);

    /**
     * Retrieves pending email delivery records ordered by creation time.
     *
     * @return a list of pending email delivery records, oldest first
     */
    List<EmailDelivery> findByStatusOrderByCreatedAtAsc(String status);
}

