package com.github.miaoxinguo.mybatis.plugin.typehandler;

import com.github.miaoxinguo.mybatis.plugin.EnumInterface;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 需要在配置文件为每一个枚举指定此 TypeHandler
 */
public class CustomerEnumTypeHandler extends BaseTypeHandler<EnumInterface> {

    private Class<EnumInterface> type;
    private final EnumInterface[] enums;   // 也可以用 Map<code, EnumInterface> 存储， hash运算 vs 数组遍历

    public CustomerEnumTypeHandler(Class<EnumInterface> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EnumInterface parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public EnumInterface getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : this.convertFromCode(code);
    }

    @Override
    public EnumInterface getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : this.convertFromCode(code);
    }

    @Override
    public EnumInterface getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : this.convertFromCode(code);
    }

    /**
     * 根据 code 获取枚举项
     */
    private EnumInterface convertFromCode(int code) {
        for (EnumInterface e : enums) {
            if (e.getCode() == code) {
                return e;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + code + " to " + type.getSimpleName() + " by code.");
    }

}

