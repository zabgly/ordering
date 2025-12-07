package com.ordering.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ordering.cms.domain.CmsProduct;

/**
 * 菜品Service接口
 * 
 * @author ordering
 * @date 2025-03-15
 */
public interface ICmsProductService extends IService<CmsProduct> {

    void queryIsPromotion(CmsProduct cmsProduct);
}
