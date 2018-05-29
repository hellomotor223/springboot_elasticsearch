package com.h2h.springboot_elasticsearch.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by 张杰斌 on 2018/5/23.
 */
@Data
@ApiModel(value = "EMappingVO",description = "初始化mapping数据结构")
public class EMappingVO {
    @ApiModelProperty(value="索引名",example = "user",notes = "如果可以直接拼在路径上")
    private String index;//索引，如果可以直接拼在路径上
    @ApiModelProperty(value="文档类型",example = "friend")
    private String type;
    private List<EMappingFiled> fileds;

}
