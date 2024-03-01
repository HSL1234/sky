package com.sky.common.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
