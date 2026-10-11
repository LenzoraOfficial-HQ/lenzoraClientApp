package com.lenzora.official.system.lenzora.shared;

import jakarta.persistence.MappedSuperclass;

/**
 * Backward-compatible superclass for entities using common persistence auditing.
 */
@MappedSuperclass
public abstract class AuditableEntity extends BaseAuditableEntity {
}
