package com.h2h.springboot_elasticsearch.util;

import com.h2h.springboot_elasticsearch.VO.EMappingFiled;
import com.h2h.springboot_elasticsearch.VO.EMappingVO;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.ObjectMapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;

/**
 * 描述：Elacticsearch操作类
 *
 * Created by 张杰斌 on 2018/5/23.
 */
public class ElasticsearchUtil {

    /**
     *
     * 功能描述: 添加索引，带有pinyin分词器与数字
     *
     * @param: index 索引名
     * @auther: 张杰斌
     * @date: 2018/5/23 14:23
     */
    public static boolean createIndex(TransportClient client ,String indexName){
        try {
            XContentBuilder builder=XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("index")
                    .startObject("analysis")
                    .startObject("analyzer")
                    .startObject("ik_pinyin_analyzer")
                    .field("type","custom").field("tokenizer","ik_smart").startArray("filter").value("my_pinyin").value("word_delimiter").endArray()
                    .endObject()
                    .startObject("email_url_analyzer")
                    .field("type","custom").field("tokenizer","uax_url_email").startArray("filter").value("trim").endArray()
                    .endObject()
                    .startObject("index_email_analyzer")
                    .field("type","custom").field("tokenizer","standard").startArray("filter").value("lowercase").value("name_ngram_filter").value("trim").endArray()
                    .endObject()
                    .startObject("search_email_analyzer")
                    .field("type","custom").field("tokenizer","standard").startArray("filter").value("lowercase").value("trim").endArray()
                    .endObject()
                    .endObject()
                    .startObject("char_filter")
                    .startObject("digit_only").field("type","pattern_replace").field("pattern","\\D+").field("replacement","").endObject()
                    .endObject()
                    .startObject("tokenizer")
                    .startObject("digit_edge_ngram_tokenizer").field("type","edgeNGram").field("min_gram",1).field("max_gram",15).startArray("token_chars").value("digit").endArray()
                    .endObject()
                    .endObject()
                    .startObject("filter")
                    .startObject("my_pinyin")
                    .field("type","pinyin").field("first_letter","prefix").field("padding_char"," ")
                    .endObject()
                    .startObject("name_ngram_filter")
                    .field("type","ngram").field("min_gram",1).field("max_gram",20)
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            client.admin().indices().prepareCreate(indexName).setSettings(builder).execute().actionGet();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * 功能描述: 更新索引mapping
     *
     * @param:
     * @return:
     * @auther: 张杰斌
     * @date: 2018/5/23 14:24
     */
    public static void updateMapping(TransportClient client,String indexName, EMappingVO mappingVO)throws IOException{
        new XContentFactory();
        XContentBuilder builder=XContentFactory.jsonBuilder();
        builder.startObject();
        builder.startObject(mappingVO.getType());
        builder .startObject("properties");
        for (EMappingFiled filed:mappingVO.getFileds()) {
            if(filed.getAnalyzer()==null){
                builder.startObject(filed.getFiledName()).field("type", filed.getFiledType()).endObject();
            }else if(filed.getAnalyzer()==1){
                builder.startObject(filed.getFiledName()).field("type", filed.getFiledType());
                builder.startObject("fields");
                builder.startObject("pinyin").field("type","keyword").field("store","no").field("term_vector","with_positions_offsets").field("analyzer","ik_pinyin_analyzer").field("boost",10);
                builder.endObject();
                builder.endObject();
                builder.endObject();
            }else if(filed.getAnalyzer()==2){
                builder.startObject(filed.getFiledName()).field("type", filed.getFiledType())
                        .field("analyzer","index_email_analyzer").field("search_analyzer","search_email_analyzer")
                        .endObject();
            }
        }
        builder.endObject();
        builder.endObject();
        builder.endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(mappingVO.getType()).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
    }

    /**
     *
     * 功能描述:根据index与type删除类型下所有数据
     *
     * @param:
     * @return:
     * @auther: xiaobo
     * @date: 2018/5/24 12:57
     */
    public static void deleteByIndexAndType(TransportClient client,String index, String type) throws Exception{
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setFrom(0).setSize(1000);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchAllQuery());

        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse response = searchRequestBuilder.execute().get();
        for(SearchHit hit : response.getHits()){
            String id = hit.getId();
            bulkRequest.add(client.prepareDelete(index, type, id).request());
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for(BulkItemResponse item : bulkResponse.getItems()){
                System.out.println(item.getFailureMessage());
            }
        } else {
            System.out.println("数据删除成功");
        }
    }

    /**
     *
     * 功能描述: 根据queryBuilder条件查询
     *
     * @param:
     * @return:
     * @auther: xiaobo
     * @date: 2018/5/24 13:12
     */
    public static Map<String,Object> queryByBuilder(TransportClient client, String index, String type, QueryBuilder queryBuilder){
        //TODO 分页暂未实现
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setFrom(0).setSize(100);
        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse response =  searchRequestBuilder.execute().actionGet();
        return getHits(response);
    }

    /**
     *
     * 功能描述:获取response中hits数据
     *
     * @param:
     * @return:
     * @auther: xiaobo
     * @date: 2018/5/24 13:17
     */
    private static Map<String,Object> getHits(SearchResponse response){
        List<Map<String,Object>> result = new ArrayList<>();
        SearchHits hits = response.getHits();
        SearchHit[] searchHists = hits.getHits();
        for (SearchHit sh : searchHists) {
            result.add(sh.getSource());
        }
        Object[] a = result.toArray();
        Arrays.sort(a, (Comparator)new PinyinComparator());
        Sort s = new Sort();
        ChineseNameIndex chineseNameIndex = new ChineseNameIndex();
        for (Object e : a) {
            String str = s.String2AlphaFirst(((Map<String, Object>) e).get("friendName").toString(), "b");
            ArrayList arrayList = (ArrayList) chineseNameIndex.get(str);
            arrayList.add(e);
        }
//        Collections.sort(result, new PinyinComparator());
        return chineseNameIndex;
    }

    /**
     * 根据条件删除多个document
     * 【注意:检查条件是否唯一，以免误删】
     * @param client
     * @param index
     * @param type
     * @param queryBuilder
     */
    public static void deleteByQuery(TransportClient client, String index, String type, BoolQueryBuilder queryBuilder) throws Exception{
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setFrom(0).setSize(1000);

        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse response = searchRequestBuilder.execute().get();
        if(response.getHits().totalHits==0){
            throw new Exception("参数错误");
        }
        for(SearchHit hit : response.getHits()){
            String id = hit.getId();
            bulkRequest.add(client.prepareDelete(index, type, id).request());
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for(BulkItemResponse item : bulkResponse.getItems()){
                System.out.println(item.getFailureMessage());
            }
        } else {
            System.out.println("数据删除成功");
        }

    }

    /**
     * 根据条件修改document
     * @param client
     * @param index
     * @param type
     * @param queryBuilder
     */
    /*public static void updateByQuery(TransportClient client, String index, String type, BoolQueryBuilder queryBuilder){

    }*/

    /**
     * 根据id修改document
     * @param client
     * @param index
     * @param type
     * @param id
     */
    public static void updateById(TransportClient client, String index, String type, String id,Map data){
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareUpdate(index, type, id).setDoc(data)).get();
    }

    /**
     * 根据条件修改document
     * @param client
     * @param index
     * @param type
     * @param queryBuilder
     * @param data
     */
    public static void updateByQuery(TransportClient client, String index, String type, QueryBuilder queryBuilder,Map data) throws Exception{
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setQuery(queryBuilder);
        SearchResponse response =  searchRequestBuilder.execute().actionGet();
        if(response.getHits().totalHits!=1){
            throw new Exception("数据异常");
        }
        String id = response.getHits().getHits()[0].getId();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareUpdate(index, type, id).setDoc(data)).get();
    }
}


