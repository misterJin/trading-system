package com.example.tradingsystem.domain.merchant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.example.tradingsystem.domain.shared.AggregateRoot;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.infrastructure.mybatis.MoneyTypeHandler;

/**
 * 商家账户聚合根
 * 
 * <p>负责管理商家的现金账户，提供以下功能：
 * <ul>
 *   <li>账户入账：用户下单后，商家账户增加对应金额</li>
 *   <li>账户出账：商家提现或退款时扣款</li>
 *   <li>余额查询：查询商家当前账户余额</li>
 * </ul>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>商家名称必须唯一</li>
 *   <li>余额不能为负</li>
 *   <li>使用乐观锁防止并发问题</li>
 * </ul>
 */
@TableName(value = "merchant_accounts", autoResultMap = true)
public class MerchantAccount implements AggregateRoot {

    /** 主键ID（数据库自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商家名称（唯一） */
    private String name;

    /** 账户余额（值对象：Money） */
    @TableField(value = "balance", typeHandler = MoneyTypeHandler.class)
    private Money balance;

    /** 乐观锁版本号（用于并发控制） */
    @Version
    private Long version;

    /**
     * 默认构造函数（MyBatis Plus需要）
     */
    protected MerchantAccount() {
    }

    public MerchantAccount(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Merchant name cannot be null or blank");
        }
        this.name = name;
        this.balance = Money.zero();
        // 乐观锁字段初始化，避免首次 updateById 时 version 为 null 导致匹配失败
        this.version = 0L;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getBalance() {
        return balance;
    }

    /**
     * 账户入账（收款）
     * 
     * <p>业务规则：
     * <ul>
     *   <li>入账金额必须大于0</li>
     *   <li>入账后余额 = 原余额 + 入账金额</li>
     * </ul>
     * 
     * @param amount 入账金额（必须大于0）
     * @throws IllegalArgumentException 如果金额无效
     */
    public void credit(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * 账户出账（扣款）
     * 
     * <p>业务规则：
     * <ul>
     *   <li>出账金额必须大于0</li>
     *   <li>余额必须足够（不能为负）</li>
     *   <li>出账后余额 = 原余额 - 出账金额</li>
     * </ul>
     * 
     * @param amount 出账金额（必须大于0）
     * @throws IllegalArgumentException 如果金额无效
     * @throws IllegalStateException 如果余额不足
     */
    public void debit(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (this.balance.isLessThan(amount)) {
            throw new IllegalStateException("Insufficient merchant balance");
        }
        this.balance = this.balance.subtract(amount);
    }
}


