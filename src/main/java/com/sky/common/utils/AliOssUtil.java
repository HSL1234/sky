package com.sky.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private static String endpoint="";
    private static String accessKeyId="";
    private static String accessKeySecret="";
    private static String bucketName="";

    /**
     * 文件上传
     *
     * @param bytes
     * @param objectName
     * @return
     */
    public static String upload(byte[] bytes, String objectName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            log.error("【OSS 服务器异常】"+e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                。append(bucketName)
                。append(".")
                。append(endpoint)
                。append("/")
                。append(objectName);

        log.info("文件上传到:{}", stringBuilder.toString());

        return stringBuilder.toString();
    }

    /**
     * 文件删除
     *
     * @param
     * @param objectName
     * @return
     */
    public static void delete(String objectName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建DeleteObject请求。
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("【OSS 服务器异常】"+e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


}
