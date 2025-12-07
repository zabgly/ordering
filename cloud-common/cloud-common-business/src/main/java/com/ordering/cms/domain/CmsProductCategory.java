package com.ordering.cms.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 菜品分类对象 cms_product_category
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class CmsProductCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private String id;
    /** 分类名称 */
    @Excel(name = "分类名称")
    private String name;
    /** 父分类ID */
    @Excel(name = "父分类ID")
    private String parentId;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;
    /** 孩子节点 */
    @TableField(exist = false)
    private List<CmsProductCategory> children;
    /** 孩子节点 */
    @TableField(exist = false)
    private List<CmsProduct> cmsProductList;
}
