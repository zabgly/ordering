package com.ordering.ums.controller.backed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.cms.domain.CmsProduct;
import com.ordering.cms.service.ICmsProductService;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.core.utils.poi.ExcelUtil;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.log.annotation.Log;
import com.ordering.common.log.enums.BusinessType;
import com.ordering.common.security.annotation.RequiresPermissions;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ordering.ums.domain.UmsFavorite;
import com.ordering.ums.service.IUmsFavoriteService;

/**
 * 收藏Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/favorite")
public class UmsFavoriteController extends BaseController {
    @Resource
    private IUmsFavoriteService umsFavoriteService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询收藏列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:favorite:list')")
    @GetMapping("/list")
    public TableDataInfo list(UmsFavorite umsFavorite) {
        startPage();




        LambdaQueryWrapper<UmsFavorite> queryWrapper = new LambdaQueryWrapper<>(umsFavorite);

        // 复制非用户名的查询条件
        if (umsFavorite.getProductId() != null) {
            queryWrapper.eq(UmsFavorite::getProductId, umsFavorite.getProductId());
        }

        // 处理用户名查询条件
        if (StringUtils.isNotEmpty(umsFavorite.getUserName())) {
            LambdaQueryWrapper<UmsUser> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.like(UmsUser::getUserName, umsFavorite.getUserName());
            List<UmsUser> users = umsUserService.list(userQueryWrapper);
            if (!users.isEmpty()) {
                List<String> userIds = users.stream().map(UmsUser::getUserId).collect(Collectors.toList());
                queryWrapper.in(UmsFavorite::getUserId, userIds);
            } else {
                // 如果没找到匹配的用户，返回空结果
                return getDataTable(new ArrayList<>());
            }
        }
        queryWrapper.orderByDesc(UmsFavorite::getUpdateTime);
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
     * 导出收藏列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:favorite:export')")
    @Log(title = "收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UmsFavorite umsFavorite) {
        LambdaQueryWrapper<UmsFavorite> queryWrapper = new LambdaQueryWrapper<>(umsFavorite);
        queryWrapper.orderByDesc(UmsFavorite::getUpdateTime);
        List<UmsFavorite> list = umsFavoriteService.list(queryWrapper);
        ExcelUtil<UmsFavorite> util = new ExcelUtil<UmsFavorite>(UmsFavorite.class);
        util.exportExcel(response, list, "收藏数据");
    }

    /**
     * 获取收藏详细信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:favorite:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<UmsFavorite> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(umsFavoriteService.getById(id));
    }

    /**
     * 新增收藏
     */
    @RequiresPermissions("@ss.hasPermi('ums:favorite:add')")
    @Log(title = "收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody UmsFavorite umsFavorite) {
        Date date = new Date();
        umsFavorite.setCreateTime(date);
        umsFavorite.setUpdateTime(date);
        return AjaxResultResponse.success(umsFavoriteService.save(umsFavorite));
    }

    /**
     * 修改收藏
     */
    @RequiresPermissions("@ss.hasPermi('ums:favorite:edit')")
    @Log(title = "收藏", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody UmsFavorite umsFavorite) {
        umsFavorite.setUpdateTime(new Date());
        return AjaxResultResponse.success(umsFavoriteService.updateById(umsFavorite));
    }

    /**
     * 删除收藏
     */
    @RequiresPermissions("@ss.hasPermi('ums:favorite:remove')")
    @Log(title = "收藏", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsFavoriteService.removeBatchByIds(ids)));
    }
}
