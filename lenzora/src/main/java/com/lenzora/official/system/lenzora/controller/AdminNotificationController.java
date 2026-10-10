package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.entity.Notification;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import com.lenzora.official.system.lenzora.repository.NotificationRepository;
import com.lenzora.official.system.lenzora.service.NotificationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final AppUserRepository appUserRepository;

    public AdminNotificationController(
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            AppUserRepository appUserRepository) {

        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.appUserRepository = appUserRepository;
    }

    // Display notification management page
    @GetMapping
    public String showNotifications(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String readStatus,
            Model model) {

        List<Notification> allNotifications =
                notificationRepository.findAll();

        // Filter by notification type
        List<Notification> notifications = allNotifications;

        if (type != null && !type.isBlank()) {
            notifications = notifications.stream()
                    .filter(n -> type.equals(n.getType()))
                    .toList();
        }

        // Filter by read status
        if ("unread".equals(readStatus)) {
            notifications = notifications.stream()
                    .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                    .toList();

        } else if ("read".equals(readStatus)) {
            notifications = notifications.stream()
                    .filter(n -> Boolean.TRUE.equals(n.getIsRead()))
                    .toList();
        }

        // Search by title or message
        if (search != null && !search.isBlank()) {
            String query = search.trim().toLowerCase();

            notifications = notifications.stream()
                    .filter(n ->
                            (n.getTitle() != null &&
                                    n.getTitle().toLowerCase().contains(query))
                                    ||
                                    (n.getMessage() != null &&
                                            n.getMessage().toLowerCase().contains(query))
                    )
                    .toList();
        }

        // Calculate summary counts
        long totalCount = allNotifications.size();

        long unreadCount = allNotifications.stream()
                .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                .count();

        long readCount = totalCount - unreadCount;

        long systemCount = allNotifications.stream()
                .filter(n -> "SYSTEM".equals(n.getType()))
                .count();

        // Send data to the HTML page
        model.addAttribute("notifications", notifications);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("readCount", readCount);
        model.addAttribute("systemCount", systemCount);

        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("readStatus", readStatus);

        return "admin/notifications";
    }

    // Display the create notification form
    @GetMapping("/create")
    public String showCreateNotificationForm(Model model) {

        List<AppUser> users = appUserRepository.findAll();

        model.addAttribute("users", users);

        return "admin/create-notification";
    }

    // Save a new notification
    @PostMapping("/create")
    public String createNotification(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam(required = false) Long orderId,
            @RequestParam String title,
            @RequestParam String message,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate required fields
        if (title.isBlank() || message.isBlank()) {
            model.addAttribute("users", appUserRepository.findAll());
            model.addAttribute("errorMessage",
                    "Title and message cannot be empty.");

            return "admin/create-notification";
        }

        // Validate notification type
        if (!List.of("ORDER_STATUS", "ACCOUNT", "SYSTEM")
                .contains(type)) {

            model.addAttribute("users", appUserRepository.findAll());
            model.addAttribute("errorMessage",
                    "Please select a valid notification type.");

            return "admin/create-notification";
        }

        // Confirm that the recipient exists
        if (!appUserRepository.existsById(userId)) {
            model.addAttribute("users", appUserRepository.findAll());
            model.addAttribute("errorMessage",
                    "The selected recipient could not be found.");

            return "admin/create-notification";
        }

        // Create the notification
        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setOrderId(orderId);
        notification.setType(type);
        notification.setTitle(title.trim());
        notification.setMessage(message.trim());
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Save to the existing database table
        notificationService.saveNotification(notification);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Notification created successfully!");

        return "redirect:/admin/notifications";
    }
}