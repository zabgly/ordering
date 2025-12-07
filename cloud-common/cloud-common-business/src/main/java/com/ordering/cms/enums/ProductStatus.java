package com.ordering.cms.enums;

public enum ProductStatus {
    UNSOLD("1", "在售"),
    SOLD("2", "售罄");

    private final String value;
    private final String label;

    ProductStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return value; }
    public String getLabel() { return label; }

    // 通过字典 value 查找枚举
    public static ProductStatus fromValue(String value) {
        for (ProductStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
