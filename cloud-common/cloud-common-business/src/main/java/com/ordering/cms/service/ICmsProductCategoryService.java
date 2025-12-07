package com.ordering.cms.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ordering.cms.domain.CmsProductCategory;

/**
 * 菜品分类Service接口
 * 
 * @author ordering
 * @date 2025-03-15
 */
public interface ICmsProductCategoryService extends IService<CmsProductCategory> {

    List<CmsProductCategory> buildTree(List<CmsProductCategory> categories);
    List<CmsProductCategory> getAllLeafNodes(List<CmsProductCategory> categories);
}
