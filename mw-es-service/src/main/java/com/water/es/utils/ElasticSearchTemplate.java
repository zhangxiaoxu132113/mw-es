package com.water.es.utils;

import com.alibaba.fastjson.JSON;
import com.water.es.annotation.EsMapping;
import com.water.es.entry.ESDocument;
import com.water.es.entry.ITArticle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
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
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("119.23.71.245"), 9300));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public Map<String, Object> getSetting() {
        Map<String, Object> setting = new HashMap<String, Object>();
        setting.put("index.number_of_shards", 5);// 创建后不能再修改
        setting.put("index.refresh_interval", "-1");
        setting.put("index.max_result_window", 500000);
        setting.put("index.number_of_replicas", 0);// 创建时副本暂时为0，加快数据导入速度
        // 在ES中，进行一次提交并删除事务日志的操作叫做 flush，一旦translog达到这个尺寸，刷新将会发生。默认为512 mb。
        setting.put("index.translog.flush_threshold_size", "2GB");
        setting.put("persistent.indices.store.throttle.max_bytes_per_sec", "150mb");// 对存储进行限流
        return setting;
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
     * 根据对象创建索引文档
     *
     * @param cls
     * @return
     */
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
     * 根据id获取文档
     *
     * @param index 索引
     * @param type  类型
     * @param id    主键
     * @return Map
     */
    public String getDocumentById(String index, String type, String id) {
        GetResponse getResponse = client.prepareGet(index, type, id).get();
        String results = "";
        Map<String, Object> source = getResponse.getSource();
        if (source != null) {
            results = JSON.toJSONString(results);
        }
        return results;
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


    /**
     * 搜索文档 （分词搜索）
     * 如果查询 日期（date） 或 整数（integer） 字段，它们会将查询字符串分别作为日期或整数对待。
     * 如果查询一个（ not_analyzed ）未分析的精确值字符串字段， 它们会将整个查询字符串作为单个词项对待。
     * 但如果要查询一个（ analyzed ）已分析的全文字段， 它们会先将查询字符串传递到一个合适的分析器，然后生成一个供查询的词项列表。
     */
    public ESDocument matchQueryBuilder(String index, String type, String key, String value, int from, int size) {
        ESDocument document = new ESDocument();
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
        long totalHits = searchResponse.getHits().getTotalHits();
        long took = searchResponse.getTook().millis();
        document.setTook(took);
        document.setTotalHits(totalHits);
        document.setJsonResult(JSON.toJSONString(results));
        return document;
    }

    /**
     * 搜索文档 (精确搜索)
     * 它不会对词的多样性进行处理（如， foo 或 FOO ）
     */
    public ESDocument searchDocumentByTerm(String index, String type, String queryField, String queryValue, int from, int size) {
        ESDocument document = new ESDocument();
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
        long totalHits = searchResponse.getHits().getTotalHits();
        long took = searchResponse.getTook().millis();
        document.setTook(took);
        document.setTotalHits(totalHits);
        document.setJsonResult(JSON.toJSONString(results));
        return document;
    }

    public String searchDocumentShouldMatch(String index, String type, String queryField, String queryValue, int from, int size) {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .should(new TermQueryBuilder(queryField, queryValue))
                .should(new TermQueryBuilder(queryField, queryValue));

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

    public ESDocument searchArticleByHighLight(String index, String[] types, String[] keys, String value, int from, int size) {
        ESDocument document = new ESDocument();
        List<ITArticle> articleList = new ArrayList<ITArticle>();
        try {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String key : keys) {
                highlightBuilder.field(key);
            }
            highlightBuilder.preTags("<strong style='color:#c00;'>");
            highlightBuilder.postTags("</strong>");
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index)
                    .highlighter(highlightBuilder);
            for (String type : types) {
                searchRequestBuilder.setTypes(type);
            }
            searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

            QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder(value);
            queryBuilder.analyzer("ik_smart");
            for (String key : keys) {
                queryBuilder.field(key);
            }
            searchRequestBuilder.setQuery(queryBuilder);
            searchRequestBuilder.setFrom(from).setSize(size);
            SearchResponse response = searchRequestBuilder.execute()
                    .actionGet();

            // 获取搜索的文档结果
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();
            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                // 将文档中的每一个对象转换json串值
                String json = hit.getSourceAsString();
                Map<String, Object> source = hit.getSource();
                Map<String, HighlightField> result = hit.highlightFields();
                //从设定的高亮域中取得指定域
                HighlightField titleField = result.get("title");
                //取得定义的高亮标签
                if (titleField != null) {
                    Text[] titleTexts = titleField.fragments();
                    //为title串值增加自定义的高亮标签
                    String title = "";
                    for (Text text : titleTexts) {
                        title += text;
                    }
                    source.put("title", title);
                }
                HighlightField contentField = result.get("content");
                //取得定义的高亮标签
                if (contentField != null) {
                    Text[] contentTexts = contentField.fragments();
                    //为title串值增加自定义的高亮标签
                    String description = "";
                    int dCount = 0;
                    for (Text text : contentTexts) {
                        if (dCount == 5) break;
                        description += text;
                        dCount++;

                    }
                    source.put("description", description);
                }
                results.add(source);
            }
            long totalHits = response.getHits().getTotalHits();
            long took = response.getTook().millis();
            document.setTook(took);
            document.setTotalHits(totalHits);
            document.setJsonResult(JSON.toJSONString(results));
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
