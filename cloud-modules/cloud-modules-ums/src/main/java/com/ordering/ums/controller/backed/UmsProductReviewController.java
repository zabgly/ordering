package com.ordering.ums.controller.backed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
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
import com.ordering.ums.domain.UmsProductReview;
import com.ordering.ums.service.IUmsProductReviewService;

/**
 * 菜品评价Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/review")
public class UmsProductReviewController extends BaseController {
    @Resource
    private IUmsProductReviewService umsProductReviewService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询菜品评价列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(UmsProductReview umsProductReview) {
        startPage();

        LambdaQueryWrapper<UmsProductReview> queryWrapper = new LambdaQueryWrapper<>(umsProductReview);

        // 复制非用户名的查询条件
        if (umsProductReview.getProductId() != null) {
            queryWrapper.eq(UmsProductReview::getProductId, umsProductReview.getProductId());
        }
        if (umsProductReview.getRating() != null) {
            queryWrapper.eq(UmsProductReview::getRating, umsProductReview.getRating());
        }
        // 添加其他字段的查询条件...

        // 处理用户名查询条件
        if (StringUtils.isNotEmpty(umsProductReview.getUserName())) {
            LambdaQueryWrapper<UmsUser> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.like(UmsUser::getUserName, umsProductReview.getUserName());
            List<UmsUser> users = umsUserService.list(userQueryWrapper);
            if (!users.isEmpty()) {
                List<String> userIds = users.stream().map(UmsUser::getUserId).collect(Collectors.toList());
                queryWrapper.in(UmsProductReview::getUserId, userIds);
            } else {
                // 如果没找到匹配的用户，返回空结果
                return getDataTable(new ArrayList<>());
            }
        }



        queryWrapper.orderByDesc(UmsProductReview::getUpdateTime);
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
     * 导出菜品评价列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:review:export')")
    @Log(title = "菜品评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UmsProductReview umsProductReview) {
        LambdaQueryWrapper<UmsProductReview> queryWrapper = new LambdaQueryWrapper<>(umsProductReview);
        queryWrapper.orderByDesc(UmsProductReview::getUpdateTime);
        List<UmsProductReview> list = umsProductReviewService.list(queryWrapper);
        ExcelUtil<UmsProductReview> util = new ExcelUtil<UmsProductReview>(UmsProductReview.class);
        util.exportExcel(response, list, "菜品评价数据");
    }

    /**
     * 获取菜品评价详细信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:review:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<UmsProductReview> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(umsProductReviewService.getById(id));
    }

    /**
     * 新增菜品评价
     */
    @RequiresPermissions("@ss.hasPermi('ums:review:add')")
    @Log(title = "菜品评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody UmsProductReview umsProductReview) {
        Date date = new Date();
        umsProductReview.setCreateTime(date);
        umsProductReview.setUpdateTime(date);
        return AjaxResultResponse.success(umsProductReviewService.save(umsProductReview));
    }

    /**
     * 修改菜品评价
     */
    @RequiresPermissions("@ss.hasPermi('ums:review:edit')")
    @Log(title = "菜品评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody UmsProductReview umsProductReview) {
        umsProductReview.setUpdateTime(new Date());
        return AjaxResultResponse.success(umsProductReviewService.updateById(umsProductReview));
    }

    /**
     * 删除菜品评价
     */
    @RequiresPermissions("@ss.hasPermi('ums:review:remove')")
    @Log(title = "菜品评价", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsProductReviewService.removeBatchByIds(ids)));
    }
}
