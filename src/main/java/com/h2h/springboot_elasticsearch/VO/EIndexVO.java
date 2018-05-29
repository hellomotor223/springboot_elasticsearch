package com.h2h.springboot_elasticsearch.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 张杰斌 on 2018/5/23.
 */
@Data
@ApiModel(value = "EIndexVO",description = "新增索引数据结构")
public class EIndexVO {
    @ApiModelProperty(value = "索引名称",example = "user",notes = "索引名不能包含 [ , \", *, \\, <, |, ,, >, /, ?]")
    private String index;
    @ApiModelProperty(value="文档类型",example = "friend")
    private String type;
}
