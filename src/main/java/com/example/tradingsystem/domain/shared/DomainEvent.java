package com.example.tradingsystem.domain.shared;

import java.time.Instant;

/**
 * 领域事件标记接口
 * 所有领域事件都应该实现此接口
 */
public interface DomainEvent {
    Instant occurredOn();
}

