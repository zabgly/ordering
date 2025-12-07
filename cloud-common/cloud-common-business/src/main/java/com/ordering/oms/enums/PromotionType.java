package com.ordering.oms.enums;

public enum PromotionType {
    ZK(0L, "折扣"),
    YH(1L, "优惠");

    private final Long value;
    private final String label;

    PromotionType(Long value, String label) {
        this.value = value;
        this.label = label;
    }

    public Long getValue() { return value; }
    public String getLabel() { return label; }
}
