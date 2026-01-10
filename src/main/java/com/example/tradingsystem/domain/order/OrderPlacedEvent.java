package com.example.tradingsystem.domain.order;

import com.example.tradingsystem.domain.shared.DomainEvent;
import com.example.tradingsystem.domain.shared.Money;

import java.time.Instant;

/**
 * 订单已创建事件
 */
public record OrderPlacedEvent(
        Long orderId,
        String username,
        String merchantName,
        String sku,
        Long quantity,
        Money totalPrice,
        Instant occurredOn
) implements DomainEvent {
    
    public OrderPlacedEvent {
        if (occurredOn == null) {
            occurredOn = Instant.now();
        }
    }
    
    public OrderPlacedEvent(Long orderId, String username, String merchantName, 
                           String sku, Long quantity, Money totalPrice) {
        this(orderId, username, merchantName, sku, quantity, totalPrice, Instant.now());
    }
}

