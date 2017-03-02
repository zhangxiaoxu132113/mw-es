package com.water.es.db.service.impl;

import com.water.es.db.service.ArticleService;
import com.water.es.repositories.ArticleRepositories;
import com.water.es.utils.ElesticsearchExtTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 *
 * Created by zhangmiaojie on 2017/3/2.
 */
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ElesticsearchExtTemplate elesticsearchExtTemplate;

    @Resource
    private ArticleRepositories articleRepositories;

    @Override
    public void test() {

    }
}
