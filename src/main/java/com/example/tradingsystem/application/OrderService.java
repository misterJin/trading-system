package com.example.tradingsystem.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.tradingsystem.domain.merchant.MerchantAccount;
import com.example.tradingsystem.domain.order.Order;
import com.example.tradingsystem.domain.order.OrderCompletedEvent;
import com.example.tradingsystem.domain.order.OrderDomainService;
import com.example.tradingsystem.domain.order.OrderPlacedEvent;
import com.example.tradingsystem.domain.product.Product;
import com.example.tradingsystem.domain.shared.DomainEventPublisher;
import com.example.tradingsystem.domain.shared.Quantity;
import com.example.tradingsystem.domain.user.UserAccount;
import com.example.tradingsystem.repository.MerchantAccountRepository;
import com.example.tradingsystem.repository.OrderRepository;
import com.example.tradingsystem.repository.ProductRepository;
import com.example.tradingsystem.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单应用服务
 * 
 * <p>协调领域服务和仓储，处理应用层的业务流程：
 * <ul>
 *   <li>下单购买：用户下单购买商品</li>
 *   <li>订单查询：根据ID查询订单详情</li>
 * </ul>
 */
@Service
public class OrderService {

    private final UserAccountRepository userAccountRepository;
    private final MerchantAccountRepository merchantAccountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final DomainEventPublisher domainEventPublisher;

    public OrderService(UserAccountRepository userAccountRepository,
                        MerchantAccountRepository merchantAccountRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        OrderDomainService orderDomainService,
                        DomainEventPublisher domainEventPublisher) {
        this.userAccountRepository = userAccountRepository;
        this.merchantAccountRepository = merchantAccountRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * 下单购买商品
     * 
     * <p>业务流程：
     * <ol>
     *   <li>加载用户、商品、商家聚合根</li>
     *   <li>创建订单聚合根并保存（获取ID）</li>
     *   <li>发布订单创建事件</li>
     *   <li>使用领域服务执行订单交易（扣库存、扣用户余额、加商家余额）</li>
     *   <li>保存所有聚合根状态</li>
     *   <li>发布订单完成事件</li>
     * </ol>
     * 
     * @param username 用户名
     * @param sku 商品SKU
     * @param quantity 购买数量
     * @return 订单对象
     * @throws ResourceNotFoundException 如果用户或商品不存在
     * @throws IllegalStateException 如果库存不足或余额不足
     */
    @Transactional
    public Order placeOrder(String username, String sku, long quantity) {
        // 1. 加载聚合根
        UserAccount user = userAccountRepository.selectOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + username);
        }
        
        Product product = productRepository.selectOne(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getSku, sku)
        );
        if (product == null) {
            throw new ResourceNotFoundException("Product not found: " + sku);
        }
        
        // 加载商家
        MerchantAccount merchant = merchantAccountRepository.selectById(product.getMerchantId());
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant not found: " + product.getMerchantId());
        }
        
        // 设置关联对象（用于业务逻辑）
        product.setMerchant(merchant);

        // 2. 创建订单聚合根
        Order order = new Order(user, merchant, product, Quantity.of(quantity));
        orderRepository.insert(order); // 先保存以获取ID

        // 3. 发布订单创建事件
        domainEventPublisher.publish(new OrderPlacedEvent(
                order.getId(),
                user.getUsername(),
                merchant.getName(),
                product.getSku(),
                order.getQuantity().getValue(),
                order.getTotalPrice()
        ));

        try {
            // 4. 使用领域服务执行订单交易（跨聚合协调）
            orderDomainService.executeOrder(order, user, merchant, product);

            // 5. 保存聚合根状态
            productRepository.updateById(product);
            userAccountRepository.updateById(user);
            merchantAccountRepository.updateById(merchant);
            orderRepository.updateById(order);

            // 6. 发布订单完成事件
            domainEventPublisher.publish(new OrderCompletedEvent(
                    order.getId(),
                    user.getUsername(),
                    merchant.getName(),
                    product.getSku(),
                    order.getQuantity().getValue(),
                    order.getTotalPrice()
            ));

            return order;
        } catch (RuntimeException ex) {
            order.markFailed();
            orderRepository.updateById(order);
            throw ex;
        }
    }

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单对象
     * @throws ResourceNotFoundException 如果订单不存在
     */
    public Order findById(Long id) {
        Order order = orderRepository.selectById(id);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found: " + id);
        }
        return order;
    }
}


