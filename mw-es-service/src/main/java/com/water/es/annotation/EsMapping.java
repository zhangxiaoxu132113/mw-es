package com.water.es.annotation;

import java.lang.annotation.*;

/**
 * Created by mrwater on 2017/3/20.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsMapping {
    boolean isMapping() default true;

    String indexAnalyzer() default "ik_smart";

    String searchAnalyzer() default "ik_smart"; //默认使用中文分词器

    boolean isAnalyzer() default true;

    boolean isStore() default true;

}
