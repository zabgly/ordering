package com.ordering.ums.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜品评价对象 ums_product_review
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class UmsProductReview implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private String id;
    /** 菜品ID */
    @Excel(name = "菜品ID")
    private String productId;
    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;
    /** 评价内容 */
    @Excel(name = "评价内容")
    private String content;
    /** 评分: 1-5 */
    @Excel(name = "评分: 1-5")
    private Long rating;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;

    //用于新增评论前端接参
    @TableField(exist = false)
    private String orderId;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String productImage;
    @TableField(exist = false)
    private String productName;
}
