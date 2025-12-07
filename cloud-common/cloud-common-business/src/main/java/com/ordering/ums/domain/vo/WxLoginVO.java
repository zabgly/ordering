package com.ordering.ums.domain.vo;


import com.ordering.ums.domain.UmsUser;
import lombok.Data;

@Data
public class WxLoginVO {
    //登录token
    private String token;
    //用户名
    private String userName;
    //头像信息
    private String userAvatar;
    //登录人的用户信息
    private UmsUser user;
}
