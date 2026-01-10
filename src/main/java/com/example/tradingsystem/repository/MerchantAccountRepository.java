package com.example.tradingsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tradingsystem.domain.merchant.MerchantAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家账户Mapper
 * 
 * <p>提供商家账户的数据库操作：
 * <ul>
 *   <li>继承BaseMapper，自动提供基本的CRUD操作</li>
 *   <li>提供根据商家名称查询的方法（通过Service层使用LambdaQueryWrapper实现）</li>
 * </ul>
 */
@Mapper
public interface MerchantAccountRepository extends BaseMapper<MerchantAccount> {
}


