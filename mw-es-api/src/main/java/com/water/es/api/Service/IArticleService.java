package com.water.es.api.Service;

import com.water.es.entry.ITArticle;

/**
 * Created by zhangmiaojie on 2017/3/3.
 */
public interface IArticleService {
    void addArticle(ITArticle article);

    String searchArticleByTerm(String field, String value, int from, int size);

    String searchArticleByMatch(String field, String value, int from, int size);
}
