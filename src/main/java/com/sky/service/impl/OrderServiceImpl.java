package com.sky.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.common.constant.MessageConstant;
import com.sky.common.context.BaseContext;
import com.sky.common.exception.OrderBusinessException;
import com.sky.common.result.PageResult;
import com.sky.mapper.*;
import com.sky.pojo.dto.*;
import com.sky.pojo.entity.*;
import com.sky.pojo.vo.*;
import com.sky.service.OrderService;
import com.sky.websocket.WebSocketServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private AddressBookMapper addressBookMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WebSocketServer webSocketServer;

    @Override
    public List<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        List<Orders> ordersList = ordersMapper.select(Orders.builder().userId(ordersPageQueryDTO.getUserId()).status(ordersPageQueryDTO.getStatus()).build());
        ArrayList<OrderVO> orderVOS = new ArrayList<>();
        ordersList.forEach(orders -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVOS.add(orderVO);
        });
        orderVOS.forEach(orderVO -> {
            List<OrderDetail> orderDetailList = orderDetailMapper.select(OrderDetail.builder().orderId(orderVO.getId()).build());
            orderVO.setOrderDetailList(orderDetailList);
        });
        System.out.println(orderVOS);
        return orderVOS;
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetailList = orderDetailMapper.select(OrderDetail.builder().orderId(id).build());
        ArrayList<ShoppingCart> cartArrayList = new ArrayList<>();
        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(new Date());
            cartArrayList.add(shoppingCart);
        });
        shoppingCartMapper.insertList(cartArrayList);
    }

    @Override
    public OrderVO orderDetail(Long id) {
        Orders orders = ordersMapper.selectByPrimaryKey(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> orderDetailList = orderDetailMapper.select(OrderDetail.builder().orderId(id).build());
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void reminder(Long id) {
        Orders orders = ordersMapper.selectByPrimaryKey(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

    @Override
    public void cancel(Long id) {
        ordersMapper.updateByPrimaryKeySelective(Orders.builder().id(id).status(6).build());
    }

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook = addressBookMapper.selectByPrimaryKey(ordersSubmitDTO.getAddressBookId());
        User user = userMapper.selectOne(User.builder().id(addressBook.getUserId()).build());
        Orders orders = Orders.builder().payStatus(0).status(1).orderTime(new Date()).number(String.valueOf(System.currentTimeMillis())).address(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail()).userName(user.getName()).build();
        BeanUtils.copyProperties(addressBook, orders, "id");
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        // 新增订单
        ordersMapper.insertSelective(orders);
        // 新增订单明细
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.select(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
        shoppingCarts.forEach(shoppingCart -> {
            OrderDetail orderDetail = OrderDetail.builder().build();
            BeanUtils.copyProperties(shoppingCart, orderDetail, "id");
            orderDetail.setOrderId(orders.getId());
            orderDetailMapper.insertSelective(orderDetail);
        });
        // 清除购物车
        shoppingCartMapper.delete(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
        return OrderSubmitVO.builder().id(orders.getId()).orderNumber(orders.getNumber()).orderAmount(orders.getAmount()).orderTime(orders.getOrderTime()).build();
    }

    /**
     * 订单支付
     *
     * @return OrderPaymentVO
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 微信支付代码...

        // 模拟支付成功:业务处理，修改订单状态、来单提醒
        this.paySuccess(ordersPaymentDTO.getOrderNumber());

        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = ordersMapper.selectOne(Orders.builder()
                .userId(userId)
                .number(outTradeNo).build());

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(new Date())
                .build();
        ordersMapper.updateByPrimaryKeySelective(orders);

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));

    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(ordersPageQueryDTO.getNumber())) {
            criteria.andLike("number", "%" + ordersPageQueryDTO.getNumber() + "%");
        }
        if (StringUtils.isNotEmpty(ordersPageQueryDTO.getPhone())) {
            criteria.andLike("phone", "%" + ordersPageQueryDTO.getPhone() + "%");
        }
        if (ordersPageQueryDTO.getStatus() != null) {
            criteria.andEqualTo("status", ordersPageQueryDTO.getStatus());
        }
        if (ordersPageQueryDTO.getUserId() != null) {
            criteria.andEqualTo("userId", ordersPageQueryDTO.getUserId());
        }
        if (ordersPageQueryDTO.getBeginTime() != null && ordersPageQueryDTO.getEndTime() != null) {
            criteria.andBetween("orderTime", ordersPageQueryDTO.getBeginTime(), ordersPageQueryDTO.getEndTime());
        }
        example.orderBy("orderTime").desc();
        List<Orders> ordersList = ordersMapper.selectByExample(example);
        PageInfo<Orders> pageInfo = new PageInfo<>(ordersList);
        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        ArrayList<OrderVO> orderVOS = new ArrayList<>();
        ordersList.forEach(orders -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.select(OrderDetail.builder().orderId(orders.getId()).build());
            // 将每一条 订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
            List<String> orderDishList = orderDetailList.stream().map(orderDetail -> orderDetail.getName() + "*" + orderDetail.getNumber() + ";").collect(Collectors.toList());
            orderVO.setOrderDishes(String.join("", orderDishList));
            orderVOS.add(orderVO);
        });
        return new PageResult(pageInfo.getTotal(), orderVOS);
    }

    @Override
    public OrderStatisticsVO statistics() {
        int toBeConfirmedCount = ordersMapper.selectCount(Orders.builder().status(Orders.TO_BE_CONFIRMED).build());
        int confirmedCount = ordersMapper.selectCount(Orders.builder().status(Orders.CONFIRMED).build());
        int deliveryInProgressCount = ordersMapper.selectCount(Orders.builder().status(Orders.DELIVERY_IN_PROGRESS).build());
        return OrderStatisticsVO.builder().toBeConfirmed(toBeConfirmedCount).confirmed(confirmedCount).deliveryInProgress(deliveryInProgressCount).build();
    }

    @Override
    public void confirm(Orders orders) {
        Orders order = ordersMapper.selectByPrimaryKey(orders.getId());
        if (Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
            orders.setStatus(Orders.CONFIRMED);
            ordersMapper.updateByPrimaryKeySelective(orders);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = ordersMapper.selectByPrimaryKey(ordersRejectionDTO.getId());
        if (Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED)) {
            orders.setStatus(Orders.CANCELLED);
            orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
            orders.setCancelTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(orders);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = ordersMapper.selectByPrimaryKey(ordersCancelDTO.getId());
        if (Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelReason(ordersCancelDTO.getCancelReason());
            orders.setCancelTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(orders);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void delivery(Long id) {
        Orders orders = ordersMapper.selectByPrimaryKey(id);
        if (Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
            ordersMapper.updateByPrimaryKeySelective(orders);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void complete(Long id) {
        Orders orders = ordersMapper.selectByPrimaryKey(id);
        if (Objects.equals(orders.getStatus(), Orders.DELIVERY_IN_PROGRESS)) {
            orders.setStatus(Orders.COMPLETED);
            orders.setDeliveryTime(new Date());
            ordersMapper.updateByPrimaryKeySelective(orders);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateByStatusAndOrdertimeLT(Integer status, Date date) {
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", status);
        criteria.andLessThan("orderTime", date);
        List<Orders> ordersList = ordersMapper.selectByExample(example);
        // 取消订单
        if (ordersList != null && !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                if (Objects.equals(status, Orders.PENDING_PAYMENT)) { // 支付超时
                    orders.setStatus(Orders.CANCELLED);
                    orders.setCancelReason("支付超时,已取消");
                    orders.setCancelTime(new Date());
                } else if (Objects.equals(status, Orders.DELIVERY_IN_PROGRESS)) { // 正在派送中
                    orders.setStatus(Orders.COMPLETED);
                    orders.setDeliveryTime(orders.getEstimatedDeliveryTime());
                }
                ordersMapper.updateByPrimaryKeySelective(orders);
            });
        }
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andBetween("orderTime", beginDateTime, endDateTime);
        int totalOrderCount = ordersMapper.selectCountByExample(example);
        criteria.andEqualTo("status", Orders.COMPLETED);
        int validOrderCount = ordersMapper.selectCountByExample(example);
        double orderCompletionRate = validOrderCount * 1.0 / totalOrderCount;

        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {

            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        ArrayList<Integer> orderCountList = new ArrayList<>();
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Example example1 = new Example(Orders.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andGreaterThanOrEqualTo("orderTime", beginTime);
            criteria1.andLessThanOrEqualTo("orderTime", endTime);
            int orderCount = ordersMapper.selectCountByExample(example1);
            orderCountList.add(orderCount);
            criteria1.andEqualTo("status", Orders.COMPLETED);
            int validCount = ordersMapper.selectCountByExample(example1);
            validOrderCountList.add(validCount);
        });

        return OrderReportVO.builder().totalOrderCount(totalOrderCount).validOrderCount(validOrderCount).orderCompletionRate(orderCompletionRate).dateList(StringUtils.join(dateList, ",")).orderCountList(StringUtils.join(orderCountList, ",")).validOrderCountList(StringUtils.join(validOrderCountList, ",")).build();
    }

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {

            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        ArrayList<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Example example = new Example(Orders.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andGreaterThanOrEqualTo("orderTime", beginTime);
            criteria.andLessThanOrEqualTo("orderTime", endTime);
            criteria.andEqualTo("status", Orders.COMPLETED);
            List<Orders> ordersList = ordersMapper.selectByExample(example);
            Double amount = 0.0;
            for (Orders orders : ordersList) {
                amount += orders.getAmount();
            }
            turnoverList.add(amount);
        });

        return TurnoverReportVO.builder().dateList(StringUtils.join(dateList, ",")).turnoverList(StringUtils.join(turnoverList, ",")).build();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        ArrayList<Integer> totalUserList = new ArrayList<>();
        ArrayList<Integer> newUserList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andLessThanOrEqualTo("createTime", endTime);
            int totalUser = userMapper.selectCountByExample(example);
            totalUserList.add(totalUser);
            criteria.andGreaterThanOrEqualTo("createTime", beginTime);
            int newUser = userMapper.selectCountByExample(example);
            newUserList.add(newUser);
        });
        return UserReportVO.builder().totalUserList(StringUtils.join(totalUserList, ",")).newUserList(StringUtils.join(newUserList, ",")).dateList(StringUtils.join(dateList, ",")).build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = ordersMapper.getSalesTop10(beginTime, endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public SalesTop10ReportVO export(LocalDate begin, LocalDate end) {
        return null;
    }

}