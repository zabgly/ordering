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
import com.ordering.ums.domain.UmsBrowseHistory;
import com.ordering.ums.service.IUmsBrowseHistoryService;

/**
 * 浏览记录Controller
 * 
 * @author ordering
 * @date 2025-03-15
 */
@RestController
@RequestMapping("/history")
public class UmsBrowseHistoryController extends BaseController {
    @Resource
    private IUmsBrowseHistoryService umsBrowseHistoryService;
    @Resource
    private IUmsUserService umsUserService;
    @Resource
    private ICmsProductService cmsProductService;

    /**
     * 查询浏览记录列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:history:list')")
    @GetMapping("/list")
    public TableDataInfo list(UmsBrowseHistory umsBrowseHistory) {
        startPage();

        LambdaQueryWrapper<UmsBrowseHistory> queryWrapper = new LambdaQueryWrapper<>(umsBrowseHistory);

        // 复制非用户名的查询条件
        if (umsBrowseHistory.getProductId() != null) {
            queryWrapper.eq(UmsBrowseHistory::getProductId, umsBrowseHistory.getProductId());
        }
        // 添加其他字段的查询条件...

        // 处理用户名查询条件
        if (StringUtils.isNotEmpty(umsBrowseHistory.getUserName())) {
            LambdaQueryWrapper<UmsUser> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.like(UmsUser::getUserName, umsBrowseHistory.getUserName());
            List<UmsUser> users = umsUserService.list(userQueryWrapper);
            if (!users.isEmpty()) {
                List<String> userIds = users.stream().map(UmsUser::getUserId).collect(Collectors.toList());
                queryWrapper.in(UmsBrowseHistory::getUserId, userIds);
            } else {
                // 如果没找到匹配的用户，返回空结果
                return getDataTable(new ArrayList<>());
            }
        }

        queryWrapper.orderByDesc(UmsBrowseHistory::getBrowseTime);
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
     * 导出浏览记录列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:history:export')")
    @Log(title = "浏览记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UmsBrowseHistory umsBrowseHistory) {
        LambdaQueryWrapper<UmsBrowseHistory> queryWrapper = new LambdaQueryWrapper<>(umsBrowseHistory);
        queryWrapper.orderByDesc(UmsBrowseHistory::getBrowseTime);
        List<UmsBrowseHistory> list = umsBrowseHistoryService.list(queryWrapper);
        ExcelUtil<UmsBrowseHistory> util = new ExcelUtil<UmsBrowseHistory>(UmsBrowseHistory.class);
        util.exportExcel(response, list, "浏览记录数据");
    }

    /**
     * 获取浏览记录详细信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:history:query')")
    @GetMapping(value = "/{id}")
    public AjaxResultResponse<UmsBrowseHistory> getInfo(@PathVariable("id") String id) {
        return AjaxResultResponse.success(umsBrowseHistoryService.getById(id));
    }

    /**
     * 新增浏览记录
     */
    @RequiresPermissions("@ss.hasPermi('ums:history:add')")
    @Log(title = "浏览记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody UmsBrowseHistory umsBrowseHistory) {
        Date date = new Date();
        umsBrowseHistory.setBrowseTime(date);
        return AjaxResultResponse.success(umsBrowseHistoryService.save(umsBrowseHistory));
    }

    /**
     * 修改浏览记录
     */
    @RequiresPermissions("@ss.hasPermi('ums:history:edit')")
    @Log(title = "浏览记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody UmsBrowseHistory umsBrowseHistory) {
        umsBrowseHistory.setBrowseTime(new Date());
        return AjaxResultResponse.success(umsBrowseHistoryService.updateById(umsBrowseHistory));
    }

    /**
     * 删除浏览记录
     */
    @RequiresPermissions("@ss.hasPermi('ums:history:remove')")
    @Log(title = "浏览记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> ids) {
        return AjaxResultResponse.success((umsBrowseHistoryService.removeBatchByIds(ids)));
    }
}
