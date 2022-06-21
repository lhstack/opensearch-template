package com.lhstack.opensearch.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lhstack.opensearch.annotation.AnnotationMetadata;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 10:56
 * @Modify by
 */
public class ConvertUtils {

    public static <T> T convert(AnnotationMetadata metadata, Class<T> clazz, String id, JSONObject jsonObject) throws Exception {
        T t = jsonObject.toJavaObject(clazz);
        if (metadata.getIdField() != null) {
            metadata.getIdField().set(t, id);
        }
        return t;
    }

    public static <T> T convert(AnnotationMetadata metadata, Class<T> clazz, String id, String json) throws Exception {
        T object = JSONObject.parseObject(json, clazz);
        if (metadata.getIdField() != null) {
            metadata.getIdField().set(object, id);
        }
        return object;
    }

    public static <T> List<T> convertList(AnnotationMetadata annotationMetadata, Class<T> clazz, SearchHits hits) throws Exception {
        List<T> list = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            list.add(ConvertUtils.convert(annotationMetadata, clazz, hit.getId(), hit.getSourceAsString()));
        }
        return list;
    }

    public static <T> List<T> convertList(AnnotationMetadata annotationMetadata, Class<T> clazz, JSONObject hits) throws Exception {
        long total = hits.getJSONObject("total").getLongValue("value");
        if (total <= 0) {
            return Collections.emptyList();
        }
        JSONArray arrays = hits.getJSONArray("hits");
        List<T> result = new ArrayList<>();
        for (int i = 0; i < arrays.size(); i++) {
            JSONObject jsonObject = arrays.getJSONObject(i);
            result.add(convert(annotationMetadata, clazz, jsonObject.getString("_id"), jsonObject.getJSONObject("_source")));
        }
        return result;
    }
}
