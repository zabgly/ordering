package com.ordering.common.security.interceptor;

import com.ordering.common.core.constant.SecurityConstants;
import com.ordering.common.core.context.SecurityContextHolder;
import com.ordering.common.core.utils.ServletUtils;
import com.ordering.common.core.utils.StringUtils;
import com.ordering.common.redis.service.RedisService;
import com.ordering.common.security.auth.AuthUtil;
import com.ordering.common.security.utils.SecurityUtils;
import com.ordering.system.api.model.LoginUser;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author cloud
 */
@Component
public class HeaderInterceptor implements AsyncHandlerInterceptor
{
    @Resource
    private RedisService redisService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String frontAuth = request.getHeader("frontAuth");
        if (StringUtils.isNotBlank(frontAuth)) {
            //前台登录数据
            String userId = request.getHeader("userId");
            String userName = request.getHeader("userName");
            if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(userName)) {
                SecurityContextHolder.setFrontUserId(userId);
                SecurityContextHolder.setFrontUserName(userName);
            }
        }else{
            //后台登录数据
            SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
            SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
            SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));

            String token = SecurityUtils.getToken();
            if (StringUtils.isNotEmpty(token)) {
                LoginUser loginUser = AuthUtil.getLoginUser(token);
                if (StringUtils.isNotNull(loginUser)) {
                    AuthUtil.verifyLoginUserExpire(loginUser);
                    SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContextHolder.remove();
    }
}
