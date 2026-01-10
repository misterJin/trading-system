package com.example.tradingsystem.application;

import com.example.tradingsystem.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Test
    void addStock() {
        Product p = inventoryService.addOrUpdateProductStock("m1", "sku-2", "Item", new BigDecimal("5.00"), 5);
        assertThat(p.getStockQuantity().getValue()).isEqualTo(5L);
        assertThat(p.getSoldQuantity().getValue()).isZero();
    }
}


