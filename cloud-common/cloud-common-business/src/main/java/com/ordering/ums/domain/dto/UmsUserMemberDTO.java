package com.ordering.ums.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UmsUserMemberDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //会员id
    private Long memberId;

    //最终结算金额
    private BigDecimal finalAmount;

}
