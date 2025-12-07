package com.ordering.ums.controller.front;

import cn.hutool.core.util.IdUtil;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.security.utils.FrontSecurityUtils;
import com.ordering.common.security.utils.SecurityUtils;
import com.ordering.ums.domain.AjaxResultResponse;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.domain.dto.UmsAppLoginDTO;
import com.ordering.ums.domain.dto.UmsAppRegisterDTO;
import com.ordering.ums.domain.vo.WxLoginVO;
import com.ordering.ums.service.IUmsUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 前台-用户注册登录登出模块
 */
//@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
public class UmsUserControllerFront {

    @Resource
    private IUmsUserService umsUserService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public AjaxResultResponse<Boolean> register(@RequestBody UmsAppRegisterDTO umsAppRegisterDTO) {
        String username = umsAppRegisterDTO.getUsername();
        String password = umsAppRegisterDTO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return AjaxResultResponse.error("账号密码不可为空", null);
        }
        if (umsUserService.getUmsUserByUserName(username) != null) {
            return AjaxResultResponse.error("用户名已存在", null);
        }
        UmsUser umsUser = new UmsUser();
        umsUser.setUserId(IdUtil.getSnowflakeNextIdStr());
        umsUser.setUserName(username);
        String encryptPassword = SecurityUtils.encryptPassword(password);
        umsUser.setUserPassword(encryptPassword);
        umsUser.setUserBalance(new BigDecimal("0"));
        Date date = new Date();
        umsUser.setCreateTime(date);
        umsUser.setUpdateTime(date);
        return AjaxResultResponse.success(umsUserService.save(umsUser));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public AjaxResultResponse<WxLoginVO> login(@RequestBody UmsAppLoginDTO umsAppLoginDTO) {
        String username = umsAppLoginDTO.getUsername();
        String password = umsAppLoginDTO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return AjaxResultResponse.error("账号密码不可为空", null);
        }
        umsAppLoginDTO.setUsername(username);
        umsAppLoginDTO.setPassword(password);
        return AjaxResultResponse.success(umsUserService.appLogin(umsAppLoginDTO));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public AjaxResultResponse<Boolean> logout(){
        return AjaxResultResponse.success(umsUserService.logout());
    }

    /**
     * 登录后查看个人信息
     */
    @GetMapping("/getById")
    public AjaxResultResponse<UmsUser> getById(){
        return AjaxResultResponse.success(umsUserService.getById(FrontSecurityUtils.getUserId()));
    }

    /**
     * 登录后更改个人信息
     */
    @PostMapping("/updatePersonalData")
    public AjaxResultResponse<Boolean> updatePersonalData(@RequestBody UmsUser umsUser) {
        umsUser.setUserId(FrontSecurityUtils.getUserId());
        return AjaxResultResponse.success(umsUserService.updateById(umsUser));
    }


}
