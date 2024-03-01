package com.sky.service;

import com.sky.common.result.PageResult;
import com.sky.pojo.dto.*;
import com.sky.pojo.entity.Orders;
import com.sky.pojo.vo.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {
    List<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    void repetition(Long id);

    OrderVO  orderDetail(Long id);

    void reminder(Long id);

    void cancel(Long id);

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(Orders orders);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);

    /**
     * 处理支付超时、派送中订单
     * @param status
     * @param date
     */
    void updateByStatusAndOrdertimeLT(Integer status, Date date);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

    /**
     * 导出Excel报表
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO export(LocalDate begin, LocalDate end);
}
