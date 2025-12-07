package com.ordering.ums.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 平台用户表
 */
@Data
public class UmsUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户id */
    private Long userId;

    /** 用户头像 */
    private String userAvatar;

    /** 用户密码 */
    private String userPassword;

    /** 用户手机号 */
    private String userPhone;

    /** 用户性别 */
    private String userGender;



}

