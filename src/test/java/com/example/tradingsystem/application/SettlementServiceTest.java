package com.example.tradingsystem.application;

import com.example.tradingsystem.application.SettlementService.SettlementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SettlementServiceTest {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SettlementService settlementService;

    private static final String USER = "u2";
    private static final String MERCHANT = "m2";
    private static final String SKU = "sku-3";

    @BeforeEach
    void setup() {
        userAccountService.deposit(USER, new BigDecimal("50.00"));
        inventoryService.addOrUpdateProductStock(MERCHANT, SKU, "Prod", new BigDecimal("5.00"), 5);
        orderService.placeOrder(USER, SKU, 2);
    }

    @Test
    void settlement() {
        List<SettlementResult> results = settlementService.settle();
        SettlementResult merchantResult = results.stream()
                .filter(r -> r.merchantName().equals(MERCHANT))
                .findFirst()
                .orElseThrow();
        assertThat(merchantResult.expected().getAmount()).isEqualByComparingTo("10.00");
        assertThat(merchantResult.actual().getAmount()).isEqualByComparingTo("10.00");
        assertThat(merchantResult.diff().getAmount()).isEqualByComparingTo("0.00");
    }
}


