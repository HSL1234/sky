package com.sky.web.user;

import com.sky.common.result.Result;
import com.sky.pojo.entity.Dish;
import com.sky.pojo.entity.DishFlavor;
import com.sky.pojo.vo.DishVO;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController("user/dishController")
@Api(tags = "菜品浏览接口")
@Slf4j
@RequestMapping("/user/dish")
public class DishController {
    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;


    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @Cacheable(cacheNames = "dishCache", key = "#categoryId")
    public Result<List<DishVO>> list(Long categoryId){
        List<Dish> dishList = dishService.list(categoryId);
        List<DishVO> dishVOList = new ArrayList<>();
        dishList.forEach(dish -> {
            List<DishFlavor> dishFlavorList = dishFlavorService.listByDishId(dish.getId());
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish,dishVO);
            dishVO.setFlavors(dishFlavorList);
            dishVOList.add(dishVO);
        });
        return Result.success(dishVOList);
    }
}