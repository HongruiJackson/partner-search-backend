package com.jackson.partnersearchbackend.enums;

import java.util.Arrays;

/**
 * 队伍状态枚举类
 */
public enum TeamStatusEnum {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密")
    ;

    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (TeamStatusEnum teamStatusEnum : TeamStatusEnum.values()) {
            if (teamStatusEnum.getValue() == value) return teamStatusEnum;
        }
        return null;

    }

    private int value;

    private String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
