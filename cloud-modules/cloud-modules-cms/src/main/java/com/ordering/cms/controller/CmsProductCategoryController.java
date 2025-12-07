package com.ordering.cms.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
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
import com.ordering.cms.domain.CmsProductCategory;
import com.ordering.cms.service.ICmsProductCategoryService;

/**
 * 菜品分类Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/category")
public class CmsProductCategoryController extends BaseController {
    @Resource
    private ICmsProductCategoryService cmsProductCategoryService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询菜品分类列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsProductCategory cmsProductCategory) {
        PageHelper.startPage(1,9999);
        LambdaQueryWrapper<CmsProductCategory> queryWrapper = new LambdaQueryWrapper<>(cmsProductCategory);
        queryWrapper.orderByAsc(CmsProductCategory::getUpdateTime);
        List<CmsProductCategory> cmsProductCategoryList = cmsProductCategoryService.list(queryWrapper);
        cmsProductCategoryList.forEach(productCategory -> {
            String categoryId = productCategory.getId();
            LambdaQueryWrapper<CmsProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(CmsProduct::getCategoryId, categoryId);
            List<CmsProduct> cmsProductList = cmsProductService.list(lambdaQueryWrapper);
            productCategory.setCmsProductList(cmsProductList);
        });
        List<CmsProductCategory> cmsProductCategoryListTree = cmsProductCategoryService.buildTree(cmsProductCategoryList);
        return getDataTable(cmsProductCategoryListTree);
    }

    /**
     * 获取叶子节点数据
     */
    @GetMapping("/listLeafNodes")
    public AjaxResultResponse<List<CmsProductCategory>> listLeafNodes() {
        LambdaQueryWrapper<CmsProductCategory> queryWrapper = new LambdaQueryWrapper<>();
        List<CmsProductCategory> cmsProductCategoryList = cmsProductCategoryService.list(queryWrapper);
        List<CmsProductCategory> cmsProductCategoryListLeafNodes = cmsProductCategoryService.getAllLeafNodes(cmsProductCategoryList);
        return AjaxResultResponse.success(cmsProductCategoryListLeafNodes);
    }

    /**
     * 导出菜品分类列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:category:export')")
    @Log(title = "菜品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CmsProductCategory cmsProductCategory) {
        LambdaQueryWrapper<CmsProductCategory> queryWrapper = new LambdaQueryWrapper<>(cmsProductCategory);
        queryWrapper.orderByDesc(CmsProductCategory::getUpdateTime);
        List<CmsProductCategory> list = cmsProductCategoryService.list(queryWrapper);
        ExcelUtil<CmsProductCategory> util = new ExcelUtil<CmsProductCategory>(CmsProductCategory.class);
        util.exportExcel(response, list, "菜品分类数据");
    }

    /**
     * 获取菜品分类详细信息
     */
    @RequiresPermissions("@ss.hasPermi('cms:category:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsProductCategory> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(cmsProductCategoryService.getById(id));
    }

    /**
     * 新增菜品分类
     */
    @RequiresPermissions("@ss.hasPermi('cms:category:add')")
    @Log(title = "菜品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody CmsProductCategory cmsProductCategory) {
        Date date = new Date();
        cmsProductCategory.setCreateTime(date);
        cmsProductCategory.setUpdateTime(date);
        return AjaxResultResponse.success(cmsProductCategoryService.save(cmsProductCategory));
    }

    /**
     * 修改菜品分类
     */
    @RequiresPermissions("@ss.hasPermi('cms:category:edit')")
    @Log(title = "菜品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody CmsProductCategory cmsProductCategory) {
        cmsProductCategory.setUpdateTime(new Date());
        return AjaxResultResponse.success(cmsProductCategoryService.updateById(cmsProductCategory));
    }

    /**
     * 删除菜品分类
     */
    @RequiresPermissions("@ss.hasPermi('cms:category:remove')")
    @Log(title = "菜品分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((cmsProductCategoryService.removeBatchByIds(ids)));
    }
}
