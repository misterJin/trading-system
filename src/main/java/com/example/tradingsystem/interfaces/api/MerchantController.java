package com.example.tradingsystem.interfaces.api;

import com.example.tradingsystem.application.InventoryService;
import com.example.tradingsystem.domain.product.Product;
import com.example.tradingsystem.interfaces.api.dto.StockRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private final InventoryService inventoryService;

    public MerchantController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{merchantName}/products/{sku}/stock")
    public ResponseEntity<Product> addStock(@PathVariable String merchantName,
                                            @PathVariable String sku,
                                            @Valid @RequestBody StockRequest request) {
        Product product = inventoryService.addOrUpdateProductStock(
                merchantName,
                sku,
                request.getName(),
                request.getPrice(),
                request.getQuantity()
        );
        return ResponseEntity.ok(product);
    }
}


