package com.github.miaoxinguo.mybatis.plugin.dialect;

import com.github.miaoxinguo.mybatis.plugin.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mysql 方言
 */
public class MySqlDialect implements Dialect {

    private static final Logger logger = LoggerFactory.getLogger(MySqlDialect.class);

    @Override
    public String getPagedSql(String originalSql, int offset, int limit) {
        String sql = originalSql.toLowerCase();
        if (sql.contains("limit")) {
            logger.debug("original sql contain 'limit', didn't build page sql again");
            return originalSql;
        }

        StringBuilder builder = new StringBuilder(sql);  // default length is sql.length + 16
        builder.append(" limit ");
        if (offset > 0) {
            builder.append(offset).append(",").append(limit);
        } else {
            builder.append(limit);
        }
        return builder.toString();
    }

    /**
     * 得到查询总数的sql
     */
    @Override
    public String getCountSql(String originalSql) {
        int orderIndex = SqlUtils.getLastOrderInsertPoint(originalSql);
        int fromIndex = SqlUtils.getAfterFromInsertPoint(originalSql);
        String countSql = originalSql.substring(0, fromIndex);

        // 如果SELECT 中包含 DISTINCT 只能在外层包含COUNT
        if (countSql.toLowerCase().contains("select distinct") || originalSql.toLowerCase().contains("group by")) {
            return "select count(*) from (" + originalSql.substring(0, orderIndex) + " ) t";
        } else {
            return "select count(*) " + originalSql.substring(fromIndex, orderIndex);
        }
    }

}
