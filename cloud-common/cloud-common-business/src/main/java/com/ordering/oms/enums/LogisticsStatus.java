package com.ordering.oms.enums;

public enum LogisticsStatus {
    UNSHIPPED(0L, "待发货"),
    SHIPPED(1L, "已发货"),
    RECEIVE(2L, "已签收"),
    COMPLETE(3L, "已完成");

    private final Long value;
    private final String label;

    LogisticsStatus(Long value, String label) {
        this.value = value;
        this.label = label;
    }

    public Long getValue() { return value; }
    public String getLabel() { return label; }

    // 通过字典 value 查找枚举
    public static LogisticsStatus fromValue(String value) {
        for (LogisticsStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
