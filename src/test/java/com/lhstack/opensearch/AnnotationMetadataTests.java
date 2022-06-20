package com.lhstack.opensearch;

import com.lhstack.opensearch.annotation.AnnotationMetadata;
import com.lhstack.opensearch.annotation.AnnotationMetadataFactory;
import com.lhstack.opensearch.entity.TestEntity;
import org.junit.jupiter.api.Test;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 10:13
 * @Modify by
 */
class AnnotationMetadataTests {

    @Test
    void annotationMetadataReader(){
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(TestEntity.class);
        System.out.println(annotationMetadata);
    }
}
