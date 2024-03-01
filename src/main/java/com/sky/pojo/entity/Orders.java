package com.sky.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders implements Serializable {

    /**
     * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     */
    public static final Integer PENDING_PAYMENT = 1;
    public static final Integer TO_BE_CONFIRMED = 2;
    public static final Integer CONFIRMED = 3;
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    public static final Integer COMPLETED = 5;
    public static final Integer CANCELLED = 6;

    /**
     * 支付状态 0未支付 1已支付 2退款
     */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUND = 2;

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "status")
    private Integer status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "address_book_id")
    private Long addressBookId;

    @Column(name = "order_time")
    private Date orderTime;

    @Column(name = "checkout_time")
    private Date checkoutTime;

    @Column(name = "pay_method")
    private Integer payMethod;

    @Column(name = "pay_status")
    private Integer payStatus;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "remark")
    private String remark;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "consignee")
    private String consignee;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "cancel_time")
    private Date cancelTime;

    @Column(name = "estimated_delivery_time")
    private Date estimatedDeliveryTime;

    @Column(name = "delivery_status")
    private Boolean deliveryStatus;

    @Column(name = "delivery_time")
    private Date deliveryTime;

    @Column(name = "pack_amount")
    private Integer packAmount;

    @Column(name = "tableware_number")
    private Integer tablewareNumber;

    @Column(name = "tableware_status")
    private Boolean tablewareStatus;

    private static final long serialVersionUID = 1L;
}