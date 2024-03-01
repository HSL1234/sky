package com.sky.web.user;

import com.sky.common.result.Result;
import com.sky.pojo.dto.ShoppingCartDTO;
import com.sky.pojo.entity.ShoppingCart;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "购物车接口")
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> shoppingCartList  = shoppingCartService.list();
        return Result.success(shoppingCartList);
    }

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    @CacheEvict(cacheNames = {"setmealCache","dishCache"},allEntries = true)
    public Result<Object> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("删除购物车中一个商品")
    @CacheEvict(cacheNames = {"setmealCache","dishCache"},allEntries = true)
    public Result<Object> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    @CacheEvict(cacheNames = {"setmealCache","dishCache"},allEntries = true)
    public Result<Object> clean() {
        shoppingCartService.clean();
        return Result.success();
    }


}