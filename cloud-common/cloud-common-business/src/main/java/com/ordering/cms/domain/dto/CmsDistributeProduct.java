package com.ordering.cms.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class CmsDistributeProduct {

    private String promotionId;

    private List<String> productIds;

}
