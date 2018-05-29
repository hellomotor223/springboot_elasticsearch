package com.h2h.springboot_elasticsearch.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by 张杰斌 on 2018/5/24.
 */
@Data
@ApiModel(value = "EUpdateFieldVO",description = "修改document数据结构")
public class EUpdateFieldVO {
    @ApiModelProperty(value="文档id",example = "AWORyKJ9Prt8kR8G8S0K",notes = "")
    private String id;
    private List<EQueryFieldVO> data;
}
