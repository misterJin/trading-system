package com.example.tradingsystem.domain.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.example.tradingsystem.domain.merchant.MerchantAccount;
import com.example.tradingsystem.domain.shared.AggregateRoot;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.domain.shared.Quantity;
import com.example.tradingsystem.infrastructure.mybatis.MoneyTypeHandler;
import com.example.tradingsystem.infrastructure.mybatis.QuantityTypeHandler;

import java.math.BigDecimal;

/**
 * 商品聚合根
 * 
 * <p>负责管理商品的库存和销售信息，提供以下功能：
 * <ul>
 *   <li>商品信息管理：SKU、名称、价格</li>
 *   <li>库存管理：增加库存、扣减库存</li>
 *   <li>销售统计：记录已售数量</li>
 * </ul>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>SKU必须唯一</li>
 *   <li>价格必须大于0</li>
 *   <li>库存不能为负</li>
 *   <li>销售时库存必须足够</li>
 *   <li>使用乐观锁防止并发问题</li>
 * </ul>
 */
@TableName(value = "products", autoResultMap = true)
public class Product implements AggregateRoot {

    /** 主键ID（数据库自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品SKU（唯一标识） */
    private String sku;

    /** 商品名称 */
    private String name;

    /** 商品价格（值对象：Money） */
    @TableField(value = "price", typeHandler = MoneyTypeHandler.class)
    private Money price;

    /** 商家ID（外键） */
    private Long merchantId;

    /** 商家对象（业务逻辑使用，不持久化） */
    @TableField(exist = false)
    private MerchantAccount merchant;

    /** 库存数量（值对象：Quantity） */
    @TableField(value = "stock_quantity", typeHandler = QuantityTypeHandler.class)
    private Quantity stockQuantity;

    /** 已售数量（值对象：Quantity） */
    @TableField(value = "sold_quantity", typeHandler = QuantityTypeHandler.class)
    private Quantity soldQuantity;

    /** 乐观锁版本号（用于并发控制） */
    @Version
    private Long version;

    /**
     * 默认构造函数（MyBatis Plus需要）
     */
    protected Product() {
    }

    /**
     * 创建商品
     * 
     * @param sku 商品SKU（不能为空）
     * @param name 商品名称（不能为空）
     * @param price 商品价格（必须大于0）
     * @param merchant 商家对象（不能为空）
     */
    public Product(String sku, String name, Money price, MerchantAccount merchant) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (price == null || !price.isPositive()) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant cannot be null");
        }
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.merchant = merchant;
        this.merchantId = merchant.getId();
        this.stockQuantity = Quantity.zero();
        this.soldQuantity = Quantity.zero();
        // 乐观锁字段初始化，避免首次 updateById 时 version 为 null 导致匹配失败
        this.version = 0L;
    }

    /**
     * 创建商品（使用BigDecimal价格）
     * 
     * @param sku 商品SKU
     * @param name 商品名称
     * @param price 商品价格（BigDecimal类型）
     * @param merchant 商家对象
     */
    public Product(String sku, String name, BigDecimal price, MerchantAccount merchant) {
        this(sku, name, Money.of(price), merchant);
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
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

    public Quantity getStockQuantity() {
        return stockQuantity;
    }

    public Quantity getSoldQuantity() {
        return soldQuantity;
    }

    /**
     * 增加库存
     * 
     * <p>业务规则：
     * <ul>
     *   <li>增加数量必须大于0</li>
     *   <li>增加后库存 = 原库存 + 增加数量</li>
     * </ul>
     * 
     * @param quantity 增加的数量（必须大于0）
     * @throws IllegalArgumentException 如果数量无效
     */
    public void addStock(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        this.stockQuantity = this.stockQuantity.add(quantity);
    }

    /**
     * 销售商品（扣减库存，增加已售数量）
     * 
     * <p>业务规则：
     * <ul>
     *   <li>销售数量必须大于0</li>
     *   <li>库存必须足够（不能为负）</li>
     *   <li>销售后库存 = 原库存 - 销售数量</li>
     *   <li>销售后已售数量 = 原已售数量 + 销售数量</li>
     * </ul>
     * 
     * @param quantity 销售数量（必须大于0）
     * @throws IllegalArgumentException 如果数量无效
     * @throws IllegalStateException 如果库存不足
     */
    public void sell(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (this.stockQuantity.isLessThan(quantity)) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stockQuantity = this.stockQuantity.subtract(quantity);
        this.soldQuantity = this.soldQuantity.add(quantity);
    }
}


