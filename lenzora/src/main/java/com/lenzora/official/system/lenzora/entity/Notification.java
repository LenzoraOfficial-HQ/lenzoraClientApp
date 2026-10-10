package com.lenzora.official.system.lenzora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a notification sent to a user within the Lenzora system.
 *
 * <p>This entity is mapped to the {@code notifications} table in the
 * database. A notification contains information about its recipient,
 * the related order, notification type, title, message, read status,
 * and creation time.</p>
 *
 */
@Entity
<<<<<<< HEAD
@Table(name = "notifications")
=======
@Table(name = "notification")
>>>>>>> 2b7f0e32aa0b4d41ab49b1ed728e5fc14222ecbb
public class Notification {

    /**
     * Unique identifier for the notification
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    /**
     * ID of the user who receives the notification
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * ID of the order related to the notification.
     * This field is optional because not every notification is related to an order.
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * Type of the notification, such as ORDER_STATUS, ACCOUNT, or SYSTEM
     */
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Title displayed for the notification.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Detailed message content of the notification.
     */
    @Column(name = "message", nullable = false)
    private String message;

    /**
     * Indicates whether the notification has been read
     * The default value is false.
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * Date and time when the notification was created
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters and Setters

    /**
     * Returns the unique ID of the notification
     *
     * @return the notification ID
     */
    public Long getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the unique ID of the notification
     *
     * @param notificationId the notification ID
     */
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Returns the ID of the user receiving the notification
     *
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user receiving the notification
     *
     * @param userId the user ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Returns the ID of the related order
     *
     * @return the order ID, or null if the notification is not related to an order
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets the ID of the related order
     *
     * @param orderId the order ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns the notification type
     *
     * @return the notification type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the notification type
     *
     * @param type the notification type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the notification title
     *
     * @return the notification title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the notification title
     *
     * @param title the notification title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the notification message
     *
     * @return the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the notification message
     *
     * @param message the notification message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns whether the notification has been read
     *
     * @return true if the notification has been read; false otherwise
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * Sets whether the notification has been read
     *
     * @param isRead true if the notification has been read; false otherwise
     */
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Returns the date and time when the notification was created
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date and time when the notification was created
     *
     * @param createdAt the creation date and time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
