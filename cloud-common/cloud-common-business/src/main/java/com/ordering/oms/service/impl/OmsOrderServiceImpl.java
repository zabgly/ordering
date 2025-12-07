package com.ordering.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.ordering.oms.mapper.OmsOrderMapper;
import com.ordering.oms.domain.OmsOrder;
import com.ordering.oms.service.IOmsOrderService;

/**
 * 订单Service业务层处理
 * 
 * @author ordering
 * @date 2025-03-15
 */
@Service
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements IOmsOrderService {

}
