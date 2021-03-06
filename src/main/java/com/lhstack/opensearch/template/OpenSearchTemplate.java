package com.lhstack.opensearch.template;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lhstack.opensearch.annotation.AnnotationMetadata;
import com.lhstack.opensearch.annotation.AnnotationMetadataFactory;
import com.lhstack.opensearch.query.PageRequest;
import com.lhstack.opensearch.query.PageResponse;
import com.lhstack.opensearch.utils.ConvertUtils;
import com.lhstack.opensearch.utils.VelocityUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.*;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.client.indices.PutMappingRequest;
import org.opensearch.common.bytes.BytesArray;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.BulkByScrollResponse;
import org.opensearch.index.reindex.DeleteByQueryRequest;
import org.opensearch.index.reindex.UpdateByQueryRequest;
import org.opensearch.rest.RestStatus;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:23
 * @Modify by
 */
public class OpenSearchTemplate {

    private final RestHighLevelClient restHighLevelClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSearchTemplate.class);
    private final RestClient lowLevelClient;

    public OpenSearchTemplate(RestClientBuilder restClientBuilder) {
        this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        this.lowLevelClient = this.restHighLevelClient.getLowLevelClient();
    }

    public OpenSearchTemplate(RestClientBuilder restClientBuilder, UsernamePasswordCredentials usernamePasswordCredentials) {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.disableAuthCaching().setDefaultCredentialsProvider(basicCredentialsProvider));
        this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        this.lowLevelClient = this.restHighLevelClient.getLowLevelClient();
    }

    /**
     * ??????index????????????
     *
     * @param index
     * @return
     */
    public Boolean existIndex(String index) {
        try {
            return this.restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.warn("index exist throw error {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * ??????index????????????
     *
     * @param clazz
     * @return
     */
    public Boolean existIndex(Class<?> clazz) {
        return existIndex(AnnotationMetadataFactory.getIndex(clazz));
    }

    /**
     * ??????Index
     *
     * @param createIndexRequest
     * @return
     */
    public Boolean createIndex(CreateIndexRequest createIndexRequest) {
        try {
            return this.restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT).isAcknowledged();
        } catch (Exception e) {
            LOGGER.warn("index create throw error {}", e.getMessage(), e);
            return false;
        }
    }


    /**
     * @param clazz        index clazz
     * @param queryBuilder ????????????
     * @param script       ??????
     * @return
     */
    public Boolean updateByQuery(Class<?> clazz, QueryBuilder queryBuilder, Script script) {
        return updateByQuery(AnnotationMetadataFactory.getIndex(clazz), queryBuilder, script);
    }

    /**
     * Boolean result = openSearchTemplate.updateByQuery(TestEntity.class,
     * QueryBuilders.matchQuery("content", "??????"), "ctx._source.name=params.name;ctx._source.content=ctx._source.content + params.name", Map.of("name", "??????Titles"));
     *
     * @param clazz
     * @param queryBuilder
     * @param painless
     * @param params
     * @return
     */
    public Boolean updateByQuery(Class<?> clazz, QueryBuilder queryBuilder, String painless, Map<String, Object> params) {
        return this.updateByQuery(clazz, queryBuilder, new Script(ScriptType.INLINE, "painless", painless, params));
    }

    /**
     * Boolean result = openSearchTemplate.updateByQuery(TestEntity.class,
     * QueryBuilders.matchQuery("content", "??????"), "ctx._source.name=params.name;ctx._source.content=ctx._source.content + params.name", Map.of("name", "??????Titles"));
     *
     * @param index
     * @param queryBuilder
     * @param painless
     * @param params
     * @return
     */
    public Boolean updateByQuery(String index, QueryBuilder queryBuilder, String painless, Map<String, Object> params) {
        return this.updateByQuery(index, queryBuilder, new Script(ScriptType.INLINE, "painless", painless, params));
    }


    /**
     * @param index        index
     * @param queryBuilder ????????????
     * @param script       ??????
     * @return
     */
    public Boolean updateByQuery(String index, QueryBuilder queryBuilder, Script script) {
        try {
            UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);
            updateByQueryRequest.setQuery(queryBuilder);
            updateByQueryRequest.setRefresh(true);
            updateByQueryRequest.setScript(script);
            BulkByScrollResponse bulkByScrollResponse = this.restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
            return bulkByScrollResponse.getUpdated() > 0;
        } catch (Exception e) {
            LOGGER.warn("update by query failure,error {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * ??????index
     *
     * @param clazz
     * @return
     */
    public Boolean createIndex(Class<?> clazz) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(annotationMetadata.getIndex());
        byte[] bytes = annotationMetadata.readMappingBytes();
        if (Objects.nonNull(bytes)) {
            createIndexRequest.source(new BytesArray(bytes), XContentType.JSON);
        }
        return this.createIndex(createIndexRequest);
    }

    /**
     * ??????index
     *
     * @param clazz
     * @return
     */
    public Boolean deleteIndex(Class<?> clazz) {
        return this.deleteIndex(AnnotationMetadataFactory.getIndex(clazz));
    }

    /**
     * ??????mapping
     *
     * @param putMappingRequest
     * @return
     */
    public Boolean putMapping(PutMappingRequest putMappingRequest) {
        try {
            return this.restHighLevelClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT).isAcknowledged();
        } catch (Exception e) {
            LOGGER.warn("putMapping throw error {}", e.getMessage(), e);
            return false;
        }
    }

    public List<JSONObject> plugins() {
        try {
            Request request = new Request("GET", "_cat/plugins?format=json");
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("plugins failure,result {}", EntityUtils.toString(response.getEntity()));
                return Collections.emptyList();
            }
            String result = EntityUtils.toString(response.getEntity());
            return JSONArray.parseArray(result).toJavaList(JSONObject.class);
        } catch (Exception e) {
            LOGGER.error("plugins failure,error {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<JSONObject> analyze(String analyzer, String content) {
        try {
            Request request = new Request("GET", "_analyze");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("analyzer", analyzer);
            jsonObject.put("text", content);
            request.setJsonEntity(jsonObject.toJSONString());
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("analyze failure,result {}", EntityUtils.toString(response.getEntity()));
                return Collections.emptyList();
            }
            String result = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(result).getObject("tokens", new TypeReference<List<JSONObject>>() {
            });
        } catch (Exception e) {
            LOGGER.error("analyze failure,error {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<JSONObject> analyze(Class<?> clazz, String field, String content) {
        return analyze(AnnotationMetadataFactory.getIndex(clazz), field, content);
    }

    public List<JSONObject> analyze(String index, String field, String content) {
        try {

            Request request = new Request("GET", String.format("%s/_analyze", index));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("field", field);
            jsonObject.put("text", content);
            request.setJsonEntity(jsonObject.toJSONString());
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("analyze failure,result {}", EntityUtils.toString(response.getEntity()));
                return Collections.emptyList();
            }
            String result = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(result).getObject("tokens", new TypeReference<List<JSONObject>>() {
            });
        } catch (Exception e) {
            LOGGER.error("analyze failure,error {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * ??????id????????????
     *
     * @param index index
     * @param id    id
     * @return
     */
    public Boolean deleteById(String index, String id) {
        try {
            return this.restHighLevelClient.delete(new DeleteRequest(index, id), RequestOptions.DEFAULT).status() == RestStatus.OK;
        } catch (Exception e) {
            LOGGER.warn("delete byId throw error {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * ??????
     *
     * @param searchRequest
     * @return
     */
    public SearchResponse search(SearchRequest searchRequest) {
        try {
            return this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ??????index
     *
     * @param clazz
     * @param consumer
     * @return
     */
    public SearchResponse search(Class<?> clazz, Consumer<SearchSourceBuilder> consumer) {
        return this.search(AnnotationMetadataFactory.getIndex(clazz), consumer);
    }

    /**
     * ??????
     *
     * @param index
     * @param consumer
     * @return
     */
    public SearchResponse search(String index, Consumer<SearchSourceBuilder> consumer) {
        SearchRequest searchRequest = Requests.searchRequest(index);
        SearchSourceBuilder source = searchRequest.source();
        consumer.accept(source);
        return this.search(searchRequest);
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    /**
     * ????????????
     *
     * @param clazz
     * @param pageRequest
     * @param <T>
     * @return
     */
    public <T> PageResponse<T> searchPage(Class<T> clazz, PageRequest pageRequest) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        try {
            SearchRequest searchRequest = Requests.searchRequest(annotationMetadata.getIndex());
            SearchSourceBuilder source = searchRequest.source();
            pageRequest.getConsumer().accept(source);
            source.from((pageRequest.getPage() - 1) * pageRequest.getSize());
            source.size(pageRequest.getSize());
            SearchResponse searchResponse = this.search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            List<T> list = ConvertUtils.convertList(annotationMetadata, clazz, hits);
            return new PageResponse<T>(pageRequest.getPage(), pageRequest.getSize(), searchResponse.getHits().getTotalHits().value, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> PageResponse<T> searchPageTemplate(Class<T> clazz, String templateId) {
        return this.searchPageTemplate(clazz, templateId, Collections.emptyMap());
    }

    public <T> PageResponse<T> searchPageTemplate(Class<T> clazz, String templateId, Map<String, Object> params) {
        try {
            AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
            String template = annotationMetadata.getTemplate(templateId);
            String queryDsl = VelocityUtils.process(template, params);
            Request request = new Request("GET", String.format("%s/_search", annotationMetadata.getIndex()));
            request.setJsonEntity(queryDsl);
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("pageTemplate failure,result {}", EntityUtils.toString(response.getEntity()));
                return new PageResponse<>(0, Collections.emptyList());
            }
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            JSONObject hits = jsonObject.getJSONObject("hits");
            return new PageResponse<>(hits.getJSONObject("total").getLongValue("value"), ConvertUtils.convertList(annotationMetadata, clazz, hits));
        } catch (Exception e) {
            LOGGER.error("pageTemplate failure,error {}", e.getMessage(), e);
            return new PageResponse<>(0, Collections.emptyList());
        }
    }

    /**
     * ????????????
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> searchAll(Class<T> clazz) {
        return this.searchList(clazz, QueryBuilders.matchAllQuery());
    }

    /**
     * ????????????
     *
     * @param clazz
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> List<T> searchList(Class<T> clazz, Consumer<SearchSourceBuilder> consumer) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        SearchRequest searchRequest = Requests.searchRequest(annotationMetadata.getIndex());
        SearchSourceBuilder source = searchRequest.source();
        consumer.accept(source);
        try {
            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            return ConvertUtils.convertList(annotationMetadata, clazz, hits);
        } catch (Exception e) {
            LOGGER.warn("searchOne throw error {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * ????????????????????????
     *
     * @param index
     * @param id    id
     * @return
     */
    public Boolean exist(String index, String id) {
        try {
            return this.restHighLevelClient.exists(Requests.getRequest(index).id(id), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ??????????????????
     *
     * @param clazz
     * @param id
     * @return
     */
    public Boolean exist(Class<?> clazz, String id) {
        try {
            return this.restHighLevelClient.exists(Requests.getRequest(AnnotationMetadataFactory.getIndex(clazz)).id(id), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ????????????????????????
     *
     * @param o
     * @return
     */
    public Boolean exist(Object o) {
        try {
            AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(o.getClass());
            if (Objects.isNull(annotationMetadata.getIdField())) {
                throw new NullPointerException("id field cannot null");
            }
            Field idField = annotationMetadata.getIdField();
            Object id = idField.get(o);
            if (Objects.isNull(id)) {
                throw new NullPointerException("id cannot null");
            }
            return this.exist(annotationMetadata.getIndex(), String.valueOf(id));
        } catch (Exception e) {
            LOGGER.warn("exist failure,error {}", e.getMessage(), e);
            return false;
        }
    }


    /**
     * ????????????????????????
     *
     * @param objects
     * @param <T>
     * @return
     */
    public <T> Boolean saveOrUpdateBatch(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            throw new RuntimeException("objects cannot empty");
        }
        BulkRequest bulkRequest = Requests.bulkRequest();
        T t = objects.get(0);
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(t.getClass());
        objects
                .stream()
                .map(item -> {
                    String id = resolveId(item, annotationMetadata);
                    return Requests.indexRequest(annotationMetadata.getIndex()).id(id).source(JSONObject.toJSONString(item), XContentType.JSON);
                }).forEach(bulkRequest::add);
        try {
            BulkResponse response = this.restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !response.hasFailures();
        } catch (Exception e) {
            LOGGER.warn("insert batch failure,error {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * ??????????????????
     *
     * @param obj
     * @return
     */
    public Boolean saveOrUpdate(Object obj) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(obj.getClass());
        try {
            String id = resolveId(obj, annotationMetadata);
            IndexRequest indexRequest = Requests.indexRequest(annotationMetadata.getIndex());
            indexRequest
                    .id(id)
                    .source(JSONObject.toJSONString(obj), XContentType.JSON);
            IndexResponse response = this.restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            return response.status() == RestStatus.CREATED;
        } catch (Exception e) {
            LOGGER.warn("insert failure,throw error {}", e.getMessage(), e);
            return false;
        }
    }

    private String resolveId(Object obj, AnnotationMetadata annotationMetadata) {
        String id = null;
        try {
            if (Objects.nonNull(annotationMetadata.getIdField())) {
                Object o = annotationMetadata.getIdField().get(obj);
                if (Objects.nonNull(o)) {
                    id = String.valueOf(o);
                }
            }
            if (Objects.isNull(id)) {
                id = annotationMetadata.getIdGenerator().nextId();
                if (Objects.nonNull(annotationMetadata.getIdField())) {
                    annotationMetadata.getIdField().set(obj, id);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("get id failure,error {}", e.getMessage(), e);
        }
        return id;
    }

    /**
     * ????????????
     *
     * @param clazz
     * @param queryBuilder
     * @param <T>
     * @return
     */
    public <T> List<T> searchList(Class<T> clazz, QueryBuilder queryBuilder) {
        return this.searchList(clazz, searchSourceBuilder -> searchSourceBuilder.query(queryBuilder));
    }

    public <T> List<T> searchListTemplate(Class<T> clazz, String templateId) {
        return this.searchListTemplate(clazz, templateId, Collections.emptyMap());
    }

    public JSONObject searchTemplate(Class<?> clazz, String templateId) {
        return this.searchTemplate(clazz, templateId, Collections.emptyMap());
    }

    public JSONObject searchTemplate(Class<?> clazz, String templateId, Map<String, Object> params) {
        try {
            AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
            String template = annotationMetadata.getTemplate(templateId);
            String queryDsl = VelocityUtils.process(template, params);
            Request request = new Request("GET", String.format("%s/_search", annotationMetadata.getIndex()));
            request.setJsonEntity(queryDsl);
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("searchTemplate failure,result {}", EntityUtils.toString(response.getEntity()));
                return new JSONObject();
            }
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            LOGGER.error("searchTemplate failure,error {}", e.getMessage(), e);
            return new JSONObject();
        }
    }

    public JSONObject searchTemplate(String index, String templateContent) {
        return searchTemplate(index, templateContent, Collections.emptyMap());
    }

    public JSONObject searchTemplate(String index, String templateContent, Map<String, Object> params) {
        try {
            String queryDsl = VelocityUtils.process(templateContent, params);
            Request request = new Request("GET", String.format("%s/_search", index));
            request.setJsonEntity(queryDsl);
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("searchTemplate failure,result {}", EntityUtils.toString(response.getEntity()));
                return new JSONObject();
            }
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            LOGGER.error("searchTemplate failure,error {}", e.getMessage(), e);
            return new JSONObject();
        }
    }

    public <T> List<T> searchListTemplate(Class<T> clazz, String templateId, Map<String, Object> params) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        try {
            String template = annotationMetadata.getTemplate(templateId);
            if (Objects.isNull(template)) {
                throw new NullPointerException("template not found,templateId " + templateId);
            }
            String queryDsl = VelocityUtils.process(template, params);
            Request request = new Request("GET", String.format("%s/_search", annotationMetadata.getIndex()));
            request.setJsonEntity(queryDsl);
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("searchListTemplate failure,result {}", EntityUtils.toString(response.getEntity()));
                return Collections.emptyList();
            }
            String jsonResult = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(jsonResult);
            JSONObject hits = jsonObject.getJSONObject("hits");
            return ConvertUtils.convertList(annotationMetadata, clazz, hits);
        } catch (Exception e) {
            LOGGER.warn("searchListTemplate throw error {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * ????????????
     *
     * @param index
     * @param queryBuilder
     * @return
     */
    public Boolean delete(String index, QueryBuilder queryBuilder) {
        try {
            BulkByScrollResponse bulkByScrollResponse = this.restHighLevelClient.deleteByQuery(new DeleteByQueryRequest(index).setQuery(queryBuilder), RequestOptions.DEFAULT);
            return bulkByScrollResponse.getDeleted() > 0;
        } catch (Exception e) {
            LOGGER.warn("delete throw error {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * ????????????
     *
     * @param clazz
     * @param queryBuilder
     * @return
     */
    public Boolean delete(Class<?> clazz, QueryBuilder queryBuilder) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        return this.delete(annotationMetadata.getIndex(), queryBuilder);
    }

    /**
     * ??????????????????
     *
     * @param clazz
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> Optional<T> searchOne(Class<T> clazz, Consumer<SearchSourceBuilder> consumer) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        SearchRequest searchRequest = Requests.searchRequest(annotationMetadata.getIndex());
        SearchSourceBuilder source = searchRequest.source();
        consumer.accept(source);
        try {
            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            long value = hits.getTotalHits().value;
            if (value > 0) {
                SearchHit hit = hits.getHits()[0];
                String id = hit.getId();
                String sourceAsString = hit.getSourceAsString();
                return Optional.of(ConvertUtils.convert(annotationMetadata, clazz, id, sourceAsString));
            }
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("searchOne throw error {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * ????????????????????????
     *
     * @param clazz
     * @param queryBuilder
     * @param <T>
     * @return
     */
    public <T> Optional<T> searchOne(Class<T> clazz, QueryBuilder queryBuilder) {
        return searchOne(clazz, searchSourceBuilder -> searchSourceBuilder.query(queryBuilder));
    }

    /**
     * ??????Velocity????????????????????????
     *
     * @param clazz
     * @param templateId
     * @param <T>
     * @return
     */
    public <T> Optional<T> searchOneTemplate(Class<T> clazz, String templateId) {
        return searchOneTemplate(clazz, templateId, Collections.emptyMap());
    }

    /**
     * ??????Velocity????????????????????????
     *
     * @param clazz
     * @param templateId
     * @param params
     * @param <T>
     * @return
     */
    public <T> Optional<T> searchOneTemplate(Class<T> clazz, String templateId, Map<String, Object> params) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        try {
            String template = annotationMetadata.getTemplate(templateId);
            if (Objects.isNull(template)) {
                throw new NullPointerException("template not found,templateId " + templateId);
            }
            String queryDsl = VelocityUtils.process(template, params);
            Request request = new Request("GET", String.format("%s/_search", annotationMetadata.getIndex()));
            request.setJsonEntity(queryDsl);
            Response response = this.lowLevelClient.performRequest(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.warn("searchOneTemplate failure,result {}", EntityUtils.toString(response.getEntity()));
                return Optional.empty();
            }
            String jsonResult = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(jsonResult);
            JSONObject hits = jsonObject.getJSONObject("hits");
            long value = hits.getJSONObject("total").getLongValue("value");
            if (value > 0) {
                JSONArray jsonArray = hits.getJSONArray("hits");
                JSONObject item = jsonArray.getJSONObject(0);
                String id = item.getString("_id");
                return Optional.of(ConvertUtils.convert(annotationMetadata, clazz, id, item.getJSONObject("_source")));
            }
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("searchOne throw error {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * ??????id??????
     *
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    public <T> Optional<T> findById(Class<T> clazz, String id) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        try {
            GetResponse response = this.restHighLevelClient.get(Requests.getRequest(annotationMetadata.getIndex()).id(id), RequestOptions.DEFAULT);
            if (!response.isExists()) {
                return Optional.empty();
            }
            String responseId = response.getId();
            return Optional.of(ConvertUtils.convert(annotationMetadata, clazz, responseId, response.getSourceAsString()));
        } catch (Exception e) {
            LOGGER.warn("findById throw error {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * @param obj obj???????????????Id??????
     * @return
     */
    public Boolean deleteById(Object obj) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(obj.getClass());
        Field idField = annotationMetadata.getIdField();
        if (Objects.isNull(idField)) {
            throw new NullPointerException("id field is null");
        }
        try {
            return this.deleteById(annotationMetadata.getIndex(), String.valueOf(idField.get(obj)));
        } catch (Exception e) {
            LOGGER.warn("delete byId throw error {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * ??????id??????
     *
     * @param clazz
     * @param id
     * @return
     */
    public Boolean deleteById(Class<?> clazz, String id) {
        AnnotationMetadata annotationMetadata = AnnotationMetadataFactory.getAnnotationMetadata(clazz);
        return this.deleteById(annotationMetadata.getIndex(), id);
    }

    public Boolean putSettings(UpdateSettingsRequest settingsRequest) {
        try {
            return this.restHighLevelClient.indices().putSettings(settingsRequest, RequestOptions.DEFAULT).isAcknowledged();
        } catch (Exception e) {
            LOGGER.warn("putSettings throw error {}", e.getMessage(), e);
            return false;
        }
    }

    public Boolean deleteIndex(String index) {
        try {
            AcknowledgedResponse response = this.restHighLevelClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            LOGGER.warn("index remove throw error {}", e.getMessage(), e);
            return false;
        }
    }

    public void close() throws IOException {
        this.restHighLevelClient.close();
    }
}
