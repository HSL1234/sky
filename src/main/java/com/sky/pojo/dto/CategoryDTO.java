package com.sky.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel(description = "分类数据传输对象")
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO implements Serializable {

    @ApiModelProperty("id")
    // 主键
    private Long id;

    @ApiModelProperty("类型 1 菜品分类 2 套餐分类")
    // 类型 1 菜品分类 2 套餐分类
    private Integer type;

    @ApiModelProperty("分类名称")
    // 分类名称
    private String name;

    @ApiModelProperty("排序")
    // 排序
    private Integer sort;

}
