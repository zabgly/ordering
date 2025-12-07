package com.ordering.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ordering.ums.domain.UmsUser;
import com.ordering.ums.domain.dto.UmsAppLoginDTO;
import com.ordering.ums.domain.dto.UmsAppRegisterDTO;
import com.ordering.ums.domain.vo.WxLoginVO;

/**
 * 平台用户Service接口
 *
 * @author haiziohhue
 * @date 2024-10-03
 */
public interface IUmsUserService extends IService<UmsUser> {

    UmsUser getUmsUserByUserName(String username);

    Boolean register(UmsAppRegisterDTO umsAppRegisterDTO);

    WxLoginVO appLogin(UmsAppLoginDTO umsAppLoginDTO);

    void loginDataSetRedis(String token, UmsUser user);

    Boolean logout();
}
