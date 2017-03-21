package com.github.miaoxinguo.mybatis.plugin;

public abstract class PageableQo {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private int pageNum;
    private int pageSize;

    public PageableQo() {
    }

    public int getOffset() {
        return this.pageNum <= 1?0:(this.pageNum - 1) * this.pageSize;
    }

    public int getLimit() {
        return this.pageSize == 0?10:this.pageSize;
    }

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