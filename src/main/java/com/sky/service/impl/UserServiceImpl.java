package com.sky.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sky.common.constant.MessageConstant;
import com.sky.common.exception.LoginFailedException;
import com.sky.common.utils.HttpClientUtil;
import com.sky.mapper.UserMapper;
import com.sky.pojo.dto.UserLoginDTO;
import com.sky.pojo.entity.User;
import com.sky.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    // 微信APPID
    public static final String APPID = "wx20271f0f403b3484";
    // 微信APP秘钥
    public static final String APP_SECRET = "3ffe728d4a1bddf0bb988b0a5052c701";

    @Resource
    private UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.selectOne(User.builder().openid(openid).build());
        if (user == null){
            user = User.builder().openid(openid).createTime(new Date()).build();
            userMapper.insertSelective(user);
        }
        return user;

    }


    /**
     * 调用微信接口服务，获取微信用户的openid
     *
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        // 调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", APPID);
        map.put("secret", APP_SECRET);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}