package com.mess.mes_backend.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum TaskStatus {
    LOCKED(0, "锁定"),
    PENDING(1, "待办"),
    RUNNING(2, "进行中"),
    COMPLETED(3, "完成");

    @EnumValue
    private final int code;
    private final String desc;

    TaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
