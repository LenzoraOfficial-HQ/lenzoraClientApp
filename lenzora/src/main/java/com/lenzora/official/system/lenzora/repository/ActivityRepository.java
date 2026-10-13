package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop5ByOrderByCreatedAtDesc();
}