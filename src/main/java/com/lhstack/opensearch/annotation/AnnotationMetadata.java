package com.lhstack.opensearch.annotation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;

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
                '}';
    }

    public byte[] readMappingBytes() {
        InputStream inputStream = null;
        try {
            if (this.mappingPath.startsWith("classpath:")) {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.mappingPath.substring(10));
            } else if (this.mappingPath.startsWith("file:")) {
                inputStream = new FileInputStream(this.mappingPath.substring(5));
            } else {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.mappingPath);
            }
            if (Objects.isNull(inputStream)) {
                return null;
            }
            return inputStream.readAllBytes();
        } catch (Exception e) {
            return null;
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
