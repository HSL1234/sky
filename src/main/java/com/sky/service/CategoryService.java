package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.pojo.dto.CategoryPageQueryDTO;
import com.sky.pojo.entity.Category;

import java.util.List;

public interface CategoryService{
    /**
     * 添加分类
     * @param category
     * @return
     */
    Integer add(Category category);

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 禁用/启用
     * @param status
     * @param id
     * @return
     */
    Integer updateStatus(Integer status, Long id);

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    Integer delete(Long id);

    /**
     * 修改分类
     * @param category
     * @return
     */
    Integer update(Category category);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
