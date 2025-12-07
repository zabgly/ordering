package com.ordering.ums.controller.backed;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.poi.ExcelUtil;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.log.annotation.Log;
import com.ordering.common.log.enums.BusinessType;
import com.ordering.common.security.annotation.RequiresPermissions;
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
import com.ordering.ums.domain.UmsCart;
import com.ordering.ums.service.IUmsCartService;

/**
 * 我的菜单Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/cart")
public class UmsCartController extends BaseController {
    @Resource
    private IUmsCartService umsCartService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询我的菜单列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:cart:list')")
    @GetMapping("/list")
    public TableDataInfo list(UmsCart umsCart) {
        String userName = umsCart.getUserName();
        startPage();
        QueryWrapper<UmsCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("user_id", "GROUP_CONCAT(product_id ORDER BY update_time DESC) AS productIds",
                        "GROUP_CONCAT(quantity ORDER BY update_time DESC) AS quantityList")
                .groupBy("user_id");
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
     * 导出我的菜单列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:cart:export')")
    @Log(title = "我的菜单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UmsCart umsCart) {
        LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>(umsCart);
        queryWrapper.orderByDesc(UmsCart::getUpdateTime);
        List<UmsCart> list = umsCartService.list(queryWrapper);
        ExcelUtil<UmsCart> util = new ExcelUtil<UmsCart>(UmsCart.class);
        util.exportExcel(response, list, "我的菜单数据");
    }

    /**
     * 获取我的菜单详细信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:cart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<UmsCart> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(umsCartService.getById(id));
    }

    /**
     * 新增我的菜单
     */
    @RequiresPermissions("@ss.hasPermi('ums:cart:add')")
    @Log(title = "我的菜单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody UmsCart cart) {
        String productId = cart.getProductId();
        String userId = cart.getUserId();
        if (productId == null || userId == null) {
            return AjaxResultResponse.error("id不可为空", null);
        }
        LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsCart::getUserId, userId)
                    .eq(UmsCart::getProductId, productId);
        UmsCart umsCart = umsCartService.getOne(queryWrapper);
        if (umsCart == null) {
            umsCart = new UmsCart();
            Date date = new Date();
            umsCart.setCreateTime(date);
            umsCart.setUpdateTime(date);
            return AjaxResultResponse.success(umsCartService.save(umsCart));
        }else{
            umsCart.setQuantity(umsCart.getQuantity() + cart.getQuantity());
            return AjaxResultResponse.success(umsCartService.updateById(umsCart));
        }
    }

    /**
     * 修改我的菜单
     */
    @RequiresPermissions("@ss.hasPermi('ums:cart:edit')")
    @Log(title = "我的菜单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody UmsCart umsCart) {
        umsCart.setUpdateTime(new Date());
        return AjaxResultResponse.success(umsCartService.updateById(umsCart));
    }

    /**
     * 删除我的菜单
     */
    @RequiresPermissions("@ss.hasPermi('ums:cart:remove')")
    @Log(title = "我的菜单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        for (String id : ids) {
            LambdaQueryWrapper<UmsCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UmsCart::getUserId, id);
            umsCartService.remove(queryWrapper);
        }
        return AjaxResultResponse.success(true);
    }


}
