package com.ordering.cms.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

/**
 * 促销活动对象 cms_promotion
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class CmsPromotion implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 促销ID */
    private String id;
    /** 促销名称 */
    @Excel(name = "促销名称")
    private String name;
    /** 促销类型: 0折扣, 1优惠 */
    @Excel(name = "促销类型: 0折扣, 1优惠")
    private Long type;
    /** 折扣率（如果是折扣类型） */
    @Excel(name = "折扣率")
    private BigDecimal discount;
    /** 优惠金额（如果是优惠类型） */
    @Excel(name = "优惠金额")
    private BigDecimal couponValue;
    /** 活动开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "活动开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;
    /** 活动结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "活动结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;
    /** 绑定的菜品 */
    @TableField(exist = false)
    private List<CmsProduct> productList;
}
