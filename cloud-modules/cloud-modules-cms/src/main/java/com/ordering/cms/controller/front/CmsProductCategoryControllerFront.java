package com.ordering.cms.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.domain.CmsProductCategory;
import com.ordering.cms.enums.ProductStatus;
import com.ordering.cms.service.ICmsProductCategoryService;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品分类Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/category")
public class CmsProductCategoryControllerFront extends BaseController {
    @Resource
    private ICmsProductCategoryService cmsProductCategoryService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询菜品分类列表
     */
    @GetMapping("/list")
    public AjaxResultResponse<List<CmsProductCategory>> list() {
        LambdaQueryWrapper<CmsProductCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CmsProductCategory::getUpdateTime);
        List<CmsProductCategory> cmsProductCategoryList = cmsProductCategoryService.list(queryWrapper);
        //拿到树
        return AjaxResultResponse.success(cmsProductCategoryService.buildTree(cmsProductCategoryList));
    }

    /**
     * 获取菜品分类详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsProductCategory> getInfo(@PathVariable("id") String categoryId) {
        if (StringUtils.isEmpty(categoryId)){
            return AjaxResultResponse.error("id不可为空", null);
        }
        CmsProductCategory productCategory = cmsProductCategoryService.getById(categoryId);
        getProductByCategoryId(categoryId, productCategory);
        return AjaxResultResponse.success(productCategory);
    }

    private void getProductByCategoryId(@PathVariable("id") String categoryId, CmsProductCategory productCategory) {
        LambdaQueryWrapper<CmsProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CmsProduct::getCategoryId, categoryId)
                .eq(CmsProduct::getProductStatus, ProductStatus.UNSOLD.getValue());
        List<CmsProduct> cmsProductList = cmsProductService.list(lambdaQueryWrapper);
        productCategory.setCmsProductList(cmsProductList);
    }


}
