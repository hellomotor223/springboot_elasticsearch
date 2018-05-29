package com.h2h.springboot_elasticsearch.VO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * Created by 张杰斌 on 2018/5/24.
 */
@Data
@ApiModel(value = "EUpdateFieldVO",description = "修改document数据结构")
public class EUpdateQueryFieldVO {
    private List<EQueryFieldVO> query;
    private List<EQueryFieldVO> data;
}
