package com.example.tradingsystem.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.tradingsystem.domain.merchant.MerchantAccount;
import com.example.tradingsystem.domain.product.Product;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.domain.shared.Quantity;
import com.example.tradingsystem.repository.MerchantAccountRepository;
import com.example.tradingsystem.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 库存应用服务
 * 
 * <p>提供商品库存相关的业务操作：
 * <ul>
 *   <li>商品库存管理：添加或更新商品库存</li>
 *   <li>商品创建：如果商品不存在，自动创建新商品</li>
 *   <li>商家管理：如果商家不存在，自动创建新商家</li>
 * </ul>
 */
@Service
public class InventoryService {

    private final MerchantAccountRepository merchantAccountRepository;
    private final ProductRepository productRepository;

    public InventoryService(MerchantAccountRepository merchantAccountRepository, ProductRepository productRepository) {
        this.merchantAccountRepository = merchantAccountRepository;
        this.productRepository = productRepository;
    }

    /**
     * 添加或更新商品库存
     * 
     * <p>业务逻辑：
     * <ol>
     *   <li>如果商家不存在，创建新商家</li>
     *   <li>如果商品不存在，创建新商品</li>
     *   <li>如果商品已存在，验证商家是否匹配</li>
     *   <li>增加商品库存</li>
     * </ol>
     * 
     * @param merchantName 商家名称
     * @param sku 商品SKU
     * @param name 商品名称
     * @param price 商品价格
     * @param quantity 增加的数量
     * @return 更新后的商品对象
     * @throws BusinessException 如果商品属于其他商家
     */
    @Transactional
    public Product addOrUpdateProductStock(String merchantName,
                                           String sku,
                                           String name,
                                           BigDecimal price,
                                           long quantity) {
        Money productPrice = Money.of(price);
        Quantity stockQuantity = Quantity.of(quantity);

        // 查询或创建商家
        MerchantAccount merchant = merchantAccountRepository.selectOne(
                new LambdaQueryWrapper<MerchantAccount>()
                        .eq(MerchantAccount::getName, merchantName)
        );
        
        if (merchant == null) {
            merchant = new MerchantAccount(merchantName);
            merchantAccountRepository.insert(merchant);
        }

        // 查询商品
        Product product = productRepository.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getSku, sku)
        );
        
        if (product == null) {
            // 创建新商品
            product = new Product(sku, name, productPrice, merchant);
            productRepository.insert(product);
        } else {
            // 验证商家是否匹配
            ensureSameMerchant(merchant, product);
            // 设置商家对象（用于业务逻辑）
            product.setMerchant(merchant);
        }

        // 增加库存
        product.addStock(stockQuantity);
        productRepository.updateById(product);
        
        return product;
    }

    /**
     * 确保商品属于指定商家
     * 
     * @param merchant 商家对象
     * @param product 商品对象
     * @throws BusinessException 如果商品属于其他商家
     */
    private Product ensureSameMerchant(MerchantAccount merchant, Product product) {
        Long productMerchantId = product.getMerchantId();
        Long currentMerchantId = merchant.getId();
        if (productMerchantId != null && currentMerchantId != null && !productMerchantId.equals(currentMerchantId)) {
            throw new BusinessException("Product belongs to another merchant");
        }
        return product;
    }
}


