package com.ordering.cms.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import com.ordering.ums.domain.UmsProductReview;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 菜品对象 cms_product
 *
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class CmsProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 菜品ID */
    private String id;
    /** 菜品名称 */
    @Excel(name = "菜品名称")
    private String name;
    /** 菜品描述 */
    @Excel(name = "菜品描述")
    private String description;
    /** 菜品价格 */
    @Excel(name = "菜品价格")
    private BigDecimal price;
    /** 库存数量 */
    @Excel(name = "库存数量")
    private Long stock;
    /** 菜品分类ID */
    @Excel(name = "菜品分类ID")
    private String categoryId;
    /** 菜品分类名称 */
    @Excel(name = "菜品分类名称")
    @TableField(exist = false)
    private String categoryName;
    /** 菜品状态 */
    @Excel(name = "菜品状态")
    private Long productStatus;
    /** 菜品图片URL */
    @Excel(name = "菜品图片URL")
    private String imageUrl;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;

    /** 菜品数量-返回给前端的 */
    @TableField(exist = false)
    private String quantity;
    /** 是否收藏-返回给前端的 */
    @TableField(exist = false)
    private Boolean isCollect;
    /** 是否折扣（true为折扣，false为优惠）-返回给前端的 */
    @TableField(exist = false)
    private Boolean isDiscount;
    /** 额度（折扣额度或优惠额度）-返回给前端的 */
    @TableField(exist = false)
    private BigDecimal discountNum;
    /** 评论列表 */
    @TableField(exist = false)
    private List<UmsProductReview> productReviewList;
}
