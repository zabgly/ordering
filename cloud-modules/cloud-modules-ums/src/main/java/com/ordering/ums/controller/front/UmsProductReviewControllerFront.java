package com.ordering.ums.controller.front;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.log.annotation.Log;
import com.ordering.common.log.enums.BusinessType;
import com.ordering.common.security.utils.FrontSecurityUtils;
import com.ordering.oms.domain.OmsOrder;
import com.ordering.oms.domain.OmsOrderItem;
import com.ordering.oms.domain.dto.OmsNotReview;
import com.ordering.oms.enums.LogisticsStatus;
import com.ordering.oms.enums.OrderStatus;
import com.ordering.oms.service.IOmsOrderItemService;
import com.ordering.oms.service.IOmsOrderService;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsProductReview;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsProductReviewService;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜品评价Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/review")
public class UmsProductReviewControllerFront extends BaseController {
    @Resource
    private IUmsProductReviewService umsProductReviewService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;
    @Resource
    private IOmsOrderService orderService;
    @Resource
    private IOmsOrderItemService orderItemService;

    /**
     * 待评价-订单结束后对菜品的评价
     */
    @GetMapping("/notReviewList")
    public TableDataInfo notReviewList() {
        List<OmsNotReview> omsNotReviewList = new ArrayList<>();
        // 查询符合条件的订单
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OmsOrder::getOrderStatus, OrderStatus.COMPLETE.getValue())
                .eq(OmsOrder::getUserId, FrontSecurityUtils.getUserId())
                .orderByDesc(OmsOrder::getUpdateTime);
        List<OmsOrder> omsOrderList = orderService.list(queryWrapper);
        if (CollUtil.isNotEmpty(omsOrderList)) {
            // 提取订单ID列表
            List<String> orderIdList = omsOrderList.stream()
                    .map(OmsOrder::getId)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(orderIdList)) {
                // 查询未评价的订单菜品
                LambdaQueryWrapper<OmsOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.in(OmsOrderItem::getOrderId, orderIdList)
                        .eq(OmsOrderItem::getIsReview, false);
                List<OmsOrderItem> omsOrderItemList = orderItemService.list(lambdaQueryWrapper);
                if (CollUtil.isNotEmpty(omsOrderItemList)) {
                    // 提取菜品ID列表
                    List<String> productIdList = omsOrderItemList.stream()
                            .map(OmsOrderItem::getProductId)
                            .collect(Collectors.toList());

                    if (CollUtil.isNotEmpty(productIdList)) {
                        // 查询菜品信息
                        List<CmsProduct> productList = cmsProductService.listByIds(productIdList);
                        // 将菜品信息按订单分组
                        Map<String, List<CmsProduct>> orderProductMap = new HashMap<>();
                        for (OmsOrderItem omsOrderItem : omsOrderItemList) {
                            String orderId = omsOrderItem.getOrderId();
                            CmsProduct product = productList.stream()
                                    .filter(p -> p.getId().equals(omsOrderItem.getProductId()))
                                    .findFirst()
                                    .orElse(null);
                            if (product != null) {
                                orderProductMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(product);
                            }
                        }
                        // 填充 OmsNotReview 对象
                        for (Map.Entry<String, List<CmsProduct>> entry : orderProductMap.entrySet()) {
                            String orderId = entry.getKey();
                            List<CmsProduct> products = entry.getValue();
                            OmsNotReview omsNotReview = new OmsNotReview();
                            omsNotReview.setOrderId(orderId);
                            omsNotReview.setCmsProductList(products);
                            omsNotReviewList.add(omsNotReview);
                        }
                    }
                }
            }
        }
        return getDataTable(omsNotReviewList);
    }


    /**
     * 查询菜品评价列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        LambdaQueryWrapper<UmsProductReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UmsProductReview::getUpdateTime)
                .eq(UmsProductReview::getUserId, FrontSecurityUtils.getUserId());
        List<UmsProductReview> list = umsProductReviewService.list(queryWrapper);
        list.forEach(productReview -> {
            // 获取用户信息并设置用户名
            String userId = productReview.getUserId();
            if (StringUtils.isNotEmpty(userId)) {
                UmsUser user = umsUserService.getById(userId);
                if (user != null) {
                    productReview.setUserName(user.getUserName());
                }
            }
            // 获取菜品信息并设置菜品图片和名称
            String productId = productReview.getProductId();
            if (StringUtils.isNotEmpty(productId)) {
                CmsProduct product = cmsProductService.getById(productId);
                if (product != null) {
                    productReview.setProductImage(product.getImageUrl());
                    productReview.setProductName(product.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 发起评价-订单完成后
     */
    @PostMapping(value = "addReview")
    @Transactional
    public AjaxResultResponse<Boolean> addReview(@RequestBody UmsProductReview umsProductReview) {
        String orderId = umsProductReview.getOrderId();
        String productId = umsProductReview.getProductId();
        if (StringUtils.isEmpty(orderId)) {
            return AjaxResultResponse.error("订单Id不可为空", null);
        }
        //更改orderItem为已评论
        LambdaUpdateWrapper<OmsOrderItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(OmsOrderItem::getIsReview, true).eq(OmsOrderItem::getOrderId, orderId)
                .eq(OmsOrderItem::getProductId, productId);
        orderItemService.update(updateWrapper);
        umsProductReview.setUserId(FrontSecurityUtils.getUserId());
        Date date = new Date();
        umsProductReview.setCreateTime(date);
        umsProductReview.setUpdateTime(date);
        return AjaxResultResponse.success(umsProductReviewService.save(umsProductReview));
    }

    /**
     * 删除菜品评价
     */
    @Log(title = "菜品评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsProductReviewService.removeBatchByIds(ids)));
    }


}
