package com.water.es.db.service;

import com.water.es.db.entry.ITArticle;
import com.water.es.entry.ESDocument;

/**
 * Created by zhangmiaojie on 2017/3/2.
 */
public interface ArticleService {
    /**
     * 索引文章
     * @param article
     */
    void addArticle(com.water.es.entry.ITArticle article);

    /**
     * 搜索文章
     * @param field 搜索字段
     * @param value 搜索内容
     * @param from  偏移值
     * @param size  条目数量
     * @return      json格式数据
     */
    ESDocument searchArticleByTerm(String field, String value, int from, int size);

    /**
     *
     * @param field 搜索字段
     * @param value 搜索内容
     * @param from  偏移值
     * @param size  条目数量
     * @return      json格式数据
     */
    ESDocument searchArticleByMatch(String field, String value, int from, int size);
}
