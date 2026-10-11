package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void logActionPersistsActorTargetActionAndTimestamp() {
        AppUser actor = new AppUser();
        actor.setEmail("admin@example.com");
        when(appUserRepository.findByEmail(actor.getEmail())).thenReturn(Optional.of(actor));

        auditService.logAction("APPROVAL", actor.getEmail(), "app_user", "Orthoptist approved");

        ArgumentCaptor<AuditLog> savedLog = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(savedLog.capture());
        assertEquals("APPROVAL", savedLog.getValue().getAuditAction());
        assertEquals(actor.getEmail(), savedLog.getValue().getUsername());
        assertEquals(actor, savedLog.getValue().getUser());
        assertEquals("app_user", savedLog.getValue().getEntityName());
        assertEquals("Orthoptist approved", savedLog.getValue().getDetails());
        assertNotNull(savedLog.getValue().getCreatedAt());
    }
}
