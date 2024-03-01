package com.sky.service.impl;

import com.sky.common.constant.StatusConstant;
import com.sky.pojo.entity.Orders;
import com.sky.pojo.vo.BusinessDataVO;
import com.sky.pojo.vo.DishOverViewVO;
import com.sky.pojo.vo.OrderOverViewVO;
import com.sky.pojo.vo.SetmealOverViewVO;
import com.sky.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Resource
    private OrderService orderService;

    @Resource
    private ReportService reportService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;
    @Override
    public BusinessDataVO getBusinessData(LocalDate begin, LocalDate end) {
        // 查询总订单数
        Integer totalOrderCount = orderService.ordersStatistics(begin, end).getTotalOrderCount();

        // 营业额
        double turnover = 0.0;
        String[] amounts = orderService.turnoverStatistics(begin, end).getTurnoverList().split(",");
        for (String amount : amounts) {
            if (amount != null && !amount.isEmpty()) {
                turnover += Double.parseDouble(amount);
            }
        }

        // 有效订单数
        Integer validOrderCount = orderService.ordersStatistics(begin, end).getValidOrderCount();

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0 && validOrderCount != 0) {
            // 订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            // 平均客单价
            unitPrice = turnover / validOrderCount;
        }

        // 新增用户数
        Integer newUsers = 0;
        String[] newUserList = orderService.userStatistics(begin, end).getNewUserList().split(",");
        for (String newUser : newUserList) {
            if (newUser != null && !newUser.isEmpty()) {
                newUsers += Integer.parseInt(newUser);
            }
        }

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    @Override
    public OrderOverViewVO getOrderOverView() {
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);

        // 待接单
        Integer waitingOrders = reportService.getOrderCount(begin, null, Orders.TO_BE_CONFIRMED);

        // 待派送
        Integer deliveredOrders = reportService.getOrderCount(begin, null, Orders.CONFIRMED);

        // 已完成
        Integer completedOrders = reportService.getOrderCount(begin, null, Orders.COMPLETED);

        // 已取消
        Integer cancelledOrders = reportService.getOrderCount(begin, null, Orders.CANCELLED);

        // 全部订单
        Integer allOrders = reportService.getOrderCount(begin, null, null);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    @Override
    public DishOverViewVO getDishOverView() {
        Integer sold = dishService.countByStatus(StatusConstant.ENABLE);
        Integer discontinued = dishService.countByStatus(StatusConstant.DISABLE);
        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {
        // 查询启用套餐
        Integer sold = setmealService.countByStatus(StatusConstant.ENABLE);
        // 查询禁用套餐
        Integer discontinued = setmealService.countByStatus(StatusConstant.DISABLE);
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}