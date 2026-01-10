package com.example.tradingsystem.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus配置类
 * 
 * <p>配置MyBatis Plus的相关设置：
 * <ul>
 *   <li>扫描Mapper接口</li>
 *   <li>其余尽量使用默认配置，避免冗余</li>
 * </ul>
 */
@Configuration
@MapperScan("com.example.tradingsystem.repository")
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus 插件配置。
     *
     * <p>当前只启用乐观锁插件（配合实体上的 {@code @Version} 字段），其余保持默认，避免不必要的复杂度。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}

