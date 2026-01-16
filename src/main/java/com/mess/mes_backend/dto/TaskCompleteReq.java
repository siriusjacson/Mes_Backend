package com.mess.mes_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCompleteReq {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    @NotNull(message = "操作员ID不能为空")
    private Long operatorId;
}
