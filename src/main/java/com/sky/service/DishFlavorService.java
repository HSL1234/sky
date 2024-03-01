package com.sky.service;

import com.sky.pojo.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {
    List<DishFlavor> listByDishId(Long dishId);
}
