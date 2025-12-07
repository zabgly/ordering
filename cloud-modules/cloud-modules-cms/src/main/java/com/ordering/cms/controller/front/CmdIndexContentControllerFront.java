package com.ordering.cms.controller.front;

import com.ordering.cms.domain.CmdIndexContent;
import com.ordering.cms.service.ICmdIndexContentService;
import com.ordering.common.core.web.controller.BaseController;
import com.ordering.ums.domain.AjaxResultResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 首页内容Controller
 */
@RestController
@RequestMapping("/front/content")
public class CmdIndexContentControllerFront extends BaseController {
    @Resource
    private ICmdIndexContentService cmdIndexContentService;

    /**
     * 获取首页内容详细信息
     */
    @GetMapping
    public AjaxResultResponse<CmdIndexContent> getInfo() {
        return AjaxResultResponse.success(cmdIndexContentService.getById(1));
    }


}
