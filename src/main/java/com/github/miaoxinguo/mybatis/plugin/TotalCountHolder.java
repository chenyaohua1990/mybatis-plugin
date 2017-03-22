package com.github.miaoxinguo.mybatis.plugin;

/**
 * 当前线程分页查询语句的总记录数
 *
 * <p>如果一个线程执行多次分页查询， 每次查询后必须保存本次查询的总记录数。下一次查询会覆盖这个值</p>
 */
public final class TotalCountHolder {

    private static ThreadLocal<Integer> totalCount = new ThreadLocal<>();

    private TotalCountHolder() {}

    public static void set(int count) {
        totalCount.set(count);
    }

    public static int get() {
        return totalCount.get();
    }
}
