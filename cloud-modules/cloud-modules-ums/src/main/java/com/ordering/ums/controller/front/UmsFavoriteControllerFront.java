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
import com.ordering.ums.domain.UmsFavorite;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsFavoriteService;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 收藏Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/front/favorite")
public class UmsFavoriteControllerFront extends BaseController {
    @Resource
    private IUmsFavoriteService umsFavoriteService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询收藏列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        LambdaQueryWrapper<UmsFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UmsFavorite::getUpdateTime)
                .eq(UmsFavorite::getUserId, FrontSecurityUtils.getUserId());
        List<UmsFavorite> list = umsFavoriteService.list(queryWrapper);
        list.forEach(favorite -> {
            // 获取用户信息并设置用户名
            String userId = favorite.getUserId();
            if (StringUtils.isNotEmpty(userId)) {
                UmsUser user = umsUserService.getById(userId);
                if (user != null) {
                    favorite.setUserName(user.getUserName());
                }
            }
            // 获取菜品信息并设置菜品图片和名称
            String productId = favorite.getProductId();
            if (StringUtils.isNotEmpty(productId)) {
                CmsProduct product = cmsProductService.getById(productId);
                if (product != null) {
                    favorite.setProductImage(product.getImageUrl());
                    favorite.setProductName(product.getName());
                }
            }
        });
        return getDataTable(list);
    }

    /**
     * 添加收藏
     */
    @PostMapping(value = "addCollect")
    public AjaxResultResponse<Boolean> addCollect(@RequestBody UmsFavorite umsFavorite) {
        umsFavorite.setUserId(FrontSecurityUtils.getUserId());
        Date date = new Date();
        umsFavorite.setCreateTime(date);
        umsFavorite.setUpdateTime(date);
        return AjaxResultResponse.success(umsFavoriteService.save(umsFavorite));
    }

    /**
     * 删除收藏
     */
    @Log(title = "收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsFavoriteService.removeBatchByIds(ids)));
    }


}
