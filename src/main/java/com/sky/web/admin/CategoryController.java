package com.sky.web.admin;

import com.sky.common.constant.StatusConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.pojo.dto.CategoryDTO;
import com.sky.pojo.dto.CategoryPageQueryDTO;
import com.sky.pojo.entity.Category;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@Api(tags = "分类相关接口")
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation("新增分类")
    public Result add(@RequestBody CategoryDTO categoryDTO){
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.ENABLE);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        Integer i = categoryService.add(category);
        if (i>0){
            return Result.success();
        }
            return Result.error("新增失败！");
    }

    /**
     * 分类分页查询
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 分类启用/禁用
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result status(@PathVariable Integer status,@RequestParam Long id){
        Integer i = categoryService.updateStatus(status,id);
        if (i>0){
            return Result.success();
        }
        return Result.error("修改失败！");
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result delete(@RequestParam Long id){
        Integer i = categoryService.delete(id);
        if (i>0){
            return Result.success();
        }
        return Result.error("删除失败！请检查分类状态");
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setUpdateTime(new Date());
        category.setUpdateUser(BaseContext.getCurrentId());
        Integer i = categoryService.update(category);
        if (i>0){
            return Result.success();
        }
        return Result.error("修改失败！");
    }

    /**
     * 根据类型查询分类
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result list(@RequestParam Integer type) {
        List<Category> categories = categoryService.list(type);
        return Result.success(categories);
    }
}