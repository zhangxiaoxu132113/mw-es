package com.water.es.utils;

import org.elasticsearch.client.Client;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * Created by zhangmiaojie on 2017/3/2.
 */
public class ElesticsearchExtTemplate extends ElasticsearchTemplate {
    public ElesticsearchExtTemplate(Client client) {
        super(client);
    }
}
