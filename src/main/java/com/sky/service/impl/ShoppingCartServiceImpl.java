package com.sky.service.impl;

import com.sky.common.context.BaseContext;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.pojo.dto.ShoppingCartDTO;
import com.sky.pojo.entity.Dish;
import com.sky.pojo.entity.Setmeal;
import com.sky.pojo.entity.ShoppingCart;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.selectAll();
    }

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(shoppingCart);
        if (shoppingCartDTO.getDishId() != null) {
            Dish dish = dishMapper.selectByPrimaryKey(shoppingCartDTO.getDishId());
            shoppingCart.setAmount(dish.getPrice());
            if (shoppingCart1 != null) {
                shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
                shoppingCartMapper.updateByPrimaryKeySelective(shoppingCart1);
            } else {
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setCreateTime(new Date());
                shoppingCart.setNumber(1);
                shoppingCartMapper.insert(shoppingCart);
            }
        } else if (shoppingCartDTO.getSetmealId() != null) {
            Setmeal setmeal = setmealMapper.selectByPrimaryKey(shoppingCartDTO.getSetmealId());
            if (shoppingCart1 != null) {
                shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
                shoppingCart1.setAmount(setmeal.getPrice());
                shoppingCartMapper.updateByPrimaryKeySelective(shoppingCart1);
            } else {
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setCreateTime(new Date());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCartMapper.insert(shoppingCart);
            }
        }
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(shoppingCart);
        shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
        if (shoppingCart1.getNumber() == 0) {
            shoppingCartMapper.deleteByPrimaryKey(shoppingCart1.getId());
            return;
        }
        shoppingCartMapper.updateByPrimaryKeySelective(shoppingCart1);
    }

    @Override
    public void clean() {
        Example example = new Example(ShoppingCart.class);
        example.createCriteria().andEqualTo("userId", BaseContext.getCurrentId());
        shoppingCartMapper.deleteByExample(example);
    }
}