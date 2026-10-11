package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminAuditLogControllerTest {

    @Test
    void loadsRequestedPageOfAuditLogs() {
        AuditLogRepository repository = mock(AuditLogRepository.class);
        when(repository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(
                        List.of(new AuditLog()), invocation.getArgument(0), 41));
        AdminAuditLogController controller = new AdminAuditLogController(repository);
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.auditLogs(2, model);

        assertEquals("admin-audit-logs", view);
        assertEquals(2, ((org.springframework.data.domain.Page<?>) model.get("auditLogs")).getNumber());
        verify(repository).findAllByOrderByCreatedAtDesc(Pageable.ofSize(20).withPage(2));
    }

    @Test
    void negativePageNumbersAreClampedToFirstPage() {
        AuditLogRepository repository = mock(AuditLogRepository.class);
        when(repository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        AdminAuditLogController controller = new AdminAuditLogController(repository);

        controller.auditLogs(-1, new ExtendedModelMap());

        verify(repository).findAllByOrderByCreatedAtDesc(Pageable.ofSize(20));
    }
}
