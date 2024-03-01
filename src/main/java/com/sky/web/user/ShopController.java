package com.sky.web.user;

import com.sky.common.constant.StatusConstant;
import com.sky.common.result.Result;
import com.sky.config.RedisConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@RestController("user/shopController")
@Api(tags = "用户端-店铺操作接口")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    @Resource
    private RedisConfig redisConfig;


    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus() {
        RedisTemplate<String, String> template = redisConfig.redisTemplate();
        return Result.success(Integer.parseInt(Objects.requireNonNull(template.opsForValue().get(StatusConstant.SHOP_STATUS_KEY))) );
    }

}