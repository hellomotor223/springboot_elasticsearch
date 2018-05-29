package com.h2h.springboot_elasticsearch.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 张杰斌 on 2018/5/24.
 */
@Data
@ApiModel(value = "EQueryFieldVO",description = "查询与删除属性数据结构")
public class EQueryFieldVO {
    @ApiModelProperty(value="属性名",example = "friendId",notes = "")
    private String filedName;//属性名
    @ApiModelProperty(value="属性值",example = "1234",notes = "")
    private String text;//属性值
}
