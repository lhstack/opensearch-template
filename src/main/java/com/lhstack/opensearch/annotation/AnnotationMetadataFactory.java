package com.lhstack.opensearch.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 10:10
 * @Modify by
 */
public class AnnotationMetadataFactory {

    private static final Map<Class<?>, AnnotationMetadata> ANNOTATION_METADATA_CACHE = new HashMap<>();

    public static AnnotationMetadata getAnnotationMetadata(Class<?> clazz) {
        return ANNOTATION_METADATA_CACHE.computeIfAbsent(clazz, key -> AnnotationMetadataUtils.readerClass(clazz));
    }

    /**
     * 获取类的index
     * @param clazz
     * @return
     */
    public static String getIndex(Class<?> clazz) {
        return getAnnotationMetadata(clazz).getIndex();
    }
}
