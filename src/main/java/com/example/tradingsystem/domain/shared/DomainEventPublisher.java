package com.example.tradingsystem.domain.shared;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布器
 * 用于发布领域事件，解耦领域模型和应用层
 */
@Component
public class DomainEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}

