package com.lenzora.official.system.lenzora.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an email delivery record in the Lenzora system.
 *
 * <p>This entity is mapped to the {@code email_delivery} table in the
 * database. It stores information about outgoing emails, including
 * the recipient, subject, message body, delivery status, number of
 * attempts,
 * sending time, and any error that occurred during delivery.</p>
 *
 */
@Entity
@Table(name = "email_delivery")
public class EmailDelivery {

    /**
     * Unique identifier for the email delivery record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private Long emailId;

    /**
     * ID of the order related to the email.
     * This field is optional because not every email needs to be related to an order.
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * ID of the notification related to the email.
     * This field is optional because an email does not necessarily need
     * to originate from an in-app notification.
     */
    @Column(name = "notification_id")
    private Long notificationId;

    /**
     * Email address of the recipient.
     */
    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    /**
     * Recipient type
     */
    @Column(name = "recipient_type", nullable = false)
    private String recipientType = "SUPPLIER";

    /**
     * Identify whether the email contains patient data.
     */
    @Column(name = "contains_patient_data", nullable = false)
    private Boolean containsPatientData = false;

    /**
     * Subject of the email.
     */
    @Column(name = "subject", nullable = false)
    private String subject;

    /**
     * Body content of the email.
     */
    @Column(name = "body")
    private String body;

    /**
     * Current delivery status of the email.
     * Possible values are PENDING, SENT, and FAILED.
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * Number of attempts made to deliver the email.
     */
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    /**
     * Date and time when the email was successfully sent.
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * Error message from the most recent failed delivery attempt.
     */
    @Column(name = "last_error")
    private String lastError;

    /**
     * Date and time when the email delivery record was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Date and time when the email delivery record was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // Getters and Setters

    /**
     * Returns the unique ID of the email delivery record.
     *
     * @return the email ID
     */
    public Long getEmailId() {
        return emailId;
    }

    /**
     * Sets the unique ID of the email delivery record.
     *
     * @param emailId the email ID
     */
    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    /**
     * Returns the ID of the related order.
     *
     * @return the order ID, or null if there is no related order
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets the ID of the related order.
     *
     * @param orderId the order ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns the ID of the related notification.
     *
     * @return the notification ID, or null if there is no related notification
     */
    public Long getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the ID of the related notification.
     *
     * @param notificationId the notification ID
     */
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Returns the recipient's email address.
     *
     * @return the recipient email address
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * Sets the recipient's email address.
     *
     * @param recipientEmail the recipient email address
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    /**
     * Returns the email subject.
     *
     * @return the email subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the email subject.
     *
     * @param subject the email subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the email body.
     *
     * @return the email body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the email body.
     *
     * @param body the email body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns the current delivery status.
     *
     * @return the delivery status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the delivery status.
     *
     * @param status the delivery status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the number of delivery attempts.
     *
     * @return the attempt count
     */
    public Integer getAttemptCount() {
        return attemptCount;
    }

    /**
     * Sets the number of delivery attempts.
     *
     * @param attemptCount the attempt count
     */
    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    /**
     * Returns the date and time when the email was sent.
     *
     * @return the sent date and time
     */
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    /**
     * Sets the date and time when the email was sent.
     *
     * @param sentAt the sent date and time
     */
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * Returns the error from the most recent failed delivery attempt.
     *
     * @return the last error message
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * Sets the error from the most recent failed delivery attempt.
     *
     * @param lastError the error message
     */
    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    /**
     * Returns the date and time when the record was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the date and time when the record was created.
     *
     * @param createdAt the creation date and time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the date and time when the record was last updated.
     *
     * @return the last update date and time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the date and time when the record was last updated.
     *
     * @param updatedAt the last update date and time
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the recipient type of the user.
     *
     * @return the recipient type.
     */
    public String getRecipientType(){
        return recipientType;
    }

    /**
     * Sets the recipient's type
     *
     * @param recipientType the type of the recipient
     */
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    /**
     * Returns patient details boolean
     *
     * @return boolean that tells whether the email contains patient data
     */
    public Boolean getContainsPatientData() {
        return containsPatientData;
    }

    /**
     * Sets patient data boolean
     *
     * @param containsPatientData that tells whether the email contains patient data
     */
    public void setContainsPatientData(Boolean containsPatientData) {
        this.containsPatientData = containsPatientData;
    }

}
