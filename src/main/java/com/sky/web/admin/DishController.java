package com.sky.web.admin;

import com.sky.common.constant.MessageConstant;
import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.pojo.dto.DishDTO;
import com.sky.pojo.dto.DishPageQueryDTO;
import com.sky.pojo.entity.Dish;
import com.sky.pojo.vo.DishVO;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Resource
    private DishService dishService;


    @PostMapping
    @ApiOperation("新增菜品")
    @CacheEvict(cacheNames = "dishCache", key = "#dishDTO.categoryId")
    public Result add(@RequestBody DishDTO dishDTO) {
        int i = dishService.addwithFlavor(dishDTO);
        if (i > 0) {
            return Result.success();
        }
        return Result.error("新增菜品失败！");
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result updateStatus(@PathVariable Integer status, Long id) {
        dishService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类Id查询菜品")
    public Result<List<Dish>> list(@RequestParam Long categoryId) {
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result deleteList(@RequestParam String ids) {
        Integer i = dishService.deleteList(ids);
        if (i == ids.split(",").length)
            return Result.success();
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据Id查询菜品")
    public Result<DishVO> listById(@PathVariable Long id) {
        DishVO dishVO = dishService.listById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    @CacheEvict(cacheNames = "dishCache", allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO) {
        int i = dishService.update(dishDTO);
        if (i > 0) {
            return Result.success();
        }
        return Result.error("修改菜品失败！");
    }
}