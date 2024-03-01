package com.sky.web.user;

import com.sky.common.result.Result;
import com.sky.pojo.entity.Setmeal;
import com.sky.pojo.entity.SetmealDish;
import com.sky.pojo.vo.DishItemVO;
import com.sky.pojo.vo.DishVO;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController("user/setmealController")
@Api(tags = "菜品浏览接口")
@Slf4j
@RequestMapping("/user/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealDishService setmealDishService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<Setmeal>> list(Long categoryId) {
        List<Setmeal> setmealList = setmealService.listByCategoryId(categoryId);
        return Result.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> dish(@PathVariable Long id) {
        List<SetmealDish> setmealDishList = setmealDishService.getDishIdListBySetmealId(id);
        List<DishItemVO> dishItemVOList = new ArrayList<>();
        setmealDishList.forEach(s -> {
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(s, dishItemVO);
            DishVO dishVO = dishService.listById(s.getDishId());
            dishItemVO.setImage(dishVO.getImage());
            dishItemVO.setDescription(dishVO.getDescription());
            dishItemVOList.add(dishItemVO);
        });
        return Result.success(dishItemVOList);
    }
}