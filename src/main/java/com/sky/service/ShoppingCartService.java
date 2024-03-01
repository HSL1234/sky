package com.sky.service;

import com.sky.pojo.dto.ShoppingCartDTO;
import com.sky.pojo.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    List<ShoppingCart> list();

    void add(ShoppingCartDTO shoppingCartDTO);

    void sub(ShoppingCartDTO shoppingCartDTO);

    void clean();
}
