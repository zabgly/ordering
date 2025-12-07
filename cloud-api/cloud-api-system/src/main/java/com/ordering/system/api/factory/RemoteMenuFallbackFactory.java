package com.ordering.system.api.factory;

import com.ordering.common.core.domain.R;
import com.ordering.system.api.RemoteMenuService;
import com.ordering.system.api.domain.MenuDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件服务降级处理
 * 
 * @author cloud
 */
@Component
public class RemoteMenuFallbackFactory implements FallbackFactory<RemoteMenuService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteMenuFallbackFactory.class);

    @Override
    public RemoteMenuService create(Throwable throwable)
    {
        log.error("菜品服务调用失败:{}", throwable.getMessage());
        return new RemoteMenuService()
        {
            @Override
            public R<List<MenuDict>> allList()
            {
                return R.fail("菜品服务allList调用失败:" + throwable.getMessage());
            }

            @Override
            public R<MenuDict> byId(Integer id) {
                return R.fail("菜品服务byId调用失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> placeOrderSubMenuInventory(Integer menuDictId, Integer orderNum) {
                return R.fail("下单扣除库存失败:" + throwable.getMessage());
            }
        };
    }
}
