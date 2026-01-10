package com.example.tradingsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tradingsystem.domain.user.UserAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账户Mapper
 * 
 * <p>提供用户账户的数据库操作：
 * <ul>
 *   <li>继承BaseMapper，自动提供基本的CRUD操作</li>
 *   <li>提供根据用户名查询的方法（通过Service层使用LambdaQueryWrapper实现）</li>
 * </ul>
 */
@Mapper
public interface UserAccountRepository extends BaseMapper<UserAccount> {
}


