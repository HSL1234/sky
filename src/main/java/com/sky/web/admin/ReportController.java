package com.sky.web.admin;

import com.sky.common.result.Result;
import com.sky.pojo.vo.OrderReportVO;
import com.sky.pojo.vo.SalesTop10ReportVO;
import com.sky.pojo.vo.TurnoverReportVO;
import com.sky.pojo.vo.UserReportVO;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@Slf4j
@Api(tags = "数据统计相关接口")
@RequestMapping("/admin/report")
public class ReportController {

    @Resource
    private OrderService orderService;

    @Resource
    private ReportService reportService;

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        // log.info("开始日期：{},结束日期：{}",begin,end);
        OrderReportVO orderReportVO = orderService.ordersStatistics(begin,end);
        return Result.success(orderReportVO);
    }

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计接口")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        // log.info("开始日期：{},结束日期：{}",begin,end);
        TurnoverReportVO turnoverReportVO = orderService.turnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        // log.info("开始日期：{},结束日期：{}",begin,end);
        UserReportVO userReportVO = orderService.userStatistics(begin,end);
        return Result.success(userReportVO);
    }

    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10接口")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        // log.info("开始日期：{},结束日期：{}",begin,end);
        SalesTop10ReportVO salesTop10ReportVO = orderService.top10(begin,end);
        return Result.success(salesTop10ReportVO);
    }
    @GetMapping("/export")
    @ApiOperation("导出Excel报表接口")
    public void export(HttpServletResponse response){
        reportService.export(response);
    }
}