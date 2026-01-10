package com.example.tradingsystem.domain.order;

import com.example.tradingsystem.domain.shared.DomainEvent;
import com.example.tradingsystem.domain.shared.Money;

import java.time.Instant;

/**
 * 订单完成事件
 */
public record OrderCompletedEvent(
        Long orderId,
        String username,
        String merchantName,
        String sku,
        Long quantity,
        Money totalPrice,
        Instant occurredOn
) implements DomainEvent {
    
    public OrderCompletedEvent {
        if (occurredOn == null) {
            occurredOn = Instant.now();
        }
    }
    
    public OrderCompletedEvent(Long orderId, String username, String merchantName, 
                              String sku, Long quantity, Money totalPrice) {
        this(orderId, username, merchantName, sku, quantity, totalPrice, Instant.now());
    }
}

