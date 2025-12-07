package com.ordering.oms.enums;

public enum OrderStatus {
    UNCOMPLETE(1L, "未完成"),
    COMPLETE(2L, "已完成");

    private final Long value;
    private final String label;

    OrderStatus(Long value, String label) {
        this.value = value;
        this.label = label;
    }

    public Long getValue() { return value; }
    public String getLabel() { return label; }
}
