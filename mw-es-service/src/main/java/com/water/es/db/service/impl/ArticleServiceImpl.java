package com.water.es.db.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.water.es.api.Service.IArticleService;
import com.water.es.entry.ITArticle;
import com.water.es.utils.Constants;
import com.water.es.utils.ElasticSearchTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by zhangmiaojie on 2017/3/3.
 */
@Service("esArticleService")
public class ArticleServiceImpl implements IArticleService {

    @Resource(name = "elasticsearchTemplate")
    private ElasticSearchTemplate elasticSearchTemplate;

    @Override
    public void addArticle(ITArticle article) {
        if (article == null || StringUtils.isBlank(article.getId())) {
            throw new RuntimeException("参数不合法！");
        }
        elasticSearchTemplate.addDocument(JSONObject.toJSONString(article), Constants.ES_CONFIG.INDEX_BLOG, Constants.ES_CONFIG.TYPE_ITARTICLE, article.getId());
    }
}
