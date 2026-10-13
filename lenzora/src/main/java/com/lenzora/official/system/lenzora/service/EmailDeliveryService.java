package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.EmailDelivery;
import com.lenzora.official.system.lenzora.repository.EmailDeliveryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Creates and updates email delivery records. */
@Service
public class EmailDeliveryService {

    private final EmailDeliveryRepository emailDeliveryRepository;

    /**
     * Creates the email delivery service.
     *
     * @param emailDeliveryRepository repository used to access
     *                                 email delivery records
     */
    public EmailDeliveryService(
            EmailDeliveryRepository emailDeliveryRepository) {
        this.emailDeliveryRepository = emailDeliveryRepository;
    }

    /**
     * Retrieves all email delivery records.
     *
     * @return a list of all email delivery records
     */
    public List<EmailDelivery> getAllEmailDeliveries() {
        return emailDeliveryRepository.findAll();
    }

    /**
     * Retrieves an email delivery record by its ID.
     *
     * @param emailId the ID of the email delivery record
     * @return the email delivery record if found
     */
    public Optional<EmailDelivery> getEmailDeliveryById(Long emailId) {
        return emailDeliveryRepository.findById(emailId);
    }

    /**
     * Retrieves email delivery records with a specific status.
     *
     * @param status the email delivery status
     * @return a list of email delivery records with the specified status
     */
    public List<EmailDelivery> getEmailDeliveriesByStatus(String status) {
        return emailDeliveryRepository.findByStatus(status);
    }

    /**
     * Retrieves email delivery records related to a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of email delivery records related to the order
     */
    public List<EmailDelivery> getEmailDeliveriesByOrderId(Long orderId) {
        return emailDeliveryRepository.findByOrderId(orderId);
    }

    /**
     * Retrieves email delivery records related to a specific notification.
     *
     * @param notificationId the ID of the notification
     * @return a list of email delivery records related to the notification
     */
    public List<EmailDelivery> getEmailDeliveriesByNotificationId(
            Long notificationId) {
        return emailDeliveryRepository.findByNotificationId(notificationId);
    }

    /**
     * Retrieves email delivery records with a specific status
     * in chronological order.
     *
     * @param status the email delivery status
     * @return a list of email delivery records ordered by creation time
     */
    public List<EmailDelivery> getEmailDeliveriesByStatusOrdered(
            String status) {
        return emailDeliveryRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    /**
     * Saves an email delivery record, filling in the times when they are empty.
     *
     * @param emailDelivery the email delivery record to save
     * @return the saved email delivery record
     */
    public EmailDelivery saveEmailDelivery(EmailDelivery emailDelivery) {

        LocalDateTime now = LocalDateTime.now();

        if (emailDelivery.getCreatedAt() == null) {
            emailDelivery.setCreatedAt(now);
        }

        emailDelivery.setUpdatedAt(now);

        return emailDeliveryRepository.save(emailDelivery);
    }

    /**
     * Marks an email delivery record as successfully sent.
     *
     * @param emailId the ID of the email delivery record
     * @return the updated email delivery record if found
     */
    public Optional<EmailDelivery> markAsSent(Long emailId) {

        Optional<EmailDelivery> emailOptional =
                emailDeliveryRepository.findById(emailId);

        if (emailOptional.isPresent()) {

            EmailDelivery emailDelivery = emailOptional.get();

            emailDelivery.setStatus("SENT");
            emailDelivery.setSentAt(LocalDateTime.now());
            emailDelivery.setLastError(null);
            emailDelivery.setUpdatedAt(LocalDateTime.now());

            return Optional.of(emailDeliveryRepository.save(emailDelivery));
        }

        return Optional.empty();
    }

    /**
     * Marks an email delivery record as failed and counts the attempt.
     *
     * @param emailId the ID of the email delivery record
     * @param errorMessage the error message describing the failure
     * @return the updated email delivery record if found
     */
    public Optional<EmailDelivery> markAsFailed(
            Long emailId, String errorMessage) {

        Optional<EmailDelivery> emailOptional =
                emailDeliveryRepository.findById(emailId);

        if (emailOptional.isPresent()) {

            EmailDelivery emailDelivery = emailOptional.get();

            emailDelivery.setStatus("FAILED");

            int currentAttempts = emailDelivery.getAttemptCount() == null
                    ? 0
                    : emailDelivery.getAttemptCount();

            emailDelivery.setAttemptCount(currentAttempts + 1);
            emailDelivery.setLastError(errorMessage);
            emailDelivery.setUpdatedAt(LocalDateTime.now());

            return Optional.of(emailDeliveryRepository.save(emailDelivery));
        }

        return Optional.empty();
    }

    /**
     * Deletes an email delivery record by its ID.
     *
     * @param emailId the ID of the email delivery record to delete
     */
    public void deleteEmailDelivery(Long emailId) {
        emailDeliveryRepository.deleteById(emailId);
    }
}

