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

@Data
@Table(name = "setmeal_dish")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetmealDish implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "setmeal_id")
    private Long setmealId;

    @Column(name = "dish_id")
    private Long dishId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "copies")
    private Integer copies;

    private static final long serialVersionUID = 1L;
}