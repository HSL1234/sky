package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.common.result.PageResult;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.pojo.dto.CategoryPageQueryDTO;
import com.sky.pojo.entity.Category;
import com.sky.pojo.entity.Dish;
import com.sky.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private DishMapper dishMapper;

    @Override
    public Integer add(Category category) {
        return categoryMapper.insert(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        if (StringUtils.isNotEmpty(categoryPageQueryDTO.getName())) {
            criteria.andLike("name", "%" + categoryPageQueryDTO.getName() + "%");
        }
        if (categoryPageQueryDTO.getType() != null) {
            criteria.andEqualTo("type", categoryPageQueryDTO.getType());
        }
        example.setOrderByClause("sort ASC, create_time DESC");
        List<Category> categories = categoryMapper.selectByExample(example);
        PageInfo<Category> categoryPageInfo = new PageInfo<>(categories);
        return new PageResult(categoryPageInfo.getTotal(), categories);
    }

    @Override
    public Integer updateStatus(Integer status, Long id) {
        Category category = Category.builder().id(id).status(status).build();
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public Integer delete(Long id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        Example example = new Example(Dish.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", id);
        List<Dish> dishes = dishMapper.selectByExample(example);
        if (category.getStatus() == 0 && dishes.isEmpty()) {
            return categoryMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    @Override
    public Integer update(Category category) {
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public List<Category> list(Integer type) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",type);
        return categoryMapper.selectByExample(example);
    }
}