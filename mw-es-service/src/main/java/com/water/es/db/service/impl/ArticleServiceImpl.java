package com.water.es.db.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.water.es.api.Service.IArticleService;
import com.water.es.entry.ESDocument;
import com.water.es.entry.ITArticle;
import com.water.es.utils.Constants;
import com.water.es.utils.ElasticSearchTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 帖子业务类
 * Created by zhangmiaojie on 2017/3/3.
 */
@Service("esArticleService")
public class ArticleServiceImpl implements IArticleService {

    @Resource(name = "elasticsearchTemplate")
    private ElasticSearchTemplate elasticSearchTemplate;

    @Override
    public void addArticle(ITArticle article) {
        if (article == null) {
            throw new RuntimeException("参数不合法！");
        }
        elasticSearchTemplate.addDocument(JSONObject.toJSONString(article), Constants.ES_CONFIG.INDEX_BLOG, Constants.ES_CONFIG.TYPE_ITARTICLE, String.valueOf(article.getId()));
    }

    @Override
    public ESDocument searchArticleByTerm(String field, String value, int from, int size) {
        String[] searchField = {"id", "title", "createOn"};
        ESDocument document = elasticSearchTemplate.searchDocumentByTerm(Constants.ES_CONFIG.INDEX_BLOG, Constants.ES_CONFIG.TYPE_ITARTICLE,
                field, value, searchField, null, from, size);
        List<ITArticle> articleList = this.getArticlesByJson(document.getJsonResult());
        document.setResult(articleList);
        return document;
    }

    @Override
    public ESDocument searchArticleByMatch(String field, String value, int from, int size) {
        String[] searchField = {"id", "title", "createOn"};
        ESDocument document = elasticSearchTemplate.matchQueryBuilder(Constants.ES_CONFIG.INDEX_BLOG, Constants.ES_CONFIG.TYPE_ITARTICLE,
                field, value, searchField, null, from, size);
        List<ITArticle> articleList = this .getArticlesByJson(document.getJsonResult());
        document.setResult(articleList);
        return document;
    }

    @Override
    public ESDocument searchArticleByMatchWithHighLight(String[] field, String value, int from, int size) {
        ESDocument document = elasticSearchTemplate.searchArticleByHighLight(Constants.ES_CONFIG.INDEX_BLOG, new String[]{Constants.ES_CONFIG.TYPE_ITARTICLE}, field, value, from, size);
        List<ITArticle> articleList = this.getArticlesByJson(document.getJsonResult());
        document.setResult(articleList);
        return document;
    }

    private List<ITArticle> getArticlesByJson(String json) {
        List<ITArticle> articles = null;
        if (StringUtils.isNotBlank(json)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ITArticle>>() {
            }.getType();
            articles = gson.fromJson(json, type);
        }
        return articles;
    }
}
