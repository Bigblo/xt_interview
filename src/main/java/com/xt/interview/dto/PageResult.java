package com.xt.interview.dto;

import java.util.List;

/**
 * 通用分页返回结构，对应 {@code { list, totalElements, totalPages, currentPage, pageSize }}。
 *
 * @param <T> 列表元素类型
 */
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> list;

    /** 符合条件的总记录数 */
    private long totalElements;

    /** 总页数 */
    private int totalPages;

    /** 当前页码（从 0 开始，与接口入参一致） */
    private int currentPage;

    /** 每页条数（实际生效值，可能被服务端限制过） */
    private int pageSize;

    public PageResult() {}

    public PageResult(List<T> list, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.list = list;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    /**
     * 静态构造方法，省去每次 new。
     */
    public static <T> PageResult<T> of(List<T> list, long totalElements, int totalPages, int currentPage, int pageSize) {
        return new PageResult<>(list, totalElements, totalPages, currentPage, pageSize);
    }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
