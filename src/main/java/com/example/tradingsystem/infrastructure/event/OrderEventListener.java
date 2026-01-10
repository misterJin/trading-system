package com.example.tradingsystem.infrastructure.event;

import com.example.tradingsystem.domain.order.OrderCompletedEvent;
import com.example.tradingsystem.domain.order.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 订单事件监听器
 * 演示如何使用 Spring 的 @EventListener 监听领域事件
 * 
 * ApplicationEventPublisher 的工作流程：
 * 1. OrderService 发布事件 -> DomainEventPublisher.publish()
 * 2. DomainEventPublisher 调用 -> ApplicationEventPublisher.publishEvent()
 * 3. Spring 容器查找所有监听该事件的监听器
 * 4. 调用监听器的处理方法（同步或异步）
 */
@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    /**
     * 监听订单创建事件
     * 可以在这里做：
     * - 发送通知给用户
     * - 记录审计日志
     * - 更新统计信息
     * - 触发其他业务流程
     */
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("订单已创建: orderId={}, username={}, sku={}, quantity={}, totalPrice={}",
                event.orderId(), event.username(), event.sku(), 
                event.quantity(), event.totalPrice());
        
        // 这里可以添加其他业务逻辑，比如：
        // - 发送邮件通知
        // - 更新用户购物历史
        // - 记录操作日志
    }

    /**
     * 监听订单完成事件
     * 可以在这里做：
     * - 发送确认邮件
     * - 更新库存预警
     * - 触发发货流程
     * - 更新商家统计
     */
    @EventListener
    @Async  // 异步处理，不阻塞主流程
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("订单已完成: orderId={}, username={}, merchant={}, sku={}, totalPrice={}",
                event.orderId(), event.username(), event.merchantName(), 
                event.sku(), event.totalPrice());
        
        // 这里可以添加其他业务逻辑，比如：
        // - 发送订单确认邮件
        // - 通知商家有新订单
        // - 更新推荐算法
        // - 触发积分系统
    }
}

