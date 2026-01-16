package com.mess.mes_backend.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum TaskStatus {
    LOCKED(0, "Locked"),
    PENDING(1, "Pending"),
    RUNNING(2, "Running"),
    COMPLETED(3, "Completed");

    @EnumValue
    private final int code;
    private final String desc;

    TaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
