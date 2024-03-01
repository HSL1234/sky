package com.sky.web.admin;

import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.pojo.dto.OrdersCancelDTO;
import com.sky.pojo.dto.OrdersPageQueryDTO;
import com.sky.pojo.dto.OrdersRejectionDTO;
import com.sky.pojo.entity.Orders;
import com.sky.pojo.vo.OrderStatisticsVO;
import com.sky.pojo.vo.OrderVO;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("admin/orderController")
@Slf4j
@Api(tags = "订单管理接口")
@RequestMapping("/admin/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult  = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);

    }
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO  = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        OrderVO orderVO  = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<Object> confirm(@RequestBody Orders orders){
        orderService.confirm(orders);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<Object> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<Object> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<Object> delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<Object> complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
}