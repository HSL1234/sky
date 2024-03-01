package com.sky.web.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.common.context.BaseContext;
import com.sky.common.result.PageResult;
import com.sky.common.result.Result;
import com.sky.pojo.dto.OrdersPageQueryDTO;
import com.sky.pojo.dto.OrdersPaymentDTO;
import com.sky.pojo.dto.OrdersSubmitDTO;
import com.sky.pojo.entity.Orders;
import com.sky.pojo.vo.OrderSubmitVO;
import com.sky.pojo.vo.OrderVO;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("user/orderController")
@Slf4j
@Api(tags = "订单接口")
@RequestMapping("/user/order")
public class OrderController {
    @Resource
    private OrderService orderService;



    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        PageHelper.orderBy("order_time desc");
        List<OrderVO> orderVOList = orderService.historyOrders(ordersPageQueryDTO);
        PageInfo<Orders> pageInfo = new PageInfo<>(orderVOList);
        PageResult pageResult = new PageResult(pageInfo.getTotal(),orderVOList);
        return Result.success(pageResult);
    }
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<Object> repetition(@PathVariable Long id){
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result<Object> reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<Object> cancel(@PathVariable Long id){
        orderService.cancel(id);
        return Result.success();
    }

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付 (后期交给微信支付)
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }
}