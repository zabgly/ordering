package com.ordering.ums.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.log.annotation.Log;
import com.ordering.common.log.enums.BusinessType;
import com.ordering.common.security.utils.FrontSecurityUtils;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsBrowseHistory;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsBrowseHistoryService;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 浏览记录Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/history")
public class UmsBrowseHistoryControllerFront extends BaseController {
    @Resource
    private IUmsBrowseHistoryService umsBrowseHistoryService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询浏览记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        LambdaQueryWrapper<UmsBrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UmsBrowseHistory::getBrowseTime)
                .eq(UmsBrowseHistory::getUserId, FrontSecurityUtils.getUserId());
        List<UmsBrowseHistory> list = umsBrowseHistoryService.list(queryWrapper);
        list.forEach(browseHistory -> {
            // 获取用户信息并设置用户名
            String userId = browseHistory.getUserId();
            if (StringUtils.isNotEmpty(userId)) {
                UmsUser user = umsUserService.getById(userId);
                if (user != null) {
                    browseHistory.setUserName(user.getUserName());
                }
            }
            // 获取菜品信息并设置菜品图片和名称
            String productId = browseHistory.getProductId();
            if (StringUtils.isNotEmpty(productId)) {
                CmsProduct product = cmsProductService.getById(productId);
                if (product != null) {
                    browseHistory.setProductImage(product.getImageUrl());
                    browseHistory.setProductName(product.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 删除浏览记录
     */
    @Log(title = "浏览记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsBrowseHistoryService.removeBatchByIds(ids)));
    }

}
