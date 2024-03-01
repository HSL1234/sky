package com.sky.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel(description = "地址传输对象")
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO implements Serializable {
    private Long id;
}