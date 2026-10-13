package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.Notification;
import com.lenzora.official.system.lenzora.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Creates and updates notifications. */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Creates the notification service.
     *
     * @param notificationRepository repository used to access notification records
     */
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Retrieves all notifications.
     *
     * @return a list of all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    /**
     * Retrieves a notification by its ID.
     *
     * @param notificationId the ID of the notification
     * @return the notification if found
     */
    public Optional<Notification> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    /**
     * Retrieves all notifications belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of the user's notifications
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    /**
     * Retrieves all unread notifications belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of unread notifications
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    /**
     * Retrieves all notifications related to a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of notifications related to the order
     */
    public List<Notification> getNotificationsByOrderId(Long orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    /**
     * Retrieves a user's notifications with the newest notifications first.
     *
     * @param userId the ID of the user
     * @return a list of notifications ordered by creation time
     */
    public List<Notification> getNotificationsByUserNewestFirst(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Saves a notification, filling in the creation time when it is empty.
     *
     * @param notification the notification to save
     * @return the saved notification
     */
    public Notification saveNotification(Notification notification) {

        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }

        return notificationRepository.save(notification);
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification
     * @return the updated notification if found
     */
    public Optional<Notification> markAsRead(Long notificationId) {

        Optional<Notification> notificationOptional =
                notificationRepository.findById(notificationId);

        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            notification.setIsRead(true);

            return Optional.of(notificationRepository.save(notification));
        }

        return Optional.empty();
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param notificationId the ID of the notification to delete
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
