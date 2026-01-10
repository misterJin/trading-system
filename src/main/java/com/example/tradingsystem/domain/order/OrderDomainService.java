package com.example.tradingsystem.domain.order;

import com.example.tradingsystem.domain.merchant.MerchantAccount;
import com.example.tradingsystem.domain.product.Product;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.domain.shared.Quantity;
import com.example.tradingsystem.domain.user.UserAccount;
import org.springframework.stereotype.Service;

/**
 * 订单领域服务
 * 处理跨聚合的业务逻辑，协调多个聚合根之间的交互
 */
@Service
public class OrderDomainService {
    
    /**
     * 执行订单交易
     * 协调用户账户、商家账户、商品库存三个聚合的交互
     * 
     * @param order 订单聚合根
     * @param user 用户账户聚合根
     * @param merchant 商家账户聚合根
     * @param product 商品聚合根
     */
    public void executeOrder(Order order, UserAccount user, MerchantAccount merchant, Product product) {
        // 1. 验证库存
        Quantity orderQuantity = order.getQuantity();
        if (product.getStockQuantity().isLessThan(orderQuantity)) {
            throw new IllegalStateException("Insufficient stock");
        }
        
        // 2. 验证用户余额
        Money totalPrice = order.getTotalPrice();
        if (user.getBalance().isLessThan(totalPrice)) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        // 3. 执行交易：扣库存、扣用户余额、加商家余额
        product.sell(orderQuantity);
        user.withdraw(totalPrice);
        merchant.credit(totalPrice);
        
        // 4. 标记订单完成
        order.markCompleted();
    }
}

