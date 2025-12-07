package com.ordering.common.security.utils;

import com.ordering.common.core.context.SecurityContextHolder;
import com.ordering.common.core.exception.ServiceException;

/**
 * 权限获取工具类-获取前台用户信息
 */
public class FrontSecurityUtils
{
    /**
     * 获取前台用户ID
     */
    public static String getUserId() {
        Long frontUserId = SecurityContextHolder.getFrontUserId();
        if (frontUserId != null){
            if (frontUserId.equals(0L)){
                throw new ServiceException("请登录", 401);
            }
            return frontUserId.toString();
        }
        throw new ServiceException("获取前台用户Id失败，请重新登录", 401);
    }

    /**
     * 获取前台用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getFrontUserName();
    }

}
