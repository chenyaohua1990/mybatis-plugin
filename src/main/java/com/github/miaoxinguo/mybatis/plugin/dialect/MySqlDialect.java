package com.github.miaoxinguo.mybatis.plugin.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mysql 方言
 */
public class MySqlDialect implements Dialect {

    private static final Logger logger = LoggerFactory.getLogger(MySqlDialect.class);

    @Override
    public String getPagedSql(String sql, int offset, int limit) {
        if (sql.contains("limit")) {
            logger.debug("original sql contain 'limit', didn't build page sql again");
            return sql;
        }
        sql = this.getLineSql(sql);
        if (sql.contains("select count")) {
            logger.debug("original sql contain 'select count', didn't build page sql");
            return sql;
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
        originalSql = this.getLineSql(originalSql);
        int orderIndex = this.getLastOrderInsertPoint(originalSql);
        int fromIndex = this.getAfterFromInsertPoint(originalSql);
        String countSql = originalSql.substring(0, fromIndex);

        // 如果SELECT 中包含 DISTINCT 只能在外层包含COUNT
        if (countSql.toLowerCase().contains("select distinct") || originalSql.toLowerCase().contains("group by")) {
            return "select count(*) from (" + originalSql.substring(0, orderIndex) + " ) t";
        } else {
            return "select count(*) " + originalSql.substring(fromIndex, orderIndex);
        }
    }

    /**
     * 得到最后一个Order By的插入点位置
     *
     * @return 返回最后一个Order By插入点的位置
     */
    private int getLastOrderInsertPoint(String sql) {
        int orderIndex = sql.toLowerCase().lastIndexOf("order by");
        if (orderIndex == -1) {
            throw new RuntimeException("Mysql pagination sql must contain order by");
        }
        return orderIndex;
    }

    /**
     * 得到SQL第一个正确的FROM的的插入点
     */
    private int getAfterFromInsertPoint(String sql) {
        String regex = "\\s+FROM\\s+";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            int fromStartIndex = matcher.start(0);
            String text = sql.substring(0, fromStartIndex);
            if (isBracketCanPartnership(text)) {
                return fromStartIndex;
            }
        }
        return 0;
    }

    /**
     * 判断括号"()"是否匹配,并不会判断排列顺序是否正确
     *
     * @param text 要判断的文本
     * @return 如果匹配返回 true, 否则返回 false
     */
    private boolean isBracketCanPartnership(String text) {
        return text == null || getIndexOfCount(text, '(') == getIndexOfCount(text, ')');
    }

    /**
     * 得到一个字符在另一个字符串中出现的次数
     *
     * @param text 文本
     * @param ch   字符
     */
    private int getIndexOfCount(String text, char ch) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            count = (text.charAt(i) == ch) ? count + 1 : count;
        }
        return count;
    }
}
