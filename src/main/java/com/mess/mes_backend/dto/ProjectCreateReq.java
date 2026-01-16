package com.mess.mes_backend.dto;

import lombok.Data;

@Data
public class ProjectCreateReq {
    private String projectNo;
    private String deviceSn;
    private Long modelId;
}
