package com.ordering.oms.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.poi.ExcelUtil;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.log.annotation.Log;
import com.ordering.common.log.enums.BusinessType;
import com.ordering.common.security.annotation.RequiresPermissions;
import com.ordering.oms.controller.front.OmsOrderControllerFront;
import com.ordering.oms.domain.OmsOrderItem;
import com.ordering.oms.service.IOmsOrderItemService;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ordering.oms.domain.OmsOrder;
import com.ordering.oms.service.IOmsOrderService;

/**
 * 订单Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/order")
public class OmsOrderController extends BaseController {
    @Resource
    private IOmsOrderService omsOrderService;
    @Resource
    private IUmsUserService userService;
    @Resource
    private IOmsOrderItemService orderItemService;
    @Resource
    private ICmsProductService productService;

    /**
     * 查询订单列表
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(OmsOrder omsOrder) {
        startPage();
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>(omsOrder);
        queryWrapper.orderByDesc(OmsOrder::getUpdateTime);
        List<OmsOrder> list = omsOrderService.list(queryWrapper);
        list.forEach(order -> {
            String orderId = order.getId();
            OmsOrderControllerFront.getOrderItemByOrderId(orderId, order, orderItemService, productService, userService);
        });
        return getDataTable(list);
    }

    /**
     * 查询订单菜品列表
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:list')")
    @GetMapping("/allList")
    public TableDataInfo allList() {
        List<OmsOrder> list = omsOrderService.list();
        list.forEach(order -> {
            String orderId = order.getId();
            OmsOrderControllerFront.getOrderItemByOrderId(orderId, order, orderItemService, productService, userService);
        });
        return getDataTable(list);
    }

    /**
     * 下拉框查询用户
     */
    @GetMapping("/listUser")
    public AjaxResultResponse<List<UmsUser>> listUser() {
        return AjaxResultResponse.success(userService.list());
    }

    /**
     * 导出订单列表
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmsOrder omsOrder) {
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>(omsOrder);
        queryWrapper.orderByDesc(OmsOrder::getUpdateTime);
        List<OmsOrder> list = omsOrderService.list(queryWrapper);
        ExcelUtil<OmsOrder> util = new ExcelUtil<OmsOrder>(OmsOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<OmsOrder> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(omsOrderService.getById(id));
    }

    /**
     * 新增订单
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody OmsOrder omsOrder) {
        Date date = new Date();
        omsOrder.setCreateTime(date);
        omsOrder.setUpdateTime(date);
        return AjaxResultResponse.success(omsOrderService.save(omsOrder));
    }

    /**
     * 修改订单
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody OmsOrder omsOrder) {
        omsOrder.setUpdateTime(new Date());
        return AjaxResultResponse.success(omsOrderService.updateById(omsOrder));
    }

    /**
     * 删除订单
     */
    @RequiresPermissions("@ss.hasPermi('oms:order:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((omsOrderService.removeBatchByIds(ids)));
    }
}
