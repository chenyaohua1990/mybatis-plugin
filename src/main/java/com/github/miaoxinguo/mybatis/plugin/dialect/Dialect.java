package com.github.miaoxinguo.mybatis.plugin.dialect;

/**
 * 支持的数据库语言
 */
public interface Dialect {

    enum Type {
        MYSQL
    }

    /**
     * 获取分页sql
     */
    String getPagedSql(String sql, int offset, int limit);

    /**
     * 获取查总数的sql
     */
    String getCountSql(String sql);

    /**
     * 将SQL语句变成一条语句，并且每个单词的间隔都是1个空格
     *
     * @param sql SQL语句
     * @return 如果sql是NULL返回空，否则返回转化后的SQL
     */
    default String getLineSql(String sql) {
        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }
}