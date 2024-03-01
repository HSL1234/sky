package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.pojo.dto.SetmealDTO;
import com.sky.pojo.dto.SetmealPageQueryDTO;
import com.sky.pojo.entity.Setmeal;
import com.sky.pojo.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void add(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void updateStatus(Long id, Integer status);

    SetmealVO selectById(Long id);

    void deleteByIds(String ids);

    void update(SetmealDTO setmealDTO);

    List<Setmeal> listByCategoryId(Long categoryId);

    Integer countByStatus(Integer status);
}
