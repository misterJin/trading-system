package com.example.tradingsystem.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.example.tradingsystem.domain.merchant.MerchantAccount;
import com.example.tradingsystem.domain.product.Product;
import com.example.tradingsystem.domain.shared.AggregateRoot;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.domain.shared.Quantity;
import com.example.tradingsystem.domain.user.UserAccount;
import com.example.tradingsystem.infrastructure.mybatis.MoneyTypeHandler;
import com.example.tradingsystem.infrastructure.mybatis.QuantityTypeHandler;

import java.time.Instant;

/**
 * 订单聚合根
 * 
 * <p>负责管理订单的生命周期和状态，提供以下功能：
 * <ul>
 *   <li>订单创建：创建新订单并计算总价</li>
 *   <li>订单状态管理：CREATED -> COMPLETED/FAILED</li>
 *   <li>订单信息查询：查询订单详情</li>
 * </ul>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>订单创建时自动计算总价（单价 × 数量）</li>
 *   <li>只有CREATED状态的订单可以标记为COMPLETED</li>
 *   <li>已完成的订单不能标记为FAILED</li>
 *   <li>使用乐观锁防止并发问题</li>
 * </ul>
 */
@TableName(value = "orders", autoResultMap = true)
public class Order implements AggregateRoot {

    /** 主键ID（数据库自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（外键） */
    private Long userId;

    /** 商家ID（外键） */
    private Long merchantId;

    /** 商品ID（外键） */
    private Long productId;

    /** 用户对象（业务逻辑使用，不持久化） */
    @TableField(exist = false)
    private UserAccount user;

    /** 商家对象（业务逻辑使用，不持久化） */
    @TableField(exist = false)
    private MerchantAccount merchant;

    /** 商品对象（业务逻辑使用，不持久化） */
    @TableField(exist = false)
    private Product product;

    /** 购买数量（值对象：Quantity） */
    @TableField(value = "quantity", typeHandler = QuantityTypeHandler.class)
    private Quantity quantity;

    /** 单价（值对象：Money） */
    @TableField(value = "unit_price", typeHandler = MoneyTypeHandler.class)
    private Money unitPrice;

    /** 总价（值对象：Money，单价 × 数量） */
    @TableField(value = "total_price", typeHandler = MoneyTypeHandler.class)
    private Money totalPrice;

    /** 订单状态（CREATED/COMPLETED/FAILED） */
    private OrderStatus status;

    /** 创建时间（不可修改） */
    private Instant createdAt;

    /** 乐观锁版本号（用于并发控制） */
    @Version
    private Long version;

    /**
     * 默认构造函数（MyBatis Plus需要）
     */
    protected Order() {
    }

    /**
     * 创建订单
     * 
     * <p>自动计算订单总价：总价 = 单价 × 数量
     * 
     * @param user 用户对象（不能为空）
     * @param merchant 商家对象（不能为空）
     * @param product 商品对象（不能为空）
     * @param quantity 购买数量（不能为空）
     * @throws IllegalArgumentException 如果参数无效
     */
    public Order(UserAccount user,
                 MerchantAccount merchant,
                 Product product,
                 Quantity quantity) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant cannot be null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        this.user = user;
        this.merchant = merchant;
        this.product = product;
        this.userId = user.getId();
        this.merchantId = merchant.getId();
        this.productId = product.getId();
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.totalPrice = this.unitPrice.multiply(quantity.getValue());
        this.status = OrderStatus.CREATED;
        this.createdAt = Instant.now();
        // 乐观锁字段初始化，避免首次 updateById 时 version 为 null 导致匹配失败
        this.version = 0L;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * 
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取商家ID
     * 
     * @return 商家ID
     */
    public Long getMerchantId() {
        return merchantId;
    }

    /**
     * 设置商家ID
     * 
     * @param merchantId 商家ID
     */
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * 获取商品ID
     * 
     * @return 商品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 设置商品ID
     * 
     * @param productId 商品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 获取用户对象
     * 
     * @return 用户对象（可能为null，需要从数据库加载）
     */
    public UserAccount getUser() {
        return user;
    }

    /**
     * 设置用户对象
     * 
     * @param user 用户对象
     */
    public void setUser(UserAccount user) {
        this.user = user;
        if (user != null && user.getId() != null) {
            this.userId = user.getId();
        }
    }

    /**
     * 获取商家对象
     * 
     * @return 商家对象（可能为null，需要从数据库加载）
     */
    public MerchantAccount getMerchant() {
        return merchant;
    }

    /**
     * 设置商家对象
     * 
     * @param merchant 商家对象
     */
    public void setMerchant(MerchantAccount merchant) {
        this.merchant = merchant;
        if (merchant != null && merchant.getId() != null) {
            this.merchantId = merchant.getId();
        }
    }

    /**
     * 获取商品对象
     * 
     * @return 商品对象（可能为null，需要从数据库加载）
     */
    public Product getProduct() {
        return product;
    }

    /**
     * 设置商品对象
     * 
     * @param product 商品对象
     */
    public void setProduct(Product product) {
        this.product = product;
        if (product != null && product.getId() != null) {
            this.productId = product.getId();
        }
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 标记订单完成
     * 
     * <p>业务规则：只有CREATED状态的订单可以标记为COMPLETED
     * 
     * @throws IllegalStateException 如果订单状态不是CREATED
     */
    public void markCompleted() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be marked as completed");
        }
        this.status = OrderStatus.COMPLETED;
    }

    /**
     * 标记订单失败
     * 
     * <p>业务规则：已完成的订单不能标记为FAILED
     * 
     * @throws IllegalStateException 如果订单状态是COMPLETED
     */
    public void markFailed() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Completed orders cannot be marked as failed");
        }
        this.status = OrderStatus.FAILED;
    }
}


