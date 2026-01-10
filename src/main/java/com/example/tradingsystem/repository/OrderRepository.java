package com.example.tradingsystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tradingsystem.domain.order.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 * 
 * <p>提供订单的数据库操作：
 * <ul>
 *   <li>继承BaseMapper，自动提供基本的CRUD操作</li>
 *   <li>支持根据ID查询、保存、更新、删除等操作</li>
 * </ul>
 */
@Mapper
public interface OrderRepository extends BaseMapper<Order> {
}


