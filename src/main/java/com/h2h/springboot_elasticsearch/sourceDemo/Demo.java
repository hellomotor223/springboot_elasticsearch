package com.h2h.springboot_elasticsearch.sourceDemo;

import com.h2h.springboot_elasticsearch.VO.EMappingVO;
import com.h2h.springboot_elasticsearch.util.ElasticsearchUtil;
import io.swagger.annotations.Api;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by 张杰斌 on 2018/5/24.
 */
@Api(value = "索引操作Controller", tags = {"索引操作接口"}, hidden = true)
public class Demo {

    @Autowired
    private TransportClient client;

    /**
     * 添加mapping源代码
     */
    public void createMapping(@PathVariable("index") String index, @RequestBody EMappingVO vo)throws Exception{
        new XContentFactory();
        XContentBuilder builder=XContentFactory.jsonBuilder()
                .startObject()
                .startObject("friend")//type
                .startObject("properties")//固定
                    //以下为自定义属性
                    .startObject("userId").field("type", "text").endObject()
                    .startObject("friendId").field("type", "text").endObject()
                    .startObject("friendHeadimg").field("type", "text").endObject()
                    .startObject("friendName").field("type", "text")
                        //pinyin分词器 需要setting中提前配置好
                        .startObject("fields")
                            .startObject("pinyin").field("type","text").field("store","no").field("term_vector","with_positions_offsets").field("analyzer","ik_pinyin_analyzer").field("boost",10)
                            .endObject()
                        .endObject()
                    .endObject()
                    .startObject("friendPhone").field("type", "text")
                        //手机号邮箱
                        .field("analyzer","index_email_analyzer").field("search_analyzer","search_email_analyzer")
                    .endObject()
                    //自定义属性结束
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest("user2").type("friend").source(builder);
        client.admin().indices().putMapping(mapping).actionGet();

        ElasticsearchUtil.updateMapping(client,index,vo);
    }
}
