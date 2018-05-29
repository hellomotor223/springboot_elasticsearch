package com.h2h.springboot_elasticsearch.controller;

import com.google.gson.Gson;
import com.h2h.springboot_elasticsearch.VO.*;
import com.h2h.springboot_elasticsearch.service.UserService;
import com.h2h.springboot_elasticsearch.util.ElasticsearchUtil;
import com.h2h.springboot_elasticsearch.util.StringUtil;
import io.swagger.annotations.*;
import org.apache.ibatis.annotations.Update;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Api(value = "索引管理Controller", tags = {"索引操作接口"}, hidden = false)
public class ElasticSearchController {

    @Autowired
    private TransportClient client;

    @Autowired
    UserService userService;

    /**
     * 创建索引
     * @param indexVO 只需要索引名
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名称", dataType = "String", defaultValue = "book", paramType = "path"),
    })
    @ApiOperation(value = "新增索引")
    @PutMapping("es/index")
    public ResponseEntity get(@RequestBody EIndexVO indexVO){
        if(!StringUtil.isIndexName(indexVO.getIndex())){
            return new ResponseEntity("索引名不能包含 [ , \", *, \\, <, |, ,, >, /, ?]",HttpStatus.BAD_REQUEST);
        }
        try {
            ElasticsearchUtil.createIndex(client,indexVO.getIndex());
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
     *  vo测试数据
     *      {"type":"friend","index":"user2","fileds":[{
                 "filedName":"userId",
                 "filedType":"text"
                 },{
                 "filedName":"friendId",
                 "filedType":"text"
                 },{
                 "filedName":"friendName",
                 "filedType":"text",
                 "analyzer":1
                 },{
                 "filedName":"friendHeadimg",
                 "filedType":"text"
                 },{
                 "filedName":"friendPhone",
                 "filedType":"text",
                 "analyzer":2
                 }]}
     * @param index 索引名称；
     * @param vo 索引类型
     * @throws Exception
     */
    @ApiOperation(value = "初始化索引mapping")
    @PutMapping("es/{index}/mapping")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名称", dataType = "String", defaultValue = "book", paramType = "path"),
    })
    public ResponseEntity createMapping(@PathVariable("index") String index,@RequestBody EMappingVO vo)throws Exception{
        if(!StringUtil.isIndexName(index)){
            return new ResponseEntity("索引名不能包含 [ , \", *, \\, <, |, ,, >, /, ?]",HttpStatus.BAD_REQUEST);
        }
        try {
            ElasticsearchUtil.updateMapping(client,index,vo);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     *
     * 功能描述: 批量导入好友数据
     *
     * @param:
     * @return:
     * @auther: xiaobo
     * @date: 2018/5/24 11:50
     */
    @ApiOperation(value = "根据用户id 批量导入好友数据")
    @PutMapping("es/user/friend")
    public ResponseEntity add(@RequestBody EIndexVO esVO){
        Gson gson = new Gson();
        List<Map<String,Object>> list = userService.getUsers();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        list.forEach(map -> {
            bulkRequest.add(client.prepareIndex(esVO.getIndex(), esVO.getType()).setSource(gson.toJson(map)));
        });
        BulkResponse bulkResponse = bulkRequest.get();
        int status = bulkResponse.status().getStatus();
        if(status==200){
            return new ResponseEntity(HttpStatus.OK);
        }else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     *
     * 功能描述: 根据手机号查询用户  首字母排序前端来实现
     *
     * @param:
     * @return:
     * @auther: xiaobo
     * @date: 2018/5/24 11:50
     */
    @ApiOperation(value = "根据 手机号/姓名/昵称 查询用户")
    @GetMapping("es/user")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键词", dataType = "String", defaultValue = "106", paramType = "query"),
    })
    public Object search(String word){

        List<Map<String,Object>> result = new ArrayList<>();
        String userId="6f1751cd499d49cd83491950cc0c0aff";
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.queryStringQuery(userId).field("userId"));
        queryBuilder.must(QueryBuilders.multiMatchQuery(word,"friendName.pinyin","friendName","friendNickname.pinyin","friendNickname","friendPhone"));//匹配多个字段

//        queryBuilder.must(QueryBuilders.queryStringQuery(word).field("friendName"));//匹配一个字段
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.queryStringQuery(userId).field("userId"));
//
//        ScoreFunctionBuilder<?> scoreFunctionBuilder = ScoreFunctionBuilders.fieldValueFactorFunction("sales").modifier(FieldValueFactorFunction.Modifier.LN1P).factor(0.1f);
//        FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery(queryBuilder,scoreFunctionBuilder).boostMode(CombineFunction.SUM);

        return ElasticsearchUtil.queryByBuilder(client, "user", "friend", queryBuilder);
    }


    /**
     *
     * 功能描述: 删除索引下类型的所有数据
     * 注意： 测试用，上线不应开放，或者需要复杂的校验
     * @param:
     * @return:
     * @auther: xiaobo
     * @date: 2018/5/24 11:45
     */
    @ApiOperation(value = "删除索引下类型的所有数据")
    @DeleteMapping("es/{index}/{type}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名称", dataType = "String", defaultValue = "book", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "类型", dataType = "String", defaultValue = "novel", paramType = "path"),
    })
    public ResponseEntity deleteType(@PathVariable("index") String index, @PathVariable("type") String type){
        try {
            ElasticsearchUtil.deleteByIndexAndType(client,index,type);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件删除索引下类型的指定数据")
    @DeleteMapping("es/{index}/{type}/delete_query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名称", dataType = "String", defaultValue = "book", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "类型", dataType = "String", defaultValue = "novel", paramType = "path"),
    })
    public ResponseEntity deleteFriend(@PathVariable("index") String index, @PathVariable("type") String type,@RequestBody List<EQueryFieldVO> queryFieldVOs){
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.matchQuery("userId","6f1cd499d49cd83491950cc0c0aff"));
            BoolQueryBuilder shouldQueryBuilder = QueryBuilders.boolQuery();
            for (EQueryFieldVO queryFieldVo:queryFieldVOs) {
                shouldQueryBuilder.should(QueryBuilders.matchQuery(queryFieldVo.getFiledName(),queryFieldVo.getText()));
            }
            queryBuilder.must(shouldQueryBuilder);
            ElasticsearchUtil.deleteByQuery(client,index,type,queryBuilder);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据id修改document数据")
    @PostMapping("es/{index}/{type}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名称", dataType = "String", defaultValue = "book", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "类型", dataType = "String", defaultValue = "novel", paramType = "path"),
    })
    public ResponseEntity updateFriendById(@PathVariable("index") String index, @PathVariable("type") String type,@RequestBody EUpdateFieldVO updateFieldVO){
        try {
            Map<String, Object> map = new HashMap<>();
            for (EQueryFieldVO queryFieldVO: updateFieldVO.getData()) {
                map.put(queryFieldVO.getFiledName(),queryFieldVO.getText());
            }
            ElasticsearchUtil.updateById(client,index,type,updateFieldVO.getId(),map);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "根据条件修改document数据")
    @PostMapping("es/{index}/{type}/update_query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "索引名称", dataType = "String", defaultValue = "book", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "类型", dataType = "String", defaultValue = "novel", paramType = "path"),
    })
    public ResponseEntity updateFriendByQuery(@PathVariable("index") String index, @PathVariable("type") String type,@RequestBody EUpdateQueryFieldVO updateQueryFieldVO){
        try {
            Map<String, Object> map = new HashMap<>();
            for (EQueryFieldVO queryFieldVO: updateQueryFieldVO.getData()) {
                map.put(queryFieldVO.getFiledName(),queryFieldVO.getText());
            }
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.queryStringQuery("6f1751cd499d49cd83491950cc0c0aff").field("userId"));
            for (EQueryFieldVO queryFieldVO: updateQueryFieldVO.getQuery()) {
                queryBuilder.must(QueryBuilders.queryStringQuery(queryFieldVO.getText()).field(queryFieldVO.getFiledName()));
            }
            ElasticsearchUtil.updateByQuery(client,index,type,queryBuilder,map);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
