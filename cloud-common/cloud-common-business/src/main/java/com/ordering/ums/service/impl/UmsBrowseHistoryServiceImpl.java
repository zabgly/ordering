package com.ordering.ums.service.impl;

import java.util.Date;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ordering.ums.mapper.UmsBrowseHistoryMapper;
import com.ordering.ums.domain.UmsBrowseHistory;
import com.ordering.ums.service.IUmsBrowseHistoryService;

/**
 * 浏览记录Service业务层处理
 *
 * @author ordering
 * @date 2025-03-15
 */
@Service
public class UmsBrowseHistoryServiceImpl extends ServiceImpl<UmsBrowseHistoryMapper, UmsBrowseHistory> implements IUmsBrowseHistoryService {

    @Override
    @Async // 标记为异步方法
    public synchronized void asyncRecordBrowseHistory(String userId, String productId) {
        //判断是否已存在
        LambdaQueryWrapper<UmsBrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmsBrowseHistory::getUserId, userId)
                .eq(UmsBrowseHistory::getProductId, productId);
        UmsBrowseHistory umsBrowseHistory = this.getOne(queryWrapper);
        if (umsBrowseHistory!=null) {
            // 更新时间
            umsBrowseHistory.setBrowseTime(new Date());
            this.updateById(umsBrowseHistory);
        }else{
            // 创建浏览记录对象
            UmsBrowseHistory browseHistory = new UmsBrowseHistory();
            browseHistory.setId(IdUtil.getSnowflakeNextIdStr()); // 生成唯一ID
            browseHistory.setUserId(userId);
            browseHistory.setProductId(productId);
            browseHistory.setBrowseTime(new Date());
            // 保存到数据库
            this.save(browseHistory);
        }

    }

}
