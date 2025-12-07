package com.ordering.ums.domain;

import com.ordering.common.core.utils.StringUtils;

/**
 * 操作消息提醒
 *
 * @author haiziohhue
 */
public class AjaxResultResponse<T> {

    public static final String DATA_TAG = "data"; // 新增常量

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回内容
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

    /**
     * 状态类型
     */
    public enum Type {
        SUCCESS(0),
        WARN(301),
        ERROR(500);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    public AjaxResultResponse() {
    }

    public AjaxResultResponse(Type type, String msg) {
        this.code = type.value;
        this.msg = msg;
    }

    public AjaxResultResponse(Type type, String msg, T data) {
        this.code = type.value;
        this.msg = msg;
        if (StringUtils.isNotNull(data)) {
            this.data = data;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static AjaxResultResponse<Void> success() {
        return success("操作成功");
    }

    public static <T> AjaxResultResponse<T> success(T data) {
        return success("操作成功", data);
    }

    public static AjaxResultResponse<Void> success(String msg) {
        return success(msg, null);
    }

    public static <T> AjaxResultResponse<T> success(String msg, T data) {
        return new AjaxResultResponse<>(Type.SUCCESS, msg, data);
    }

    public static AjaxResultResponse<Void> warn(String msg) {
        return warn(msg, null);
    }

    public static <T> AjaxResultResponse<T> warn(String msg, T data) {
        return new AjaxResultResponse<>(Type.WARN, msg, data);
    }

    public static AjaxResultResponse<Void> error() {
        return error("操作失败");
    }

    public static AjaxResultResponse<Void> error(String msg) {
        return error(msg, null);
    }

    public static <T> AjaxResultResponse<T> error(String msg, T data) {
        return new AjaxResultResponse<>(Type.ERROR, msg, data);
    }

    public static AjaxResultResponse<Void> error(int code, String msg) {
        AjaxResultResponse<Void> result = new AjaxResultResponse<>(Type.ERROR, msg);
        result.code = code; // 设置自定义状态码
        return result;
    }
}
