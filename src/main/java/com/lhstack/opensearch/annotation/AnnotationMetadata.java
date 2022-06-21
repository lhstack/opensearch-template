package com.lhstack.opensearch.annotation;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lhstack.opensearch.utils.PathResolveUtils;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 10:00
 * @Modify by
 */
public class AnnotationMetadata {

    private String index;

    private String mappingPath;

    private IdGenerator idGenerator;

    private Field idField;

    private Map<String, String> templateCache;
    private String templatePath;

    public AnnotationMetadata setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
        byte[] bytes = PathResolveUtils.readBytes(templatePath);
        if (bytes == PathResolveUtils.EMPTY_BYTES) {
            templateCache = Collections.emptyMap();
            return this;
        }
        Yaml yaml = new Yaml();
        JSONObject jsonObject = yaml.loadAs(new String(bytes, StandardCharsets.UTF_8), JSONObject.class);
        this.templateCache = jsonObject.toJavaObject(new TypeReference<Map<String, String>>() {
        });
        return this;
    }

    public String getTemplate(String id) {
        return this.templateCache.get(id);
    }

    public AnnotationMetadata setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    public AnnotationMetadata setMappingPath(String mappingPath) {
        this.mappingPath = mappingPath;
        return this;
    }

    public AnnotationMetadata setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public AnnotationMetadata setIdField(Field idField) {
        this.idField = idField;
        return this;
    }

    public Field getIdField() {
        return idField;
    }

    @Override
    public String toString() {
        return "AnnotationMetadata{" +
                "index='" + index + '\'' +
                ", mappingPath='" + mappingPath + '\'' +
                ", idGenerator=" + idGenerator +
                ", idField=" + idField +
                ", templateCache=" + templateCache +
                ", templatePath='" + templatePath + '\'' +
                '}';
    }

    public byte[] readMappingBytes() {
        return PathResolveUtils.readBytes(this.mappingPath);
    }

}
