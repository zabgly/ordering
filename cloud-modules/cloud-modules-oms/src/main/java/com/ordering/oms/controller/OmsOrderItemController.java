package com.ordering.oms.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.common.core.utils.poi.ExcelUtil;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.log.annotation.Log;
import com.ordering.common.log.enums.BusinessType;
import com.ordering.common.security.annotation.RequiresPermissions;
import com.ordering.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ordering.oms.domain.OmsOrderItem;
import com.ordering.oms.service.IOmsOrderItemService;

/**
 * 订单菜品Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/item")
public class OmsOrderItemController extends BaseController {
    @Resource
    private IOmsOrderItemService omsOrderItemService;

    /**
     * 查询订单菜品列表
     */
    @RequiresPermissions("@ss.hasPermi('oms:item:list')")
    @GetMapping("/list")
    public TableDataInfo list(OmsOrderItem omsOrderItem) {
        startPage();
        LambdaQueryWrapper<OmsOrderItem> queryWrapper = new LambdaQueryWrapper<>(omsOrderItem);
        List<OmsOrderItem> list = omsOrderItemService.list(queryWrapper);
        return getDataTable(list);
    }

    /**
     * 导出订单菜品列表
     */
    @RequiresPermissions("@ss.hasPermi('oms:item:export')")
    @Log(title = "订单菜品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmsOrderItem omsOrderItem) {
        LambdaQueryWrapper<OmsOrderItem> queryWrapper = new LambdaQueryWrapper<>(omsOrderItem);
        queryWrapper.orderByDesc(OmsOrderItem::getUpdateTime);
        List<OmsOrderItem> list = omsOrderItemService.list(queryWrapper);
        ExcelUtil<OmsOrderItem> util = new ExcelUtil<OmsOrderItem>(OmsOrderItem.class);
        util.exportExcel(response, list, "订单菜品数据");
    }

    /**
     * 获取订单菜品详细信息
     */
    @RequiresPermissions("@ss.hasPermi('oms:item:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<OmsOrderItem> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(omsOrderItemService.getById(id));
    }

    /**
     * 新增订单菜品
     */
    @RequiresPermissions("@ss.hasPermi('oms:item:add')")
    @Log(title = "订单菜品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody OmsOrderItem omsOrderItem) {
        Date date = new Date();
        omsOrderItem.setCreateTime(date);
        omsOrderItem.setUpdateTime(date);
        return AjaxResultResponse.success(omsOrderItemService.save(omsOrderItem));
    }

    /**
     * 修改订单菜品
     */
    @RequiresPermissions("@ss.hasPermi('oms:item:edit')")
    @Log(title = "订单菜品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody OmsOrderItem omsOrderItem) {
        omsOrderItem.setUpdateTime(new Date());
        return AjaxResultResponse.success(omsOrderItemService.updateById(omsOrderItem));
    }

    /**
     * 删除订单菜品
     */
    @RequiresPermissions("@ss.hasPermi('oms:item:remove')")
    @Log(title = "订单菜品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((omsOrderItemService.removeBatchByIds(ids)));
    }
}
