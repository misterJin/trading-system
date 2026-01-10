package com.example.tradingsystem.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.tradingsystem.domain.shared.Money;
import com.example.tradingsystem.domain.user.UserAccount;
import com.example.tradingsystem.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户账户应用服务
 * 
 * <p>提供用户账户相关的业务操作：
 * <ul>
 *   <li>账户充值：用户向账户中充值</li>
 *   <li>账户查询：根据用户名查询或创建账户</li>
 * </ul>
 */
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * 用户账户充值
     * 
     * <p>业务逻辑：
     * <ol>
     *   <li>根据用户名查询账户，如果不存在则创建新账户</li>
     *   <li>调用账户的deposit方法进行充值</li>
     *   <li>保存账户信息到数据库</li>
     * </ol>
     * 
     * @param username 用户名
     * @param amount 充值金额
     * @return 更新后的用户账户
     */
    @Transactional
    public UserAccount deposit(String username, BigDecimal amount) {
        Money depositAmount = Money.of(amount);
        
        // 使用MyBatis Plus的LambdaQueryWrapper查询
        UserAccount account = userAccountRepository.selectOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );
        
        // 如果账户不存在，创建新账户
        if (account == null) {
            account = new UserAccount(username, Money.zero());
            userAccountRepository.insert(account);
        }
        
        // 充值
        account.deposit(depositAmount);
        
        // 更新账户
        userAccountRepository.updateById(account);
        
        return account;
    }

    /**
     * 获取或创建用户账户
     * 
     * <p>如果账户不存在，则创建新账户并保存到数据库
     * 
     * @param username 用户名
     * @return 用户账户
     */
    public UserAccount getOrCreate(String username) {
        // 查询账户
        UserAccount account = userAccountRepository.selectOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );
        
        // 如果不存在，创建新账户
        if (account == null) {
            account = new UserAccount(username, Money.zero());
            userAccountRepository.insert(account);
        }
        
        return account;
    }
}


