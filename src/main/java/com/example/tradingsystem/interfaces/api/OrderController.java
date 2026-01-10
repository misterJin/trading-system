package com.example.tradingsystem.interfaces.api;

import com.example.tradingsystem.application.OrderService;
import com.example.tradingsystem.domain.order.Order;
import com.example.tradingsystem.interfaces.api.dto.PlaceOrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(request.getUsername(), request.getSku(), request.getQuantity());
        return ResponseEntity.ok(order);
    }
}


