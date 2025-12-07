package com.ordering.cms.enums;

public enum PromotionType {
    DISCOUNT("0", "折扣"),
    COUPON("1", "优惠");

    private final String value;
    private final String label;

    PromotionType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }

    // 通过字典 value 查找枚举
    public static PromotionType fromValue(String value) {
        for (PromotionType status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
