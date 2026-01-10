package com.example.tradingsystem.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.tradingsystem.domain.order.Order;
import com.example.tradingsystem.domain.order.OrderStatus;
import com.example.tradingsystem.domain.product.Product;
import com.example.tradingsystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    private static final String USER = "u1";
    private static final String MERCHANT = "m1";
    private static final String SKU = "sku-1";

    @BeforeEach
    void setup() {
        userAccountService.deposit(USER, new BigDecimal("100.00"));
        inventoryService.addOrUpdateProductStock(MERCHANT, SKU, "Prod", new BigDecimal("10.00"), 10);
    }

    @Test
    void placeOrder() {
        Order order = orderService.placeOrder(USER, SKU, 2);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.getTotalPrice().getAmount()).isEqualByComparingTo("20.00");

        Product product = productRepository.selectOne(
                new LambdaQueryWrapper<Product>().eq(Product::getSku, SKU)
        );
        assertThat(product).isNotNull();
        assertThat(product.getSoldQuantity().getValue()).isEqualTo(2L);
        assertThat(product.getStockQuantity().getValue()).isEqualTo(8L);
    }
}


