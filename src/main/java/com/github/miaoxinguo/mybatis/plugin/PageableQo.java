package com.github.miaoxinguo.mybatis.plugin;

/**
 * 分页查询的查询对象基类
 */
public class PageableQo {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private int pageNum;
    private int pageSize;

    public PageableQo() {
    }

    /**
     * 获取偏移量
     */
    public int getOffset() {
        return this.pageNum <= 1 ? 0 : (this.pageNum - 1) * this.pageSize;
    }

    /**
     * 获取要查询的记录数
     */
    public int getLimit() {
        return this.pageSize <= 0 ? DEFAULT_PAGE_SIZE : this.pageSize;
    }

    // getter and setter
    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}