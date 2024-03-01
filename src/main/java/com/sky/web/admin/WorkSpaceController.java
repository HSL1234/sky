package com.sky.web.admin;

import com.sky.common.result.Result;
import com.sky.pojo.vo.BusinessDataVO;
import com.sky.pojo.vo.DishOverViewVO;
import com.sky.pojo.vo.OrderOverViewVO;
import com.sky.pojo.vo.SetmealOverViewVO;
import com.sky.service.DishService;
import com.sky.service.OrderService;
import com.sky.service.SetmealService;
import com.sky.service.WorkspaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Api(tags = "工作台接口")
@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;
    @Resource
    private OrderService orderService;

    @Resource
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> businessData() {
        //获得当天的开始时间
        LocalDate begin = LocalDate.now();
        //获得当天的结束时间
        LocalDate end = LocalDate.now();
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);

    }

    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrders() {
        return Result.success(workspaceService.getOrderOverView());

    }

    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> overviewDishes() {
        return Result.success(workspaceService.getDishOverView());

    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals() {
        return Result.success(workspaceService.getSetmealOverView());

    }
}