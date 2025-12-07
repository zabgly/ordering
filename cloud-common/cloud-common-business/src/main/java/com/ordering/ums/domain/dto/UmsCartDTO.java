package com.ordering.ums.domain.dto;


import com.ordering.ums.domain.UmsCart;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 我的菜单对象 ums_cart
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class UmsCartDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 我的菜单集合 */
    private List<UmsCart> umsCartList;
}
