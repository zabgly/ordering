package com.ordering.oms.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OmsPlaceOrder {

    /** 菜品信息 */
    private List<OmsPlaceOrderProductItem> omsPlaceOrderProductItemList;
    /** 订单总金额 */
    private BigDecimal totalAmount;
    /** 支付方式 */
    private String paymentMethod;
    /** 是否是我的菜单 */
    private Boolean isCar;

}
