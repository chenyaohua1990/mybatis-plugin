package com.github.miaoxinguo.mybatis.plugin;

import java.util.List;

/**
 * 分页查询结果
 */
public class PagingResult<T> {

    // 当前页
    private int currentPage = 1;

    // 总页数
    private int totalPage;

    // 总记录数
    private int totalRecord;

    // 页大小
    private int pageSize;

    // 起始记录条数
    private int start;

    // 分页查询结果
    private List<T> list;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
