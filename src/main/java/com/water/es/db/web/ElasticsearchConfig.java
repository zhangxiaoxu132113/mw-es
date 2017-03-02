//package com.water.es.db.web;
//
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.node.NodeBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
///**
//* Created by zhangmiaojie on 2017/3/2.
//*/
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.water.es.repositories")
//public class ElasticsearchConfig {
//    @Bean
//    public NodeBuilder nodeBuilder() {
//        return new NodeBuilder();
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        Settings.Builder elasticsearchSettings =
//                Settings.settingsBuilder()
//                        .put("http.enabled", "true") // 1
////                        .put("path.data", tmpDir.toAbsolutePath().toString()) // 2
//                        .put("path.home", "D:\\software\\elasticsearch-5.2.0"); // 3
//
//        return new ElasticsearchTemplate(nodeBuilder()
//                .local(true)
//                .settings(elasticsearchSettings.build())
//                .node()
//                .client());
//    }
//}
