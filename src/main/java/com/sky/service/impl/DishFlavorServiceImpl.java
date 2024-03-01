package com.sky.service.impl;

import com.sky.mapper.DishFlavorMapper;
import com.sky.pojo.entity.DishFlavor;
import com.sky.service.DishFlavorService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DishFlavorServiceImpl implements DishFlavorService {
    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public List<DishFlavor> listByDishId(Long dishId) {
        DishFlavor dishFlavor = DishFlavor.builder().dishId(dishId).build();
        return dishFlavorMapper.select(dishFlavor);
    }
}