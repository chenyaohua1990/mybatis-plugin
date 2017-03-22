package com.github.miaoxinguo.mybatis.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理sql的几个公用方法
 */
public final class SqlUtils {

    private SqlUtils() {
    }

    /**
     * 将SQL语句变成一条语句，并且每个单词的间隔都是1个空格
     *
     * @param sql SQL语句
     * @return 如果sql是NULL返回空，否则返回转化后的SQL
     */
    public static String getLineSql(String sql) {
        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    /**
     * 得到最后一个Order By的插入点位置
     *
     * @return 返回最后一个Order By插入点的位置
     */
    public static int getLastOrderInsertPoint(String sql) {
        int orderIndex = sql.toLowerCase().lastIndexOf("order by");
        if (orderIndex == -1) {
            throw new RuntimeException("Mysql pagination sql must contain order by");
        }
        return orderIndex;
    }

    /**
     * 得到SQL第一个正确的FROM的的插入点
     */
    public static int getAfterFromInsertPoint(String sql) {
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
    private static boolean isBracketCanPartnership(String text) {
        return text == null || getIndexOfCount(text, '(') == getIndexOfCount(text, ')');
    }

    /**
     * 得到一个字符在另一个字符串中出现的次数
     *
     * @param text 文本
     * @param ch   字符
     */
    private static int getIndexOfCount(String text, char ch) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            count = (text.charAt(i) == ch) ? count + 1 : count;
        }
        return count;
    }
}
