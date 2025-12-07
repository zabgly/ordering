package com.ordering.system.api;

import com.ordering.common.core.constant.SecurityConstants;
import com.ordering.common.core.constant.ServiceNameConstants;
import com.ordering.common.core.domain.R;
import com.ordering.system.api.domain.SysUser;
import com.ordering.system.api.factory.RemoteUserFallbackFactory;
import com.ordering.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户服务
 * 
 * @author cloud
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 下单后结算金额
     *
     * @param totalAmount 总金额
     * @return 结果
     */
    @GetMapping("/user/payPlaceOrderAmount")
    public R<Boolean> payPlaceOrderAmount(@RequestParam("totalAmount") BigDecimal totalAmount);
}
