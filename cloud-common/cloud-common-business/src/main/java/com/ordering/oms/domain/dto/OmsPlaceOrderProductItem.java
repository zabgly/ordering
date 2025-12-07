package com.ordering.oms.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmsPlaceOrderProductItem {

    /** 菜品ID */
    private String productId;
    /** 菜品数量 */
    private Long quantity;
    /** 菜品单价 */
    private BigDecimal price;
}
