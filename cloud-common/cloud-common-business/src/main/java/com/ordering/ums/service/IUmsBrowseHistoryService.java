package com.ordering.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ordering.ums.domain.UmsBrowseHistory;

/**
 * 浏览记录Service接口
 * 
 * @author ordering
 * @date 2025-03-15
 */
public interface IUmsBrowseHistoryService extends IService<UmsBrowseHistory> {
    /**
     * 异步记录菜品浏览历史
     *
     * @param userId    用户ID
     * @param productId 菜品ID
     */
    void asyncRecordBrowseHistory(String userId, String productId);
}
