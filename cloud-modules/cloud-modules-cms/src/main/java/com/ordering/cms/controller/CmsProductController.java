package com.ordering.cms.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProductCategory;
import com.ordering.cms.service.ICmsProductCategoryService;
import com.ordering.common.core.utils.StringUtils;
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
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;

/**
 * 菜品Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/product")
public class CmsProductController extends BaseController {
    @Resource
    private ICmsProductService cmsProductService;
    @Resource
    private ICmsProductCategoryService cmsProductCategoryService;

    /**
     * 查询菜品列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsProduct cmsProduct) {
        startPage();
        LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>(cmsProduct);
        queryWrapper.orderByDesc(CmsProduct::getUpdateTime);
        List<CmsProduct> list = cmsProductService.list(queryWrapper);
        list.forEach(product -> {
            String categoryId = product.getCategoryId();
            if (StringUtils.isNotEmpty(categoryId)){
                CmsProductCategory productCategory = cmsProductCategoryService.getById(categoryId);
                if (productCategory!=null){
                    product.setCategoryName(productCategory.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 导出菜品列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:product:export')")
    @Log(title = "菜品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CmsProduct cmsProduct) {
        LambdaQueryWrapper<CmsProduct> queryWrapper = new LambdaQueryWrapper<>(cmsProduct);
        queryWrapper.orderByDesc(CmsProduct::getUpdateTime);
        List<CmsProduct> list = cmsProductService.list(queryWrapper);
        ExcelUtil<CmsProduct> util = new ExcelUtil<CmsProduct>(CmsProduct.class);
        util.exportExcel(response, list, "菜品数据");
    }

    /**
     * 获取菜品详细信息
     */
    @RequiresPermissions("@ss.hasPermi('cms:product:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsProduct> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(cmsProductService.getById(id));
    }

    /**
     * 新增菜品
     */
    @RequiresPermissions("@ss.hasPermi('cms:product:add')")
    @Log(title = "菜品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody CmsProduct cmsProduct) {
        Date date = new Date();
        cmsProduct.setCreateTime(date);
        cmsProduct.setUpdateTime(date);
        return AjaxResultResponse.success(cmsProductService.save(cmsProduct));
    }

    /**
     * 修改菜品
     */
    @RequiresPermissions("@ss.hasPermi('cms:product:edit')")
    @Log(title = "菜品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody CmsProduct cmsProduct) {
        cmsProduct.setUpdateTime(new Date());
        return AjaxResultResponse.success(cmsProductService.updateById(cmsProduct));
    }

    /**
     * 删除菜品
     */
    @RequiresPermissions("@ss.hasPermi('cms:product:remove')")
    @Log(title = "菜品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((cmsProductService.removeBatchByIds(ids)));
    }
}
