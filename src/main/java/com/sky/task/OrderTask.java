package com.sky.task;

import com.sky.pojo.entity.Orders;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 自定义定时任务，实现订单状态定时处理
 */
@Component
@Slf4j
public class OrderTask {

    @Resource
    private OrderService orderService;

    /**
     * 处理支付超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理支付超时订单：{}", new Date());
        long nowSubTime = System.currentTimeMillis() - 15 * 1000 * 60; // 当前-15分钟日期
        orderService.updateByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, new Date(nowSubTime));
    }

    /**
     * 处理“派送中”状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单：{}", new Date());
        long nowSubTime = System.currentTimeMillis() - 1000 * 60 * 60; // 当前-1小时日期
        orderService.updateByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, new Date(nowSubTime));
    }

}