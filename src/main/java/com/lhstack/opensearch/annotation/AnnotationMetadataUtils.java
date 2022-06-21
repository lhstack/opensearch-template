package com.lhstack.opensearch.annotation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:59
 * @Modify by
 */
public class AnnotationMetadataUtils {

    public static AnnotationMetadata readerClass(Class<?> clazz) {
        AnnotationMetadata annotationMetadata = new AnnotationMetadata();
        Document document = clazz.getAnnotation(Document.class);
        if (Objects.isNull(document)) {
            annotationMetadata.setIndex(clazz.getSimpleName().toLowerCase(Locale.ROOT));
            annotationMetadata.setMappingPath("classpath:mappings/".concat(annotationMetadata.getIndex()).concat(".json"));
            annotationMetadata.setIdGenerator(IdGeneratorFactory.getOrNewInstanceIdGenerator(IdInsertStrategy.UUID, null));
            annotationMetadata.setTemplatePath("classpath:searchTemplates/".concat(annotationMetadata.getIndex()).concat(".yaml"));
        } else {
            annotationMetadata.setIndex(document.value().isBlank() ? clazz.getSimpleName().toLowerCase(Locale.ROOT) : document.value());
            annotationMetadata.setMappingPath(document.mappingPath().isBlank() ? "classpath:mappings/".concat(annotationMetadata.getIndex()).concat(".json") : document.mappingPath());
            annotationMetadata.setTemplatePath(document.templatePath().isBlank() ? "classpath:searchTemplates/".concat(annotationMetadata.getIndex()).concat(".yaml") : document.templatePath());
            List<Field> ids = Arrays.stream(clazz.getDeclaredFields()).filter(item -> Objects.nonNull(item.getAnnotation(Id.class)))
                    .collect(Collectors.toList());
            if (ids.size() > 1) {
                throw new IllegalArgumentException("The number of ids cannot be greater than 1");
            }
            if (ids.size() == 1) {
                Field field = ids.get(0);
                field.setAccessible(true);
                annotationMetadata.setIdField(field);
                Id id = field.getAnnotation(Id.class);
                annotationMetadata.setIdGenerator(IdGeneratorFactory.getOrNewInstanceIdGenerator(id.idInsertStrategy(), id.idGenerator()));
            } else {
                annotationMetadata.setIdGenerator(IdGeneratorFactory.getOrNewInstanceIdGenerator(IdInsertStrategy.UUID, null));
            }
        }
        return annotationMetadata;
    }
}
