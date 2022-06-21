package com.lhstack.opensearch.query;

import java.util.List;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 11:28
 * @Modify by
 */
public class PageResponse<T> {

    private int page;

    private int size;

    private long total;

    private List<T> list;

    public PageResponse(int page, int size, long total, List<T> list) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.list = list;
    }

    public PageResponse(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public PageResponse<T> setPage(int page) {
        this.page = page;
        return this;
    }

    public int getSize() {
        return size;
    }

    public PageResponse<T> setSize(int size) {
        this.size = size;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public PageResponse<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public PageResponse<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", list=" + list +
                '}';
    }
}
