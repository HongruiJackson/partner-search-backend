package com.jackson.partnersearchbackend.enums;

public enum ErrorCode {
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"数据为空",""),
    DB_ERROR(40002,"数据库出错",""),
    NO_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),

    FORBIDDEN(40301, "禁止操作", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");


    ;
    public final int code;

    public final String message;

    public final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
