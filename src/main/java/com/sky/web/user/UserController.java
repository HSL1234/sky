package com.sky.web.user;

import com.sky.common.constant.JwtClaimsConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.properties.JwtProperties;
import com.sky.common.result.Result;
import com.sky.common.utils.JwtUtil;
import com.sky.pojo.dto.UserLoginDTO;
import com.sky.pojo.entity.User;
import com.sky.pojo.vo.UserLoginVO;
import com.sky.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
@RequestMapping("user/user")
@Api(tags = "用户接口")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录：{}",userLoginDTO.getCode());
        User user = userService.login(userLoginDTO);
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        UserLoginVO userLoginVO = UserLoginVO.builder().id(user.getId()).openid(user.getOpenid()).token(token).build();
        return Result.success(userLoginVO);
    }

    @PostMapping("/logout")
    @ApiOperation("用户退出")
    public Result<Object> logout(){
        BaseContext.removeCurrentId();
        return Result.success();
    }
}