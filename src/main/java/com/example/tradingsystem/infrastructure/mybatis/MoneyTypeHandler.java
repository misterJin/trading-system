package com.example.tradingsystem.infrastructure.mybatis;

import com.example.tradingsystem.domain.shared.Money;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Money值对象的TypeHandler
 * 
 * <p>用于MyBatis Plus将Money值对象与数据库的DECIMAL类型进行转换：
 * <ul>
 *   <li>写入数据库：将Money对象转换为BigDecimal</li>
 *   <li>从数据库读取：将BigDecimal转换为Money对象</li>
 * </ul>
 */
@MappedTypes(Money.class)
@MappedJdbcTypes(JdbcType.DECIMAL)
public class MoneyTypeHandler extends BaseTypeHandler<Money> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Money parameter, JdbcType jdbcType) throws SQLException {
        ps.setBigDecimal(i, parameter.getAmount());
    }

    @Override
    public Money getNullableResult(ResultSet rs, String columnName) throws SQLException {
        BigDecimal amount = rs.getBigDecimal(columnName);
        return amount == null ? null : Money.of(amount);
    }

    @Override
    public Money getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        BigDecimal amount = rs.getBigDecimal(columnIndex);
        return amount == null ? null : Money.of(amount);
    }

    @Override
    public Money getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        BigDecimal amount = cs.getBigDecimal(columnIndex);
        return amount == null ? null : Money.of(amount);
    }
}

