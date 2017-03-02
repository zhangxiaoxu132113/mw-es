package com.water.es.repositories;

import com.water.es.db.entry.ITArticle;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
* IT文章操作
* Created by zhangmiaojie on 2017/3/2.
*/
public interface ArticleRepositories extends PagingAndSortingRepository<ITArticle, String> {
}
