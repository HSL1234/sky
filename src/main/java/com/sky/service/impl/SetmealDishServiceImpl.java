package com.sky.service.impl;

import com.sky.mapper.SetmealDishMapper;
import com.sky.pojo.entity.SetmealDish;
import com.sky.service.SetmealDishService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SetmealDishServiceImpl implements SetmealDishService {
    @Resource
    private SetmealDishMapper setmealDishMapper;
    @Override
    public List<SetmealDish> getDishIdListBySetmealId(Long id) {
        return setmealDishMapper.select(SetmealDish.builder().setmealId(id).build());
    }
}