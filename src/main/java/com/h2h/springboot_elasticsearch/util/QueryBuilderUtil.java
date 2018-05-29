package com.h2h.springboot_elasticsearch.util;

import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashMap;

/**
 * 描述：构建查询条件工具类
 *
 * Created by 张杰斌 on 2018/5/24.
 */
public class QueryBuilderUtil {

    /**
     * 匹配所有
     * @return
     */
    public static QueryBuilder matchAllQuery(){
        return QueryBuilders.matchAllQuery();
    }

    /**
     * 模糊查询 习语匹配
     * @param filedName
     * @param text
     * @return
     */
    public static QueryBuilder matchQuery(String filedName,Object text){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(filedName, text);
        return matchQueryBuilder;
    }

    /**
     * 把查询条件当作一个整体去查询
     * @param filedName
     * @param text
     * @return
     */
    public static QueryBuilder matchPhraseQuery(String filedName,Object text){
        return QueryBuilders.matchPhraseQuery(filedName,text);
    }

    /**
     * 匹配多个条件
     * @param text
     * @param filedNames
     * @return
     */
    public static QueryBuilder multiMatchQuery(Object text,String[] filedNames){
        return QueryBuilders.multiMatchQuery(text,filedNames);
    }

    /**
     * 根据语法查询
     * @param queryString example queryString = ”(ElasticSearch AND 入门) OR Java OR python“
     * @param fieldNames 可以指定字段，不需要指定传null或者空数组
     * @return
     */
    public static QueryBuilder queryStringQuery(String queryString,String[] fieldNames){
        HashMap<String, Float> fields = new HashMap<>();
        if(fieldNames!=null && fieldNames.length>0){
            for (String fieldName:fieldNames) {
                fields.put(fieldName,1.0F);
            }
        }else {
            fieldNames= new String[]{};
        }
        return QueryBuilders.queryStringQuery(queryString).fields(fields);
    }

    /**
     * 单条件固定分数查询，查询到的分数都是1
     * @param score 分数 若非正数 默认为1
     * @param name 属性名
     * @param text 属性值
     * @return
     */
    public static QueryBuilder constantScoreMatchQuery(Float score,String name,String text){
        ConstantScoreQueryBuilder constantScoreQueryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.matchQuery(name,text));
        if(score!=null || score>0){
            constantScoreQueryBuilder.boost(score);
        }
        return constantScoreQueryBuilder;
    }

    /**
     *  多条件匹配固定分数查询，查询到的分数为 score
     * @param score  分数 若非正数 默认为1
     * @param text  属性值
     * @param fieldNames    需要匹配的属性数组
     * @return
     */
    public static QueryBuilder constantScoreMultiMatchQuery(Float score,String text,String[] fieldNames){
        ConstantScoreQueryBuilder constantScoreQueryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.multiMatchQuery(text,fieldNames));
        if(score!=null || score>0){
            constantScoreQueryBuilder.boost(score);
        }
        return constantScoreQueryBuilder;
    }
}
