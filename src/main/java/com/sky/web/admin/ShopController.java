package com.sky.web.admin;

import com.sky.common.constant.StatusConstant;
import com.sky.common.result.Result;
import com.sky.config.RedisConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("admin/shopController")
@Api(tags = "管理端-店铺操作接口")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Resource
    private RedisConfig redisConfig;

    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result updateStatus(@PathVariable String status) {
        RedisTemplate<String, String> template = redisConfig.redisTemplate();
        template.opsForValue().set(StatusConstant.SHOP_STATUS_KEY,status);
        return Result.success();
    }


    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus() {
        RedisTemplate<String, String> template = redisConfig.redisTemplate();
        return Result.success(Integer.parseInt(template.opsForValue().get(StatusConstant.SHOP_STATUS_KEY)) );
    }

}