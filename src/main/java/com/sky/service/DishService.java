package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.pojo.dto.DishDTO;
import com.sky.pojo.dto.DishPageQueryDTO;
import com.sky.pojo.entity.Dish;
import com.sky.pojo.vo.DishVO;

import java.util.List;

public interface DishService {
    Integer add(Dish dish);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    int addwithFlavor(DishDTO dishDTO);

    int updateStatus(Long id, Integer status);

    List<Dish> list(Long categoryId);

    Integer deleteList(String ids);

    DishVO listById(Long id);

    int update(DishDTO dishDTO);

    Integer countByStatus(Integer status);
}
