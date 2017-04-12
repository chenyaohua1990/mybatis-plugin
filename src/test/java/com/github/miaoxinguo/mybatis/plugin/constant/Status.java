package com.github.miaoxinguo.mybatis.plugin.constant;

import com.github.miaoxinguo.mybatis.plugin.EnumInterface;

/**
 *
 */
public enum Status implements EnumInterface {
    ENABLE(1),
    DISABLE(2);

    private int code;

    Status(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    public Status getByCode(int code) {
        for (Status status : Status.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + code + " to " + Status.class.getSimpleName() + " by code.");
    }
}
