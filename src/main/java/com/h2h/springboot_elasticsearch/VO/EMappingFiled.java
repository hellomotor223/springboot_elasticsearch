package com.h2h.springboot_elasticsearch.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 张杰斌 on 2018/5/23.
 */
@Data
@ApiModel(value = "EMappingFiled",description = "mapping属性数据结构")
public class EMappingFiled{
    @ApiModelProperty(value="属性名",example = "userId",notes = "如果可以直接拼在路径上")
    private String filedName;//属性名
    @ApiModelProperty(value="属性类型",example = "text",notes = "常用类型//TODO")
    private String filedType;//属性类型
    @ApiModelProperty(value="分词器类型",example = "1",notes = "null表示默认，1表示pinyin，2表示数字")
    private Integer analyzer;//分词器类型，null表示默认，1表示pinyin，2表示数字
}
