package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.common.constant.MessageConstant;
import com.sky.common.constant.StatusConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.exception.DeletionNotAllowedException;
import com.sky.common.exception.SetmealEnableFailedException;
import com.sky.common.result.PageResult;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.pojo.dto.SetmealDTO;
import com.sky.pojo.dto.SetmealPageQueryDTO;
import com.sky.pojo.entity.Dish;
import com.sky.pojo.entity.Setmeal;
import com.sky.pojo.entity.SetmealDish;
import com.sky.pojo.vo.SetmealVO;
import com.sky.service.SetmealService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmeal.setCreateUser(BaseContext.getCurrentId());
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmeal.setCreateTime(new Date());
        setmeal.setUpdateTime(new Date());
        setmealMapper.insert(setmeal);
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishList.forEach(e -> {
            e.setSetmealId(setmeal.getId());
            e.setPrice(setmeal.getPrice());
        });
        setmealDishMapper.insertList(setmealDishList);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Example example = new Example(Setmeal.class);
        Example.Criteria criteria = example.createCriteria();
        if (setmealPageQueryDTO.getCategoryId() != null) {
            criteria.andEqualTo("categoryId", setmealPageQueryDTO.getCategoryId());
        }
        if (StringUtils.isNotEmpty(setmealPageQueryDTO.getName())) {
            criteria.andLike("name", "%" + setmealPageQueryDTO.getName() + "%");
        }
        if (setmealPageQueryDTO.getStatus() != null) {
            criteria.andEqualTo("status", setmealPageQueryDTO.getStatus());
        }
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<Setmeal> setmeals = setmealMapper.selectByExample(example);
        PageInfo<Setmeal> pageInfo = new PageInfo<>(setmeals);
        List<SetmealVO> setmealVOList = pageInfo.getList().stream().map(setmeal -> {
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmeal, setmealVO);
            if (setmeal.getCategoryId() != null) {
                setmealVO.setCategoryName(categoryMapper.selectByPrimaryKey(setmeal.getCategoryId()).getName());
            }
            return setmealVO;
        }).collect(Collectors.toList());
        return new PageResult(pageInfo.getTotal(), setmealVOList);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        if (Objects.equals(status, StatusConstant.ENABLE)) {
            Example example = new Example(SetmealDish.class);
            example.createCriteria().andEqualTo("setmealId", id);
            List<SetmealDish> setmealDishes = setmealDishMapper.selectByExample(example);
            ArrayList<Dish> disabledDishs = new ArrayList<>();
            setmealDishes.forEach(e -> {
                Dish dish = dishMapper.selectByPrimaryKey(e.getDishId());
                if (Objects.equals(dish.getStatus(), StatusConstant.DISABLE)) {
                    disabledDishs.add(dish);
                }
            });
            if (disabledDishs.isEmpty()) {
                setmealMapper.updateByPrimaryKeySelective(setmeal);
            } else {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }else {
            setmealMapper.updateByPrimaryKeySelective(setmeal);
        }
    }

    @Override
    public SetmealVO selectById(Long id) {
        Setmeal setmeal = setmealMapper.selectByPrimaryKey(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setCategoryName(categoryMapper.selectByPrimaryKey(setmeal.getCategoryId()).getName());
        Example example = new Example(SetmealDish.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("setmealId", id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectByExample(example);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    @Transactional
    public void deleteByIds(String ids) {
        String[] idList = ids.split(",");
        for (String id : idList) {
            Setmeal setmeal = setmealMapper.selectByPrimaryKey(id);
            if (setmeal.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            Example.Criteria criteria = new Example(SetmealDish.class).createCriteria().andEqualTo("setmealId", id);
            setmealDishMapper.deleteByExample(criteria);
        }
        setmealMapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmeal.setUpdateTime(new Date());
        setmealMapper.updateByPrimaryKeySelective(setmeal);
        Example example = new Example(SetmealDish.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("setmealId", setmeal.getId());
        setmealDishMapper.deleteByExample(example);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(e -> {
            e.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertList(setmealDishes);
    }

    @Override
    public List<Setmeal> listByCategoryId(Long categoryId) {
        Setmeal setmeal = Setmeal.builder().categoryId(categoryId).build();
        return setmealMapper.select(setmeal);
    }

    @Override
    public Integer countByStatus(Integer status) {
        return setmealMapper.selectCount(Setmeal.builder().status(status).build());
    }
}