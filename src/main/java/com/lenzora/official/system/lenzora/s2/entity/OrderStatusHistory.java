package com.lenzora.official.system.lenzora.s2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a record of a change in the status of an order.
 *
 * <p>This entity is mapped to the {@code order_status_history} table
 * in the database. Each record stores the previous status, the new
 * status, the user who made the change, the time of the change,
 * and an optional note.</p>
 *
 * @author S2
 * @version 1.0
 */
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

    /**
     * Unique identifier for the order status history record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    /**
     * ID of the order whose status was changed
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * Previous status of the order before the change
     */
    @Column(name = "from_status")
    private String fromStatus;

    /**
     * New status of the order after the change
     */
    @Column(name = "to_status", nullable = false)
    private String toStatus;

    /**
     * ID of the user who made the status change
     * A null value indicates that the change was made by the system
     */
    @Column(name = "changed_by")
    private Long changedBy;

    /**
     * Date and time when the status change occurred
     */
    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    /**
     * Additional information about the status change
     */
    @Column(name = "note")
    private String note;

    // Getters and Setters

    /**
     * Returns the unique ID of this history record
     *
     * @return the history record ID
     */
    public Long getHistoryId() {
        return historyId;
    }

    /**
     * Sets the unique ID of this history record
     *
     * @param historyId the history record ID
     */
    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    /**
     * Returns the ID of the related order
     *
     * @return the order ID
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
     * Returns the previous order status
     *
     * @return the previous status
     */
    public String getFromStatus() {
        return fromStatus;
    }

    /**
     * Sets the previous order status
     *
     * @param fromStatus the previous status
     */
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    /**
     * Returns the new order status
     *
     * @return the new status
     */
    public String getToStatus() {
        return toStatus;
    }

    /**
     * Sets the new order status
     *
     * @param toStatus the new status
     */
    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    /**
     * Returns the ID of the user who changed the status
     *
     * @return the user ID, or null if changed by the system
     */
    public Long getChangedBy() {
        return changedBy;
    }

    /**
     * Sets the ID of the user who changed the status
     *
     * @param changedBy the user ID
     */
    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }

    /**
     * Returns the date and time of the status change
     *
     * @return the change date and time
     */
    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    /**
     * Sets the date and time of the status change
     *
     * @param changedAt the change date and time
     */
    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    /**
     * Returns the note associated with the status change
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note associated with the status change
     *
     * @param note the note
     */
    public void setNote(String note) {
        this.note = note;
    }
}
