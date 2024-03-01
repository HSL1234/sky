package com.sky.service;

import com.sky.pojo.vo.BusinessDataVO;
import com.sky.pojo.vo.DishOverViewVO;
import com.sky.pojo.vo.OrderOverViewVO;
import com.sky.pojo.vo.SetmealOverViewVO;

import java.time.LocalDate;

public interface WorkspaceService {
    BusinessDataVO getBusinessData(LocalDate begin, LocalDate end);

    OrderOverViewVO getOrderOverView();

    DishOverViewVO getDishOverView();

    SetmealOverViewVO getSetmealOverView();
}
