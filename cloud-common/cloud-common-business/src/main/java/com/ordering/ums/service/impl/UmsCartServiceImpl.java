package com.ordering.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.ordering.ums.mapper.UmsCartMapper;
import com.ordering.ums.domain.UmsCart;
import com.ordering.ums.service.IUmsCartService;

/**
 * 我的菜单Service业务层处理
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Service
public class UmsCartServiceImpl extends ServiceImpl<UmsCartMapper, UmsCart> implements IUmsCartService {

}
