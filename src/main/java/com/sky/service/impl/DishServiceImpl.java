package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.common.constant.MessageConstant;
import com.sky.common.constant.StatusConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.exception.BaseException;
import com.sky.common.result.PageResult;
import com.sky.common.utils.AliOssUtil;
import com.sky.mapper.*;
import com.sky.pojo.dto.DishDTO;
import com.sky.pojo.dto.DishPageQueryDTO;
import com.sky.pojo.entity.*;
import com.sky.pojo.vo.DishVO;
import com.sky.service.DishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public Integer add(Dish dish) {
        dish.setCreateTime(new Date());
        dish.setUpdateTime(new Date());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dish.setStatus(StatusConstant.DISABLE);
        return dishMapper.insert(dish);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        Example example = new Example(Dish.class);
        Example.Criteria criteria = example.createCriteria();
        if (dishPageQueryDTO.getCategoryId() != null) {
            criteria.andEqualTo("categoryId", dishPageQueryDTO.getCategoryId());
        }
        if (StringUtils.isNotEmpty(dishPageQueryDTO.getName())) {
            criteria.andLike("name", "%" + dishPageQueryDTO.getName() + "%");
        }
        if (dishPageQueryDTO.getStatus() != null) {
            criteria.andEqualTo("status", dishPageQueryDTO.getStatus());
        }
        example.setOrderByClause("create_time desc");
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        List<Dish> dishes = dishMapper.selectByExample(example);
        PageInfo<Dish> dishPageInfo = new PageInfo<>(dishes);
        List<DishVO> dishVOList = dishPageInfo.getList().stream().map(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            if (dish.getCategoryId() != null) {
                dishVO.setCategoryName(categoryMapper.selectByPrimaryKey(dish.getCategoryId()).getName());
            }
            return dishVO;
        }).collect(Collectors.toList());
        return new PageResult(dishPageInfo.getTotal(), dishVOList);
    }

    @Override
    @Transactional
    public int addwithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setCreateTime(new Date());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateTime(new Date());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dish.setStatus(0);
        int i = dishMapper.insert(dish);

        Long dishId = dish.getId();// 菜品id
        // 菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors = flavors.stream().peek((item) -> item.setDishId(dishId)).collect(Collectors.toList());
            // 保存菜品口味数据到菜品口味表dish_flavor(批量添加)
            dishFlavorMapper.insertList(flavors);
        }
        return i;
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Integer status) {
        Dish dish = Dish.builder().id(id).status(status).build();

        // 当status为禁用状态（例如：0或者您定义的其他禁用状态码）时，处理套餐关联关系
        if (status.equals(StatusConstant.DISABLE)) {
            Example example = new Example(SetmealDish.class);
            example.createCriteria().andEqualTo("dishId", id);
            List<SetmealDish> setmealDishes = setmealDishMapper.selectByExample(example);

            if (!setmealDishes.isEmpty()) {
                setmealDishes.forEach(setmealDish -> {
                    Long setmealId = setmealDish.getSetmealId();
                    Setmeal setmealToUpdate = Setmeal.builder().id(setmealId).status(StatusConstant.DISABLE).build();
                    setmealMapper.updateByPrimaryKeySelective(setmealToUpdate);
                });
            }
        }

        // 不论是启用还是禁用dish，都需要更新dish的状态
        return dishMapper.updateByPrimaryKeySelective(dish);
    }


    @Override
    public List<Dish> list(Long categoryId) {
        Example example = new Example(Dish.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        return dishMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public Integer deleteList(String ids) {
        String[] idList = ids.split(",");
        Example example = new Example(SetmealDish.class);
        Example.Criteria criteria = example.createCriteria();
        for (String id : idList) {
            // 起售中的菜品不能删除
            if (dishMapper.selectByPrimaryKey(id).getStatus() == 1) {
                throw new BaseException(MessageConstant.DISH_ON_SALE);
            }
            // 被套餐关联的菜品不能删除
            criteria.orEqualTo("dishId",id);
            if (!setmealDishMapper.selectByExample(example).isEmpty()){
                throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
            // 删除菜品后，关联的口味数据也需要删除掉
            dishFlavorMapper.deleteByExample(example);
            // 删除oss菜品图片
            Dish dish = dishMapper.selectByPrimaryKey(id);
            String objectName = StringUtils.substringAfterLast(dish.getImage(), "/");
            AliOssUtil.delete(objectName);
        }
        return dishMapper.deleteByIds(ids);
    }

    @Override
    public DishVO listById(Long id) {
        Dish dish = dishMapper.selectByPrimaryKey(id);
        DishVO dishVO = new DishVO();

        // 查询口味
        Example example = new Example(DishFlavor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("dishId",id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByExample(example);

        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        // 查询分类名称
        Category category = categoryMapper.selectByPrimaryKey(dish.getCategoryId());
        dishVO.setCategoryName(category.getName());

        return dishVO;
    }

    @Override
    @Transactional
    public int update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dish.setUpdateTime(new Date());
        dish.setUpdateUser(BaseContext.getCurrentId());

        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 删除原有口味
        Example example = new Example(DishFlavor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("dishId",dish.getId());
        dishFlavorMapper.deleteByExample(example);
        // 将修改后的口味加入
        dishFlavorMapper.insertList(flavors);
        return dishMapper.updateByPrimaryKeySelective(dish);
    }

    @Override
    public Integer countByStatus(Integer status) {
        return dishMapper.selectCount(Dish.builder().status(status).build());
    }
}