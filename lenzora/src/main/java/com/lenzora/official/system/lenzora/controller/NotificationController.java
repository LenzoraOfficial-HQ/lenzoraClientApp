package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import com.lenzora.official.system.lenzora.service.NotificationService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificationController{

    private final NotificationService notificationService;
    private final AppUserRepository appUserRepository;

    public NotificationController(NotificationService notificationService, AppUserRepository appUserRepository){
        this.notificationService = notificationService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/notifications")
    public String showNotifications( Authentication authentication, Model model){

        // Get the email of the logged-in user
        String email = authentication.getName();

        // Find the user's AppUser record
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user was not found in AppUser"));

        // Retrieve this user's notifications
        Long userId = appUser.getUserId();

        List<com.lenzora.official.system.lenzora.entity.Notification>
                notifications = notificationService.getNotificationsByUserNewestFirst(userId);

        // Retrieve unread notifications
        List<com.lenzora.official.system.lenzora.entity.Notification>
                unreadNotifications = notificationService.getUnreadNotifications(userId);

        // Send notification data to the HTML page
        model.addAttribute("notifications", notifications);
        model.addAttribute("totalCount", notifications.size());
        model.addAttribute("unreadCount", unreadNotifications.size());
        model.addAttribute("readCount",notifications.size() - unreadNotifications.size());

        return "notifications";
    }

    @PostMapping("/notifications/{notificationId}/read")
    public String markNotificationAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        // Identify the logged-in user
        String email = authentication.getName();

        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Logged-in user was not found in AppUser"));

        Long userId = appUser.getUserId();

        // Find the notification
        var notificationOptional =
                notificationService.getNotificationById(notificationId);

        // Only allow the owner to mark their notification as read
        if (notificationOptional.isPresent()
                && notificationOptional.get().getUserId().equals(userId)) {

            notificationService.markAsRead(notificationId);
        }

        // Return to the notification page
        return "redirect:/notifications";
    }


}
