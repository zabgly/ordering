package com.ordering.ums.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 我的菜单对象 ums_cart
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class UmsCart implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 我的菜单ID */
    private String id;
    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;
    /** 菜品ID */
    @Excel(name = "菜品ID")
    private String productId;
    /** 菜品数量 */
    @Excel(name = "菜品数量")
    private Long quantity;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;

    /** 用户数据-用于接口返回 */
    @TableField(exist = false)
    private UmsUser umsUser;
    /** 菜品集合-用于接口返回 */
    @TableField(exist = false)
    private List<CmsProduct> cmsProductList;
    /** 菜品ID集合-用于数据库分组接参 */
    @TableField(exist = false)
    private String productIds;
    /** 菜品数量-用于数据库分组接参 */
    @TableField(exist = false)
    private String quantityList;
    /** 用户数据-用于前端接收 */
    @TableField(exist = false)
    private String userName;
}
