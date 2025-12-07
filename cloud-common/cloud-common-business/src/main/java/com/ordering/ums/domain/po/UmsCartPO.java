package com.ordering.ums.domain.po;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UmsCartPO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String userId;
    /** 菜品ID集合 */
    @TableField(exist = false)
    private List<String> cmsProductIds;
    /** 菜品数量 */
    private Long quantity;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;
}
