package com.ordering.ums.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏对象 ums_favorite
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class UmsFavorite implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 收藏ID */
    private String id;
    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;
    /** 菜品ID */
    @Excel(name = "菜品ID")
    private String productId;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateTime;


    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String productImage;
    @TableField(exist = false)
    private String productName;
}
