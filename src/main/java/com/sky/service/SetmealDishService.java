package com.sky.service;

import com.sky.pojo.entity.SetmealDish;

import java.util.List;

public interface SetmealDishService {
    List<SetmealDish> getDishIdListBySetmealId(Long id);
}
