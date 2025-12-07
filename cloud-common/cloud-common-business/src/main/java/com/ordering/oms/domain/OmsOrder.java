package com.ordering.oms.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单对象 oms_order
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class OmsOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private String id;
    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;
    /** 用户名 */
    @Excel(name = "用户名")
    @TableField(exist = false)
    private String userName;
    /** 订单总金额 */
    @Excel(name = "订单总金额")
    private BigDecimal totalAmount;
    /** 订单状态: 1未完成2已完成 */
    @Excel(name = "订单状态: 1未完成2已完成")
    private Long orderStatus;
    /** 支付方式 */
    @Excel(name = "支付方式")
    private String paymentMethod;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;
    /** 订单菜品 */
    @TableField(exist = false)
    private List<OmsOrderItem> omsOrderItemList;
}
