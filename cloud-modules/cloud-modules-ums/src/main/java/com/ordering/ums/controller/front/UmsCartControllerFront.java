package com.ordering.ums.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.security.utils.FrontSecurityUtils;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsCart;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsCartService;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 我的菜单Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/cart")
public class UmsCartControllerFront extends BaseController {
    @Resource
    private IUmsCartService umsCartService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查用户询我的菜单
     */
    @GetMapping("/list")
    public TableDataInfo list(UmsCart umsCart) {
        String userName = umsCart.getUserName();
        QueryWrapper<UmsCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id", "GROUP_CONCAT(product_id ORDER BY update_time DESC) AS productIds",
                        "GROUP_CONCAT(quantity ORDER BY update_time DESC) AS quantityList")
                .groupBy("user_id").eq("user_id", FrontSecurityUtils.getUserId());
        if (userName != null) {
            LambdaQueryWrapper<UmsUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.like(UmsUser::getUserName, userName);
            //查询条件
            UmsUser umsUser = umsUserService.getOne(lambdaQueryWrapper);
            if (umsUser != null) {
                queryWrapper.eq("user_id", umsUser.getUserId());
            }else{
                //无数据
                queryWrapper.eq("user_id", "0");
            }
        }
        List<UmsCart> list = umsCartService.list(queryWrapper);
        list.forEach(cart -> {
            cart.setUmsUser(umsUserService.getById(cart.getUserId()));
            // 获取菜品列表（按 productIds 查询）
            List<CmsProduct> productList = cmsProductService.listByIds(Arrays.asList(cart.getProductIds().split(",")));
            productList.forEach(product -> {
                //查询是否是促销产品
                cmsProductService.queryIsPromotion(product);
            });
            // 获取数量列表
            List<String> quantityList = Arrays.asList(cart.getQuantityList().split(","));
            // 确保两个列表长度一致，避免索引越界
            if (productList.size() == quantityList.size()) {
                for (int i = 0; i < productList.size(); i++) {
                    productList.get(i).setQuantity(quantityList.get(i)); // 按索引赋值
                }
            } else {
                throw new RuntimeException("菜品列表和数量列表长度不匹配！");
            }
            cart.setCmsProductList(productList);
        });
        return getDataTable(list);
    }

    /**
     * 菜品加入菜单
     */
    @PostMapping(value = "addProductInCart")
    public AjaxResultResponse<Boolean> addCart(@RequestBody UmsCart cart) {
        String productId = cart.getProductId();
        if (StringUtils.isBlank(productId)) {
            return AjaxResultResponse.error("id不可为空", null);
        }
        String userId = FrontSecurityUtils.getUserId();
        cart.setUserId(userId);
        UmsCart umsCart = quantityNum(userId, productId);
        //扣除数量,如果当前数量为1，那么删掉数据
        if (umsCart == null) {
            cart.setQuantity(1L);
            Date date = new Date();
            cart.setCreateTime(date);
            cart.setUpdateTime(date);
            return AjaxResultResponse.success(umsCartService.save(cart));
        } else if (umsCart.getQuantity() > 0L) {
            cart.setId(umsCart.getId());
            return AjaxResultResponse.success(umsCartService.updateById(cart));
        }
        return AjaxResultResponse.error("数据不规范", null);
    }

    /**
     * 菜品从我的菜单中删除
     */
    @PostMapping(value = "removeProductInCart")
    public AjaxResultResponse<Boolean> removeProductInCart(@RequestBody UmsCart cart) {
        String productId = cart.getProductId();
        if (StringUtils.isBlank(productId)) {
            return AjaxResultResponse.error("id不可为空", null);
        }
        String userId = FrontSecurityUtils.getUserId();
        cart.setUserId(userId);
        UmsCart umsCart = quantityNum(userId, productId);
        Long currentQuantity = umsCart.getQuantity();
        cart.setId(umsCart.getId());
        //扣除数量,如果当前数量为1，那么删掉数据
        if (currentQuantity > 1L) {
            return AjaxResultResponse.success(umsCartService.updateById(cart));
        } else if (currentQuantity == 1L) {
            return AjaxResultResponse.success("删除成功", umsCartService.removeById(cart));
        }
        return AjaxResultResponse.error("数据不规范", null);
    }

    //查看当前我的菜单数量，如果数量为0那就是不存在
    private UmsCart quantityNum(String userId, String productId) {
        LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsCart::getUserId, userId)
                .eq(UmsCart::getProductId, productId);
        return umsCartService.getOne(queryWrapper);
    }



}
