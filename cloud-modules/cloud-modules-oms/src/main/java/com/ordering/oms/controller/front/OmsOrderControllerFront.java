package com.ordering.oms.controller.front;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.exception.ServiceException;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.security.utils.FrontSecurityUtils;
import com.ordering.oms.domain.OmsOrder;
import com.ordering.oms.domain.OmsOrderItem;
import com.ordering.oms.domain.dto.OmsPlaceOrder;
import com.ordering.oms.domain.dto.OmsPlaceOrderProductItem;
import com.ordering.oms.enums.OrderStatus;
import com.ordering.oms.service.IOmsOrderItemService;
import com.ordering.oms.service.IOmsOrderService;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsCart;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsCartService;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/order")
public class OmsOrderControllerFront extends BaseController {
    @Resource
    private IOmsOrderService omsOrderService;
    @Resource
    private IUmsUserService userService;
    @Resource
    private IOmsOrderItemService orderItemService;
    @Resource
    private ICmsProductService productService;
    @Resource
    private IUmsCartService cartService;

    /**
     * 我的订单
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<OmsOrder>> list(OmsOrder omsOrder) {
        LambdaQueryWrapper<OmsOrder> queryWrapper = new LambdaQueryWrapper<>(omsOrder);
        queryWrapper.orderByDesc(OmsOrder::getUpdateTime)
                .eq(OmsOrder::getUserId, FrontSecurityUtils.getUserId());
        List<OmsOrder> list = omsOrderService.list(queryWrapper);
        list.forEach(order -> {
            String orderId = order.getId();
            OmsOrderControllerFront.getOrderItemByOrderId(orderId, order, orderItemService, productService, userService);
        });
        return AjaxResultResponse.success(list);
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<OmsOrder> getInfo(@PathVariable("id") String orderId) {
        if (StringUtils.isEmpty(orderId)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        OmsOrder order = omsOrderService.getById(orderId);
        getOrderItemByOrderId(orderId, order, orderItemService, productService, userService);
        return AjaxResultResponse.success(order);
    }

    public static void getOrderItemByOrderId(@PathVariable("id") String orderId, OmsOrder order, IOmsOrderItemService orderItemService, ICmsProductService productService, IUmsUserService userService) {
        LambdaQueryWrapper<OmsOrderItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmsOrderItem::getOrderId, orderId);
        List<OmsOrderItem> omsOrderItemList = orderItemService.list(lambdaQueryWrapper);
        omsOrderItemList.forEach(orderItem -> {
            String productId = orderItem.getProductId();
            orderItem.setCmsProduct(productService.getById(productId));
        });
        order.setOmsOrderItemList(omsOrderItemList);
        UmsUser umsUser = userService.getById(order.getUserId());
        if (umsUser!=null){
            order.setUserName(umsUser.getUserName());
        }
    }

    /**
     * 菜品下单
     */
    @PostMapping("placeOrder")
    @Transactional
    public AjaxResultResponse<Boolean> placeOrder(@RequestBody OmsPlaceOrder omsPlaceOrder) {
        List<OmsPlaceOrderProductItem> omsPlaceOrderProductItemList = omsPlaceOrder.getOmsPlaceOrderProductItemList();
        if (CollUtil.isEmpty(omsPlaceOrderProductItemList)){
            return AjaxResultResponse.error("下单时菜品清单不可为空", null);
        }
        String paymentMethod = omsPlaceOrder.getPaymentMethod();
        if (StringUtils.isEmpty(paymentMethod)){
            return AjaxResultResponse.error("支付方式不可为空", null);
        }
        BigDecimal totalAmount = omsPlaceOrder.getTotalAmount();
        if (totalAmount==null){
            return AjaxResultResponse.error("金额不可为空", null);
        }
        //生成订单
        return AjaxResultResponse.success(createOrder(omsPlaceOrder));
    }

    public boolean createOrder(OmsPlaceOrder omsPlaceOrder) {
        //余额校验
        BigDecimal totalAmount = omsPlaceOrder.getTotalAmount();
        UmsUser umsUser = userService.getById(FrontSecurityUtils.getUserId());
        BigDecimal userBalance = umsUser.getUserBalance();
        if (totalAmount.compareTo(userBalance)>0){
            throw new ServiceException("用户余额不足", 500);
        }
        //订单生成
        OmsOrder order = new OmsOrder();
        String orderId = IdUtil.getSnowflakeNextIdStr();
        order.setId(orderId);
        order.setUserId(FrontSecurityUtils.getUserId());
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(omsPlaceOrder.getPaymentMethod());
        order.setOrderStatus(OrderStatus.COMPLETE.getValue());
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        //订单菜品详情
        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
        for (OmsPlaceOrderProductItem omsPlaceOrderProductItem : omsPlaceOrder.getOmsPlaceOrderProductItemList()) {
            String productId = omsPlaceOrderProductItem.getProductId();
            Long quantity = omsPlaceOrderProductItem.getQuantity();
            BigDecimal price = omsPlaceOrderProductItem.getPrice();
            //库存校验
            CmsProduct cmsProduct = productService.getById(productId);
            Long stock = cmsProduct.getStock();
            if (quantity.compareTo(stock)>0){
                throw new ServiceException(cmsProduct.getName()+"库存不足，请去除该菜品重新下单", 500);
            }
            //扣减库存
            cmsProduct.setStock(stock-quantity);
            productService.updateById(cmsProduct);
            //创建订单菜品并添加到list
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setId(IdUtil.getSnowflakeNextIdStr());
            omsOrderItem.setOrderId(orderId);
            omsOrderItem.setProductId(productId);
            omsOrderItem.setQuantity(quantity);
            omsOrderItem.setPrice(price);
            omsOrderItem.setIsReview(false);
            omsOrderItem.setCreateTime(date);
            omsOrderItem.setUpdateTime(date);
            omsOrderItemList.add(omsOrderItem);
            //是我的菜单，下单完毕要把我的菜单的给删了
            if (omsPlaceOrder.getIsCar()){
                LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UmsCart::getUserId, FrontSecurityUtils.getUserId())
                        .eq(UmsCart::getProductId, productId);
                cartService.remove(queryWrapper);
            }
        }
        //余额扣除
        umsUser.setUserBalance(userBalance.subtract(totalAmount));
        userService.updateById(umsUser);
        //下单
        return omsOrderService.save(order)&&orderItemService.saveBatch(omsOrderItemList);
    }



}
