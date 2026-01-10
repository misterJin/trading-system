package com.example.tradingsystem.application;

import com.example.tradingsystem.domain.merchant.MerchantAccount;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.repository.MerchantAccountRepository;
import com.example.tradingsystem.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 结算应用服务
 * 
 * <p>提供商家结算相关的业务操作：
 * <ul>
 *   <li>每日结算：对每个商家进行结算，验证库存中卖出的商品价值和商家账户余额是否匹配</li>
 *   <li>结算结果：返回每个商家的预期金额、实际金额和差额</li>
 * </ul>
 * 
 * <p>结算逻辑：
 * <ul>
 *   <li>预期金额 = 所有商品的（单价 × 已售数量）之和</li>
 *   <li>实际金额 = 商家账户余额</li>
 *   <li>差额 = 实际金额 - 预期金额（应该为0，表示账目平衡）</li>
 * </ul>
 */
@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    private final MerchantAccountRepository merchantAccountRepository;
    private final ProductRepository productRepository;

    public SettlementService(MerchantAccountRepository merchantAccountRepository,
                             ProductRepository productRepository) {
        this.merchantAccountRepository = merchantAccountRepository;
        this.productRepository = productRepository;
    }

    /**
     * 执行结算
     * 
     * <p>对每个商家进行结算，验证库存中卖出的商品价值和商家账户余额是否匹配
     * 
     * @return 结算结果列表
     */
    @Transactional(readOnly = true)
    public List<SettlementResult> settle() {
        List<SettlementResult> results = new ArrayList<>();
        
        // 查询所有商家
        List<MerchantAccount> merchants = merchantAccountRepository.selectList(null);
        
        // 查询所有商品
        List<com.example.tradingsystem.domain.product.Product> products = productRepository.selectList(null);
        
        // 对每个商家进行结算
        for (MerchantAccount merchant : merchants) {
            // 计算预期金额：该商家所有商品的（单价 × 已售数量）之和
            Money expected = products.stream()
                    .filter(p -> merchant.getId().equals(p.getMerchantId()))
                    .map(p -> p.getPrice().multiply(p.getSoldQuantity().getValue()))
                    .reduce(Money.zero(), Money::add);
            
            // 实际金额：商家账户余额
            Money actual = merchant.getBalance();
            
            // 计算差额
            Money diff = actual.subtract(expected);
            
            SettlementResult result = new SettlementResult(merchant.getName(), expected, actual, diff);
            results.add(result);
            
            log.info("Settlement result for merchant {}: expected={}, actual={}, diff={}",
                    merchant.getName(), expected, actual, diff);
        }
        
        return results;
    }

    /**
     * 结算结果
     * 
     * @param merchantName 商家名称
     * @param expected 预期金额（根据已售商品计算）
     * @param actual 实际金额（商家账户余额）
     * @param diff 差额（实际 - 预期，应该为0）
     */
    public record SettlementResult(String merchantName, Money expected, Money actual, Money diff) {
    }
}


