package com.mess.mes_backend.dto;

import lombok.Data;

@Data
public class TaskCompleteReq {
    private Long taskId;
    private Long operatorId;
}
