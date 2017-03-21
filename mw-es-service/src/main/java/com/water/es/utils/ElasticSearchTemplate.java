package com.water.es.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import com.water.es.annotation.EsMapping;
import com.water.es.entry.ITArticle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * elasticsearch帮助类
 * Created by zhangmiaojie on 2017/2/17.
 */
public class ElasticSearchTemplate {
    private static Log logger = LogFactory.getLog(ElasticSearchTemplate.class);
    private TransportClient client;

    public ElasticSearchTemplate() {
        try {
            Settings settings = Settings.builder().put("cluster.name", "my-application").build();
            //创建client
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 创建索引
     *
     * @param index 索引
     * @param type  类型
     * @param cls   对象
     * @return boolean
     */
    public boolean createIndex(String index, String type, Class cls) {
        XContentBuilder xContentBuilder = createIKMapping(cls);
        client.admin().indices()
                .preparePutMapping(index)
                .setType(type)
                .setSource(xContentBuilder)
                .execute()
                .actionGet();

        return false;
    }

    /**
     * 添加文档
     *
     * @param objStr 对象
     * @param index  索引
     * @param type   类型
     * @param id     主键
     * @return String
     */
    public String addDocument(String objStr, String index, String type, String id) {
        IndexResponse indexResponse = client.prepareIndex(index, type, id).setSource(objStr.getBytes()).get();
        return indexResponse.toString();
    }

    /**
     * 获取文档
     *
     * @param index 索引
     * @param type  类型
     * @param id    主键
     * @return Map
     */
    public Map<String, Object> getDocument(String index, String type, String id) {
        GetResponse getResponse = client.prepareGet(index, type, id).get();
        return getResponse.getSource();
    }

    /**
     * 删除文档
     *
     * @param index 索引
     * @param type  类型
     * @param id    主键
     * @return String
     */
    public String delDocument(String index, String type, String id) {
        DeleteResponse deleteResponse = client.prepareDelete(index, type, id).get();
        return deleteResponse.toString();
    }

    /**
     * 更新文档
     *
     * @param index 索引
     * @param type  类型
     * @param id    主键
     * @param key   key
     * @param value value
     * @return String
     */
    public String updateDocument(String index, String type, String id, String key, String value) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(index, type, id);
            updateRequest.doc(XContentFactory.jsonBuilder().startObject().field(key, value).endObject());
            UpdateResponse updateResponse = client.update(updateRequest).get();
            return updateResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public XContentBuilder createIKMapping(Class cls) {
        XContentBuilder xContentBuilder = null;
        try {
            xContentBuilder = XContentFactory.jsonBuilder();
            xContentBuilder.startObject().startObject("properties");
            for (Field field : cls.getDeclaredFields()) {
                EsMapping annotation = field.getAnnotation(EsMapping.class);
                if (annotation.isMapping()) {
                    xContentBuilder = xContentBuilder.startObject(field.getName()).field("type", field.getType().getSimpleName().toLowerCase());
                    if (annotation.isAnalyzer()) { //判断是否索引
                        xContentBuilder = xContentBuilder.field("analyzer", annotation.indexAnalyzer()).field("search_analyzer", annotation.searchAnalyzer());
                    } else {
                        xContentBuilder = xContentBuilder.field("index", "not_analyzed");
                    }

                    if (annotation.isStore()) { //判断是否存储
                        xContentBuilder = xContentBuilder.field("store", "yes");
                    } else {
                        xContentBuilder = xContentBuilder.field("store", "no");
                    }
                    xContentBuilder.endObject();
                }
            }
            xContentBuilder.endObject().endObject();
            System.out.println(xContentBuilder.string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xContentBuilder;
    }

    public String matchQueryBuilder(String index, String type, String key, String value,int from, int size) {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(key, value);
        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type)
                .setQuery(queryBuilder)
                .setFrom(from)
                .setSize(size)
                .execute()
                .actionGet();

        SearchHits searchHit = searchResponse.getHits();
        Map<String, Object> sources;
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (searchHit.totalHits() > 0) {
            for (SearchHit hit : searchHit.getHits()) {
                sources = hit.getSource();
                results.add(sources);
            }
        }
        return JSON.toJSONString(results);
    }

    /**
     * 搜索文档
     *
     * @param queryValue
     */
    public String searchDocumentByTerm(String index, String type, String queryField, String queryValue, int from, int size) {
        QueryBuilder queryBuilder = new TermQueryBuilder(queryField, queryValue);
        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type)
                .setQuery(queryBuilder)
                .setFrom(from)
                .setSize(size)
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHit = searchResponse.getHits();
        Map<String, Object> sources;
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (searchHit.totalHits() > 0) {
            for (SearchHit hit : searchHit.getHits()) {
                sources = hit.getSource();
                results.add(sources);
            }
        }
        return JSON.toJSONString(results);
    }

    public static void main(String[] args) {
        ElasticSearchTemplate elasticSearchTemplate = new ElasticSearchTemplate();
//        String result = elasticSearchTemplate.matchQueryBuilder("blog", "article", "content", "密码列表");
        String result = elasticSearchTemplate.searchDocumentByTerm("blog", "article", "content", "密码列表", 0, 20);
        System.out.println(result);
        Gson gson = new Gson();
        List<ITArticle> itArticles;
        Type type = new TypeToken<ArrayList<ITArticle>>() {
        }.getType();
        itArticles = gson.fromJson(result, type);
    }
}
