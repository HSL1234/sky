package com.sky.common.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    // 设置jwt签名加密时使用的秘钥
    private String adminSecretKey = "#@^$&^%%#@%$#%d";
    // 设置jwt过期时间
    private long adminTtl = 7200000;
    // 设置前端传递过来的令牌名称
    private String adminTokenName = "token";

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey = "@#￥#@%￥@#%%￥#%";
    private long userTtl = 7200000;
    private String userTokenName = "authentication";

}
