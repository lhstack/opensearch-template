package com.lhstack.opensearch.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.lhstack.opensearch.annotation.Document;
import com.lhstack.opensearch.annotation.Id;
import com.lhstack.opensearch.annotation.IdInsertStrategy;
import com.lhstack.opensearch.idgenerator.CustomIdGenerator;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 10:12
 * @Modify by
 */
@Document(value = ".test_mapping")
public class TestEntity {

    @Id(idInsertStrategy = IdInsertStrategy.SNOW_FLAKE)
    @JSONField(serialize = false, deserialize = false)
    private String id;

    private String content;

    private String name;

    public TestEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public TestEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public TestEntity setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
