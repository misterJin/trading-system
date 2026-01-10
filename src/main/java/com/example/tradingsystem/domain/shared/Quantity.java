package com.example.tradingsystem.domain.shared;

import java.util.Objects;

/**
 * 值对象：数量
 * 
 * <p>不可变对象，封装数量相关的业务规则：
 * <ul>
 *   <li>确保数量非负</li>
 *   <li>提供安全的数量计算操作</li>
 *   <li>提供数量比较方法</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>
 * Quantity qty = Quantity.of(10);
 * Quantity result = qty.add(Quantity.of(5)); // 15
 * </pre>
 */
public class Quantity {
    
    /** 数量值 */
    private Long value;
    
    /**
     * 默认构造函数（MyBatis Plus需要）
     */
    protected Quantity() {
    }
    
    private Quantity(Long value) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.value = value;
    }
    
    public static Quantity of(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return new Quantity(value);
    }
    
    public static Quantity zero() {
        return new Quantity(0L);
    }
    
    public static Quantity ofNonNegative(long value) {
        return new Quantity(value);
    }
    
    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }
    
    public Quantity subtract(Quantity other) {
        long result = this.value - other.value;
        if (result <= 0) {
            throw new IllegalArgumentException("Result quantity must be positive");
        }
        return new Quantity(result);
    }
    
    public boolean isGreaterThan(Quantity other) {
        return this.value > other.value;
    }
    
    public boolean isGreaterThanOrEqual(Quantity other) {
        return this.value >= other.value;
    }
    
    public boolean isLessThan(Quantity other) {
        return this.value < other.value;
    }
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return Objects.equals(value, quantity.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

