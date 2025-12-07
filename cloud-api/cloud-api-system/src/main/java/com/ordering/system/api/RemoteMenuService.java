package com.ordering.system.api;

import com.ordering.common.core.constant.ServiceNameConstants;
import com.ordering.common.core.domain.R;
import com.ordering.system.api.domain.MenuDict;
import com.ordering.system.api.factory.RemoteFileFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品服务
 * 
 * @author cloud
 */
@FeignClient(contextId = "remoteMenuService", value = ServiceNameConstants.MENU_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteMenuService
{
    /**
     * 全部菜品数据
     */
    @GetMapping(value = "/menu/allList")
    public R<List<MenuDict>> allList();

    /**
     * 根据id获取菜品数据
     */
    @GetMapping(value = "/menu/byId/{id}")
    public R<MenuDict> byId(@PathVariable("id") Integer id);

    /**
     * 下单去库存
     */
    @GetMapping(value = "/menu/placeOrderSubMenuInventory")
    public R<Boolean> placeOrderSubMenuInventory(@RequestParam("menuDictId") Integer menuDictId,
                                                 @RequestParam("orderNum") Integer orderNum);

}
