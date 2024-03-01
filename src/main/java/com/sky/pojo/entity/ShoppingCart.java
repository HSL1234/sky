package com.sky.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "shopping_cart")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCart implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "dish_id")
    private Long dishId;

    @Column(name = "setmeal_id")
    private Long setmealId;

    @Column(name = "dish_flavor")
    private String dishFlavor;

    @Column(name = "number")
    private Integer number;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}