package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.ordering.Order;
import com.lenzora.official.system.lenzora.ordering.Order.OrderStatus;
import com.lenzora.official.system.lenzora.ordering.OrderRepository;
import com.lenzora.official.system.lenzora.service.OrderStatusHistoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/order-status")
public class AdminOrderStatusController {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryService orderStatusHistoryService;

    public AdminOrderStatusController(
            OrderRepository orderRepository,
            OrderStatusHistoryService orderStatusHistoryService) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryService = orderStatusHistoryService;
    }

    @GetMapping
    public String showOrderStatuses(
            @RequestParam(required = false) Long orderId,
            Model model) {

        List<Order> orders = orderRepository.findAll();

        model.addAttribute("orders", orders);
        model.addAttribute("totalCount", orders.size());
        model.addAttribute(
                "reviewCount",
                orderRepository.countByStatus(OrderStatus.SENT_FOR_REVIEW));
        model.addAttribute(
                "deliveredCount",
                orderRepository.countByStatus(OrderStatus.DELIVERED));
        model.addAttribute(
                "completedCount",
                orderRepository.countByStatus(OrderStatus.COMPLETED));
        model.addAttribute("orderStatuses", OrderStatus.values());

        if (orderId != null) {
            Order selectedOrder =
                    orderRepository.findById(orderId).orElse(null);

            if (selectedOrder != null) {
                model.addAttribute("selectedOrder", selectedOrder);
                model.addAttribute(
                        "orderHistory",
                        orderStatusHistoryService
                                .getHistoryByOrderIdChronologically(orderId));
            }
        }

        return "admin/order-status";
    }

    // Open the separate Update Order page.
    @GetMapping("/{orderId}/edit")
    public String showUpdateOrderPage(
            @PathVariable Long orderId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected order could not be found.");
            return "redirect:/admin/order-status";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderStatuses", OrderStatus.values());

        return "admin/update-order-status";
    }

    // Save the new status and remarks.
    @PostMapping("/change")
    public String changeOrderStatus(
            @RequestParam Long orderId,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            RedirectAttributes redirectAttributes) {

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected order could not be found.");
            return "redirect:/admin/order-status";
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);

            // moveTo enforces the existing order-editing rules.
            boolean changed = order.moveTo(newStatus);

            if (!changed) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "This order cannot be changed from its current status.");
                return "redirect:/admin/order-status/"
                        + orderId + "/edit";
            }

            // Preserve existing notes and update the remarks.
            order.annotate(order.getNotes(), remarks);

            // The database trigger records actual status changes.
            orderRepository.save(order);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Order updated successfully.");

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please select a valid order status.");
            return "redirect:/admin/order-status/" + orderId + "/edit";
        }

        return "redirect:/admin/order-status/" + orderId + "/edit";
    }
}




