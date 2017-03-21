package com.github.miaoxinguo.mybatis.plugin;

import java.util.List;

/**
 * 分页查询结果
 */
public class Page<T> {

    private static final int DEFAULT_PAGE_SIZE = 10;

    // 当前页
    private int pageNum;

    // 页大小
    private int pageSize;

    // 总页数
    private int totalPage;

    // 总记录数
    private int totalRecord;

    // 分页查询结果
    private List<T> list;

    public Page() {
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 获取偏移量
     */
    public int getOffset() {
        if (pageNum <= 1) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取查询记录数
     */
    public int getLimit() {
        if (pageSize == 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
