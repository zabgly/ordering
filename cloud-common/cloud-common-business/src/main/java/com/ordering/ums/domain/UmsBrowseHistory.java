package com.ordering.ums.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ordering.common.core.annotation.Excel;
import lombok.Data;

/**
 * 浏览记录对象 ums_browse_history
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Data
public class UmsBrowseHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 浏览记录ID */
    private String id;
    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;
    /** 菜品ID */
    @Excel(name = "菜品ID")
    private String productId;
    /** 浏览时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "浏览时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date browseTime;


    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String productImage;
    @TableField(exist = false)
    private String productName;
}
