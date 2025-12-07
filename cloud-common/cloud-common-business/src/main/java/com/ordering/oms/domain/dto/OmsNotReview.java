package com.ordering.oms.domain.dto;

import com.ordering.cms.domain.CmsProduct;
import lombok.Data;

import java.util.List;

@Data
public class OmsNotReview {

    private String orderId;

    private List<CmsProduct> cmsProductList;
}
