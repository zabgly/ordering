package com.ordering.cms.controller;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.ordering.cms.domain.CmsProductPromotion;
import com.ordering.cms.service.ICmsProductPromotionService;

/**
 * 菜品促销关联Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/link")
public class CmsProductPromotionController extends BaseController {
    @Resource
    private ICmsProductPromotionService cmsProductPromotionService;

    /**
     * 查询菜品促销关联列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsProductPromotion cmsProductPromotion) {
        startPage();
        LambdaQueryWrapper<CmsProductPromotion> queryWrapper = new LambdaQueryWrapper<>(cmsProductPromotion);
        List<CmsProductPromotion> list = cmsProductPromotionService.list(queryWrapper);
        return getDataTable(list);
    }

    /**
     * 导出菜品促销关联列表
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:export')")
    @Log(title = "菜品促销关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CmsProductPromotion cmsProductPromotion) {
        LambdaQueryWrapper<CmsProductPromotion> queryWrapper = new LambdaQueryWrapper<>(cmsProductPromotion);
        queryWrapper.orderByDesc(CmsProductPromotion::getUpdateTime);
        List<CmsProductPromotion> list = cmsProductPromotionService.list(queryWrapper);
        ExcelUtil<CmsProductPromotion> util = new ExcelUtil<CmsProductPromotion>(CmsProductPromotion.class);
        util.exportExcel(response, list, "菜品促销关联数据");
    }

    /**
     * 获取菜品促销关联详细信息
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<CmsProductPromotion> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(cmsProductPromotionService.getById(id));
    }

    /**
     * 新增菜品促销关联
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:add')")
    @Log(title = "菜品促销关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody CmsProductPromotion cmsProductPromotion) {
        Date date = new Date();
        cmsProductPromotion.setCreateTime(date);
        cmsProductPromotion.setUpdateTime(date);
        return AjaxResultResponse.success(cmsProductPromotionService.save(cmsProductPromotion));
    }

    /**
     * 修改菜品促销关联
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:edit')")
    @Log(title = "菜品促销关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody CmsProductPromotion cmsProductPromotion) {
        cmsProductPromotion.setUpdateTime(new Date());
        return AjaxResultResponse.success(cmsProductPromotionService.updateById(cmsProductPromotion));
    }

    /**
     * 删除菜品促销关联
     */
    @RequiresPermissions("@ss.hasPermi('cms:promotion:remove')")
    @Log(title = "菜品促销关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((cmsProductPromotionService.removeBatchByIds(ids)));
    }
}
