package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import com.lenzora.official.system.lenzora.ordering.Order;
import com.lenzora.official.system.lenzora.ordering.OrderItem;
import com.lenzora.official.system.lenzora.ordering.OrderRepository;
import com.lenzora.official.system.lenzora.repository.EmailDeliveryOrderItemRepository;
import org.springframework.stereotype.Service;

import com.lenzora.official.system.lenzora.entity.EmailDelivery;
import com.lenzora.official.system.lenzora.repository.EmailDeliveryRepository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;
import java.util.Optional;

@Service
public class EmailDeliveryOrderService {

    private final OrderRepository orderRepository;
    private final EmailDeliveryOrderItemRepository orderItemRepository;
    private final FrameRepository frameRepository;
    private final EmailDeliveryRepository emailDeliveryRepository;

    public EmailDeliveryOrderService(
            OrderRepository orderRepository,
            EmailDeliveryOrderItemRepository orderItemRepository,
            FrameRepository frameRepository,
            EmailDeliveryRepository emailDeliveryRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.frameRepository = frameRepository;
        this.emailDeliveryRepository = emailDeliveryRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findAll()
                .stream()
                .filter(item -> orderId.equals(item.getOrderId()))
                .toList();
    }

    public Supplier getSupplierForItem(OrderItem item) {

        if (item == null || item.getFrameId() == null) {
            return null;
        }

        Frame frame = findFrame(item.getFrameId());

        if (frame == null) {
            return null;
        }

        return frame.getSupplier();
    }

    private Frame findFrame(Long frameId) {
        return frameRepository.findById(frameId).orElse(null);
    }

    public int generateSupplierEmails(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found: " + orderId));

        List<OrderItem> items = orderItemRepository.findItemsForEmailDelivery(orderId);

        Map<String, StringBuilder> supplierMessages = new LinkedHashMap<>();

        for (OrderItem item : items) {
            Supplier supplier = getSupplierForItem(item);

            if (supplier == null
                    || supplier.getSupplierEmail() == null
                    || supplier.getSupplierEmail().isBlank()) {
                continue;
            }

            String email = supplier.getSupplierEmail();

            supplierMessages.computeIfAbsent(email, key -> new StringBuilder())
                    .append("- ")
                    .append(item.getFrameDescription() != null
                            ? item.getFrameDescription() : "Frame")
                    .append(", quantity: ")
                    .append(item.getQuantity())
                    .append("\n");
        }

        int generated = 0;

        for (Map.Entry<String, StringBuilder> entry : supplierMessages.entrySet()) {

            String recipient = entry.getKey();

            // Avoid generating another record for the same order and recipient.
            boolean alreadyExists = emailDeliveryRepository
                    .findByOrderId(orderId)
                    .stream()
                    .anyMatch(existing ->
                            recipient.equalsIgnoreCase(existing.getRecipientEmail()));

            if (alreadyExists) {
                continue;
            }

            EmailDelivery email = new EmailDelivery();

            email.setOrderId(orderId);
            email.setRecipientEmail(recipient);
            email.setRecipientType("SUPPLIER");
            email.setContainsPatientData(false);
            email.setSubject("Lenzora Order " + order.getOrderNumber());
            email.setBody(
                    "Dear Supplier,\n\n"
                            + "Please review the following items for order "
                            + order.getOrderNumber() + ".\n\n"
                            + entry.getValue()
                            + "\nKind regards,\nLenzora"
            );
            email.setStatus("PENDING");
            email.setAttemptCount(0);
            email.setSentAt(null);
            email.setLastError(null);

            LocalDateTime now = LocalDateTime.now();
            email.setCreatedAt(now);
            email.setUpdatedAt(now);

            emailDeliveryRepository.save(email);
            generated++;
        }

        return generated;
    }
}
