package com.example.tradingsystem.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.example.tradingsystem.domain.shared.AggregateRoot;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.infrastructure.mybatis.MoneyTypeHandler;

import java.math.BigDecimal;

/**
 * 用户账户聚合根
 * 
 * <p>负责管理用户的预存现金账户，提供以下功能：
 * <ul>
 *   <li>账户充值：用户可以向账户中充值</li>
 *   <li>账户扣款：下单时从账户中扣款</li>
 *   <li>余额查询：查询当前账户余额</li>
 * </ul>
 * 
 * <p>业务规则：
 * <ul>
 *   <li>用户名必须唯一</li>
 *   <li>余额不能为负</li>
 *   <li>使用乐观锁防止并发问题</li>
 * </ul>
 */
@TableName(value = "user_accounts", autoResultMap = true)
public class UserAccount implements AggregateRoot {

    /** 主键ID（数据库自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /** 账户余额（值对象：Money） */
    @TableField(value = "balance", typeHandler = MoneyTypeHandler.class)
    private Money balance;

    /** 乐观锁版本号（用于并发控制） */
    @Version
    private Long version;

    /**
     * 默认构造函数（MyBatis Plus需要）
     */
    protected UserAccount() {
    }

    /**
     * 创建用户账户
     * 
     * @param username 用户名（不能为空）
     * @param initialBalance 初始余额（如果为null则默认为0）
     */
    public UserAccount(String username, Money initialBalance) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        this.username = username;
        this.balance = initialBalance == null ? Money.zero() : initialBalance;
        // 乐观锁字段初始化，避免首次 updateById 时 version 为 null 导致匹配失败
        this.version = 0L;
    }

    /**
     * 创建用户账户（使用BigDecimal）
     * 
     * @param username 用户名
     * @param initialBalance 初始余额（BigDecimal类型）
     */
    public UserAccount(String username, BigDecimal initialBalance) {
        this(username, initialBalance == null ? Money.zero() : Money.of(initialBalance));
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Money getBalance() {
        return balance;
    }

    /**
     * 账户充值
     * 
     * <p>业务规则：
     * <ul>
     *   <li>充值金额必须大于0</li>
     *   <li>充值后余额 = 原余额 + 充值金额</li>
     * </ul>
     * 
     * @param amount 充值金额（必须大于0）
     * @throws IllegalArgumentException 如果金额无效
     */
    public void deposit(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * 账户扣款（提现）
     * 
     * <p>业务规则：
     * <ul>
     *   <li>扣款金额必须大于0</li>
     *   <li>余额必须足够（不能为负）</li>
     *   <li>扣款后余额 = 原余额 - 扣款金额</li>
     * </ul>
     * 
     * @param amount 扣款金额（必须大于0）
     * @throws IllegalArgumentException 如果金额无效
     * @throws IllegalStateException 如果余额不足
     */
    public void withdraw(Money amount) {
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (this.balance.isLessThan(amount)) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }
}


