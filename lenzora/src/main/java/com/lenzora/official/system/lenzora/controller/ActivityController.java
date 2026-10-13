package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.ActivityLog;
import com.lenzora.official.system.lenzora.repository.ActivityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getRecentActivities() {
        List<ActivityLog> activities = activityRepository.findTop5ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(activities);
    }
}