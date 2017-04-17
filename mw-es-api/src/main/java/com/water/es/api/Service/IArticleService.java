package com.water.es.api.Service;

import com.water.es.entry.ESDocument;
import com.water.es.entry.ITArticle;

import java.util.List;

/**
 * Created by zhangmiaojie on 2017/3/3.
 */
public interface IArticleService {
    void addArticle(ITArticle article);

    ESDocument searchArticleByTerm(String field, String value, int from, int size);

    ESDocument searchArticleByMatch(String field, String value, int from, int size);

    ESDocument searchArticleByMatchWithHighLight(String[] field, String value, int from, int size);
}
