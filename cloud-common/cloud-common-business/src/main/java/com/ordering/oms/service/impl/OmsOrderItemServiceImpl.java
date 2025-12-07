package com.ordering.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.ordering.oms.mapper.OmsOrderItemMapper;
import com.ordering.oms.domain.OmsOrderItem;
import com.ordering.oms.service.IOmsOrderItemService;

/**
 * 订单菜品Service业务层处理
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Service
public class OmsOrderItemServiceImpl extends ServiceImpl<OmsOrderItemMapper, OmsOrderItem> implements IOmsOrderItemService {

}
