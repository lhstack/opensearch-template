package com.lhstack.opensearch;

import com.alibaba.fastjson.JSONObject;
import com.lhstack.opensearch.entity.TestEntity;
import com.lhstack.opensearch.query.PageRequest;
import com.lhstack.opensearch.query.PageResponse;
import com.lhstack.opensearch.template.OpenSearchTemplate;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.client.*;
import org.opensearch.client.core.GetSourceRequest;
import org.opensearch.client.core.GetSourceResponse;
import org.opensearch.common.settings.Settings;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.UpdateByQueryRequest;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.search.sort.SortBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    void testAnalyze(){
        List<JSONObject> analyze = openSearchTemplate.analyze(TestEntity.class, "content", "中华人民共和国");
        System.out.println(analyze);

        List<JSONObject> result = openSearchTemplate.analyze("ik_smart", "我已经远离了你的河流，绝不是疏远，因为我已是身居异土。远远地，依然听得到你翻山越岭、日夜兼程的脚步声，依然无比清晰地看得到你的那条洒满星星的清澈的河流。只要，也只有想起你——故乡，就是异域的那条河流也会洒满故乡的星星。无论醒着还是沉睡，都会重复着那个让人心旌摇曳的画面，星星都在你的河流里百媚地眨眼。因为那条洒满星星的河流途经我无猜的孩童，无知的少年，迷茫的青春，还要经过可知的未来，是不是今后还要交付给大海了呢？那样，大海的那些星星一定是故乡送给他们的馈赠，没有故乡的孕育，大海也会失去灵气，因为大海的源头就在故乡的这头……");
        System.out.println(result);
    }

    @Test
    void testPlugins(){
        System.out.println(openSearchTemplate.plugins());
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
    void testUpdateByQuery(){
        Boolean result = openSearchTemplate.updateByQuery(TestEntity.class,
                QueryBuilders.matchQuery("content", "世界"), "ctx._source.name=params.name;ctx._source.content=ctx._source.content + params.name", Map.of("name", "世界Titles"));
        System.out.println(result);
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
