package com.lhstack.opensearch.query;

import org.opensearch.search.builder.SearchSourceBuilder;

import java.util.function.Consumer;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 11:21
 * @Modify by
 */
public class PageRequest {

    private final int page;

    private final int size;

    private final Consumer<SearchSourceBuilder> consumer;

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
        this.consumer = searchSourceBuilder -> {};
    }

    public PageRequest(int page, int size, Consumer<SearchSourceBuilder> consumer) {
        this.page = page;
        this.size = size;
        this.consumer = consumer;
    }

    public Consumer<SearchSourceBuilder> getConsumer() {
        return consumer;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
