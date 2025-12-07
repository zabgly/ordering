package com.ordering.cms.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.enums.ProductStatus;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.security.utils.FrontSecurityUtils;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsFavorite;
import com.ordering.ums.domain.UmsProductReview;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsBrowseHistoryService;
import com.ordering.ums.service.IUmsFavoriteService;
import com.ordering.ums.service.IUmsProductReviewService;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/product")
public class CmsProductControllerFront extends BaseController {
    @Resource
    private ICmsProductService cmsProductService;
    @Resource
    private IUmsBrowseHistoryService umsBrowseHistoryService;
    @Resource
    private IUmsProductReviewService umsProductReviewService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private IUmsFavoriteService umsFavoriteService;

    /**
     * 查询菜品列表
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<CmsProduct>> list(CmsProduct cmsProduct) {
        LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>(cmsProduct);
        queryWrapper.orderByDesc(CmsProduct::getUpdateTime)
                .eq(CmsProduct::getProductStatus, ProductStatus.UNSOLD.getValue());
        List<CmsProduct> list = cmsProductService.list(queryWrapper);
        return AjaxResultResponse.success(list);
    }

    /**
     * 搜索菜品
     */
    @GetMapping(value = "/searchProduct")
    public AjaxResultResponse<List<CmsProduct>> searchProduct(
            @RequestParam(value = "searchContent", required = false) String searchContent,
            @RequestParam(value = "categoryId", required = false) String categoryId ) {
        LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CmsProduct::getUpdateTime);
        if (StringUtils.isNotBlank(searchContent)){
            queryWrapper.and(wrapper -> wrapper.like(CmsProduct::getName, searchContent)
                    .or().like(CmsProduct::getDescription, searchContent));
        }
        if (StringUtils.isNotBlank(categoryId)) {
            queryWrapper.eq(CmsProduct::getCategoryId, categoryId);
        }
        List<CmsProduct> cmsProductList = cmsProductService.list(queryWrapper);
        return AjaxResultResponse.success(cmsProductList);
    }

    /**
     * 获取菜品详细信息-需登录
     */
    @GetMapping(value = "/getById/{id}")
    public AjaxResultResponse<CmsProduct> getInfo(@PathVariable("id") String productId) {
        if (StringUtils.isEmpty(productId)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        CmsProduct cmsProduct = cmsProductService.getById(productId);
        LambdaQueryWrapper<UmsProductReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsProductReview::getProductId, productId);
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
        });
        cmsProduct.setProductReviewList(list);
        //查询是否收藏
        LambdaQueryWrapper<UmsFavorite> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UmsFavorite::getProductId, productId)
                .eq(UmsFavorite::getUserId, FrontSecurityUtils.getUserId());
        UmsFavorite umsFavorite = umsFavoriteService.getOne(lambdaQueryWrapper);
        cmsProduct.setIsCollect(umsFavorite != null);
        //查询是否是促销产品
        cmsProductService.queryIsPromotion(cmsProduct);
        //异步记录用户浏览记录
        String userId = FrontSecurityUtils.getUserId();
        if (StringUtils.isNotEmpty(userId)&&!"0".equals(userId) ){
            umsBrowseHistoryService.asyncRecordBrowseHistory(userId,productId);
        }
        return AjaxResultResponse.success(cmsProduct);
    }




}
