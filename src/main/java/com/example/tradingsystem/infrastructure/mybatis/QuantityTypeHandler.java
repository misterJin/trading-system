package com.example.tradingsystem.infrastructure.mybatis;

import com.example.tradingsystem.domain.shared.Quantity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Quantity值对象的TypeHandler
 * 
 * <p>用于MyBatis Plus将Quantity值对象与数据库的BIGINT类型进行转换：
 * <ul>
 *   <li>写入数据库：将Quantity对象转换为Long</li>
 *   <li>从数据库读取：将Long转换为Quantity对象</li>
 * </ul>
 */
@MappedTypes(Quantity.class)
@MappedJdbcTypes(JdbcType.BIGINT)
public class QuantityTypeHandler extends BaseTypeHandler<Quantity> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Quantity parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.getValue());
    }

    @Override
    public Quantity getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Long value = rs.getLong(columnName);
        return value == null ? null : Quantity.ofNonNegative(value);
    }

    @Override
    public Quantity getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Long value = rs.getLong(columnIndex);
        return value == null ? null : Quantity.ofNonNegative(value);
    }

    @Override
    public Quantity getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Long value = cs.getLong(columnIndex);
        return value == null ? null : Quantity.ofNonNegative(value);
    }
}

