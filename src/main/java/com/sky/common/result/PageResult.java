package com.sky.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分页查询结果传输对象")
public class PageResult implements Serializable {

    @ApiModelProperty("总条数")
    private long total; //总记录数

    @ApiModelProperty("查询结果对象集合")
    private List records; //当前页数据集合

}
