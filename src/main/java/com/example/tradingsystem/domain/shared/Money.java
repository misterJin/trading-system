package com.example.tradingsystem.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 值对象：金额
 * 
 * <p>不可变对象，封装金额相关的业务规则：
 * <ul>
 *   <li>自动处理精度（保留2位小数）</li>
 *   <li>提供安全的金额计算操作</li>
 *   <li>提供金额比较方法</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * Money price = Money.of("100.50");
 * Money total = price.multiply(2); // 201.00
 * </pre>
 */
public class Money {
    
    /** 金额精度：保留2位小数 */
    private static final int SCALE = 2;
    /** 舍入模式：四舍五入 */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    /** 金额值 */
    private BigDecimal amount;
    
    /**
     * 默认构造函数（MyBatis Plus需要）
     */
    protected Money() {
    }
    
    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.scale() > SCALE) {
            this.amount = amount.setScale(SCALE, ROUNDING_MODE);
        } else {
            this.amount = amount;
        }
    }
    
    /**
     * 从BigDecimal创建Money对象
     * 
     * @param amount 金额
     * @return Money对象
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }
    
    /**
     * 从字符串创建Money对象
     * 
     * @param amount 金额字符串
     * @return Money对象
     */
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }
    
    /**
     * 创建零金额对象
     * 
     * @return 零金额Money对象
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
    
    /**
     * 金额相加
     * 
     * @param other 另一个金额
     * @return 新的Money对象（不可变）
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
    
    /**
     * 金额相减
     * 
     * @param other 另一个金额
     * @return 新的Money对象（不可变）
     */
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }
    
    /**
     * 金额乘以倍数
     * 
     * @param multiplier 倍数
     * @return 新的Money对象（不可变）
     */
    public Money multiply(long multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }
    
    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }
    
    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }
    
    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }
    
    public boolean isPositive() {
        return this.amount.signum() > 0;
    }
    
    public boolean isZero() {
        return this.amount.signum() == 0;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
    
    @Override
    public String toString() {
        return amount.toString();
    }
}

