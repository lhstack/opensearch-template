```yaml
version: '3'
services:
  opensearch:
    image: opensearchproject/opensearch:2.0.0
    container_name: opensearch
    restart: always
    environment:
    - "OPENSEARCH_JAVA_OPTS=-Xms512m -Xmx512m"
    - "discovery.type=single-node"
    - "DISABLE_SECURITY_PLUGIN=true"
    ulimits:
      memlock:
        soft: -1
        hard: -1
      nofile:
        soft: 65536
        hard: 65536
    ports:
    - 9200:9200
    - 9600:9600
    logging:
      options:
        max-file: '2'
        max-size: '32k'
    volumes:
    - ./data:/usr/share/opensearch/data
    - ./plugins:/usr/share/opensearch/plugins
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 512M
  dashboard:
    image: opensearchproject/opensearch-dashboards:2.0.0
    container_name: dashboard
    restart: always
    environment:
      DISABLE_SECURITY_DASHBOARDS_PLUGIN: true
      I18N_LOCALE: zh-CN
      OPENSEARCH_HOSTS: '["http://opensearch:9200"]'
    ports:
    - '5601:5601'
    depends_on:
    - opensearch
    logging:
      options:
        max-file: '2'
        max-size: '32k'
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 256M
```
```java
package com.lhstack.opensearch;

import com.lhstack.opensearch.entity.TestEntity;
import com.lhstack.opensearch.query.PageRequest;
import com.lhstack.opensearch.query.PageResponse;
import com.lhstack.opensearch.template.OpenSearchTemplate;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.UpdateByQueryRequest;
import org.opensearch.search.sort.SortBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Description TODO
 * @Copyright: Copyright (c) 2022 ALL RIGHTS RESERVED.
 * @Author lhstack
 * @Date 2022/6/20 9:27
 * @Modify by
 */
class OpenSearchTemplateTests {

    private static OpenSearchTemplate openSearchTemplate;

    @BeforeAll
    static void before(){
        openSearchTemplate = new OpenSearchTemplate(
                RestClient.builder(new HttpHost("192.168.2.188",9200)),
                new UsernamePasswordCredentials("es","654321"));
    }

    @Test
    void testCreateIndex(){
        Boolean result = openSearchTemplate.createIndex(TestEntity.class);
        System.out.println(result);
    }

    @Test
    void testDeleteIndex(){
        Boolean result = openSearchTemplate.deleteIndex(TestEntity.class);
        System.out.println(result);
    }

    @Test
    void testDeleteById(){
        System.out.println(openSearchTemplate.deleteById("test", "null"));
    }


    @Test
    void testDeleteByIdUseObj(){
        System.out.println(openSearchTemplate.deleteById(new TestEntity().setId("6")));
    }

    @Test
    void testFindById(){
        Optional<TestEntity> byId = openSearchTemplate.findById(TestEntity.class, "6");
        byId.ifPresent(System.out::println);
    }

    @Test
    void testSearchOne(){
        System.out.println(openSearchTemplate.searchOne(TestEntity.class, QueryBuilders.matchAllQuery()));
    }

    @Test
    void testSearchAll(){
        System.out.println(openSearchTemplate.searchAll(TestEntity.class));
    }

    @Test
    void testSearchList(){
        System.out.println(openSearchTemplate.searchList(TestEntity.class, searchSourceBuilder -> {
            searchSourceBuilder.query(QueryBuilders.matchQuery("content","世界"))
                    .sort(SortBuilders.fieldSort("_id"));
        }));
    }

    @Test
    void testDeleteAll(){
        Boolean delete = openSearchTemplate.delete(TestEntity.class, QueryBuilders.matchAllQuery());
        System.out.println(delete);
    }

    @Test
    void testSaveOrUpdate(){
        TestEntity testEntity = new TestEntity();
        testEntity.setContent("你好世界");
        testEntity.setName("title");
        System.out.println(openSearchTemplate.saveOrUpdate(testEntity));
        System.out.println(testEntity);
    }

    @Test
    void testInsertBatch(){
        TestEntity testEntity = new TestEntity();
        testEntity.setContent("你好世界");
        testEntity.setId("71214306c2cc495fb8ffd84b795ed9e2");
        List<TestEntity> list = new ArrayList<>();
        list.add(testEntity);
        TestEntity testEntity1 = new TestEntity();
        testEntity1.setContent("你好你好");
        list.add(testEntity1);
        System.out.println(openSearchTemplate.saveOrUpdateBatch(list));
        System.out.println(list);
    }

    @Test
    void testPage(){
        PageResponse<TestEntity> page = openSearchTemplate.page(TestEntity.class, new PageRequest(1, 10));
        System.out.println(page);
    }

    @Test
    void testDelete() {
        Boolean delete = openSearchTemplate.delete(TestEntity.class, QueryBuilders.matchAllQuery());
        System.out.println(delete);
    }

    @Test
    void testExistIndexOnClass(){
        Boolean result = openSearchTemplate.existIndex(TestEntity.class);
        System.out.println(result);
    }

    @Test
    void testExistIndex(){
        Boolean test = openSearchTemplate.existIndex("test1");
        System.out.println(test);
    }
}
```