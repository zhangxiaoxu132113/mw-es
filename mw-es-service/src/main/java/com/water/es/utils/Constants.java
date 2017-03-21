package com.water.es.utils;

/**
 *
 * Created by zhangmiaojie on 2017/3/2.
 */
public final class Constants {
    private Constants() {

    }

    /** elasticsearch配置信息 */
    public static class ES_CONFIG {
        //索引 index
        public static final String INDEX_BLOG = "blog";


        //类型 type
        public static final String TYPE_ITARTICLE = "article";
    }

    public static class Analyzer {
        public static final String IK_SMART = "ik_smart";
        public static final String IK_MAX_WORD = "ik_max_word";
    }
}
