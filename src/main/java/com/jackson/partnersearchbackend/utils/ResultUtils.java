package com.jackson.partnersearchbackend.utils;

import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.SuccessCode;

public class ResultUtils {
    public static <T>BaseResponse<T> success(T data, SuccessCode successCode) {
        return new BaseResponse<>(data,successCode);
    }

    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(int errorCode, String message, String description) {
        return new BaseResponse<>(errorCode,null,message,description);
    }

    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.code,null,message,description);
    }
}
