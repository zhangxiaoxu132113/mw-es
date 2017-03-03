package com.water.es.utils;

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
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
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
     * 添加文档
     *
     * @param objStr 对象
     * @param index  索引
     * @param type   类型
     * @param id     主键
     * @return       String
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
     * @return      Map
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
     * @return      String
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
     * @return      String
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
     * 搜索文档
     *
     * @param queryValue
     */
    public void searchDocumentByTerm(String queryValue) {
        QueryBuilder queryBuilder = new TermQueryBuilder("content",queryValue);
        SearchResponse searchResponse = client.prepareSearch("blog")
                .setQuery(queryBuilder)
                .setFrom(0)
                .setSize(60)
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHit = searchResponse.getHits();
        for (int i=0; i<searchHit.totalHits(); i++){
            System.out.println(searchHit.getAt(i).getSource().get("title") + " : " + searchHit.getAt(i).getScore());
//            System.out.println(searchHit.getAt(i).getSource().get("content"));
        }

    }
}
