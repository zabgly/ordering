package com.ordering.oms.enums;

public enum PayType {
    WX("1", "微信支付"),
    ZFB("2", "支付宝支付");

    private final String value;
    private final String label;

    PayType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }

    // 通过字典 value 查找枚举
    public static PayType fromValue(String value) {
        for (PayType status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
