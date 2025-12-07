package com.ordering.system.api.domain;

import com.ordering.common.core.annotation.Excel;
import com.ordering.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 菜品管理对象 menu_dict
 *
 * @author cloud
 * @date 2024-06-20
 */
public class MenuDict extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 菜品编号 */
    private Integer id;

    /** 菜品名 */
    @Excel(name = "菜品名")
    private String menuName;

    /** 菜单价格 */
    @Excel(name = "菜单价格")
    private Long menuPrice;

    /** 封面图片 */
    @Excel(name = "封面图片")
    private String menuImage;

    /** 菜品库存 */
    @Excel(name = "菜品库存")
    private Integer menuNum;

    /** 菜单状态1在售，2售完 */
    @Excel(name = "菜单状态1在售，2售完")
    private String menuStatus;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setMenuName(String menuName)
    {
        this.menuName = menuName;
    }

    public String getMenuName()
    {
        return menuName;
    }
    public void setMenuPrice(Long menuPrice)
    {
        this.menuPrice = menuPrice;
    }

    public Long getMenuPrice()
    {
        return menuPrice;
    }
    public void setMenuImage(String menuImage)
    {
        this.menuImage = menuImage;
    }

    public String getMenuImage()
    {
        return menuImage;
    }
    public void setMenuNum(Integer menuNum)
    {
        this.menuNum = menuNum;
    }

    public Integer getMenuNum()
    {
        return menuNum;
    }
    public void setMenuStatus(String menuStatus)
    {
        this.menuStatus = menuStatus;
    }

    public String getMenuStatus()
    {
        return menuStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("menuName", getMenuName())
                .append("menuPrice", getMenuPrice())
                .append("menuImage", getMenuImage())
                .append("menuNum", getMenuNum())
                .append("menuStatus", getMenuStatus())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}

