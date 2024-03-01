package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {


    private ApiInfo apiInfo = new ApiInfoBuilder()
            .title("天天外卖项目接口文档")
            .contact(new Contact("顺利", "", "1751535744@qq.com"))
            .version("2.0")
            .description("天天外卖项目接口文档")
            .build();

    /**
     * 通过knife4j生成Admin接口文档
     *
     * @return
     */
    @Bean
    public Docket docketAdmin() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.web.admin"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 通过knife4j生成User接口文档
     *
     * @return
     */
    @Bean
    public Docket docketUser() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("客户端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.web.user"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}