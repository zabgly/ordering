package com.ordering.ums.controller.backed;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ordering.common.core.utils.poi.ExcelUtil;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.common.core.web.page.TableDataInfo;
import com.ordering.common.security.annotation.RequiresPermissions;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 用户信息Controller
 * 
 * @author cloud
 * @date 2025-01-11
 */
@RestController
@RequestMapping("/user")
public class UmsUserController extends BaseController {
    @Resource
    private IUmsUserService umsUserService;

    /**
     * 查询用户信息列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(UmsUser umsUser) {
        startPage();
        LambdaQueryWrapper<UmsUser> queryWrapper = new LambdaQueryWrapper<>(umsUser);
        List<UmsUser> list = umsUserService.list(queryWrapper);
        return getDataTable(list);
    }

    /**
     * 导出用户信息列表
     */
    @RequiresPermissions("@ss.hasPermi('ums:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, UmsUser umsUser) {
        LambdaQueryWrapper<UmsUser> queryWrapper = new LambdaQueryWrapper<>(umsUser);
        List<UmsUser> list = umsUserService.list(queryWrapper);
        ExcelUtil<UmsUser> util = new ExcelUtil<UmsUser>(UmsUser.class);
        util.exportExcel(response, list, "用户信息数据");
    }

    /**
     * 获取用户信息详细信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResultResponse<UmsUser> getInfo(@PathVariable("userId") String userId) {
        return AjaxResultResponse.success(umsUserService.getById(userId));
    }

    /**
     * 新增用户信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:user:add')")
    @PostMapping
    public AjaxResultResponse<Boolean> add(@RequestBody UmsUser umsUser) {
        Date date = new Date();
        umsUser.setCreateTime(date);
        umsUser.setUpdateTime(date);
        return AjaxResultResponse.success(umsUserService.save(umsUser));
    }

    /**
     * 修改用户信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:user:edit')")
    @PutMapping
    public AjaxResultResponse<Boolean> edit(@RequestBody UmsUser umsUser) {
        umsUser.setUpdateTime(new Date());
        return AjaxResultResponse.success(umsUserService.updateById(umsUser));
    }

    /**
     * 删除用户信息
     */
    @RequiresPermissions("@ss.hasPermi('ums:user:remove')")
	@DeleteMapping("/{userIds}")
    public AjaxResultResponse<Boolean> remove(@PathVariable List<String> userIds) {
        return AjaxResultResponse.success((umsUserService.removeBatchByIds(userIds)));
    }
}
